package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.api.CastingPredictionResponse
import com.example.data.db.CastingPredictionDao
import com.example.data.db.CastingPrediction
import com.example.data.model.CastingParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class CastingRepository(private val predictionDao: CastingPredictionDao) {

    val allPredictions: Flow<List<CastingPrediction>> = predictionDao.getAllPredictions()

    suspend fun insertPrediction(prediction: CastingPrediction): Int {
        return withContext(Dispatchers.IO) {
            predictionDao.insertPrediction(prediction).toInt()
        }
    }

    suspend fun deletePrediction(id: Int) {
        withContext(Dispatchers.IO) {
            predictionDao.deletePredictionById(id)
        }
    }

    suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            predictionDao.clearAllPredictions()
        }
    }

    suspend fun saveFeedback(id: Int, rating: String, corrections: String) {
        withContext(Dispatchers.IO) {
            predictionDao.updateFeedback(id, rating, corrections)
        }
    }

    @Suppress("LongMethod")
    suspend fun predictRejection(params: CastingParameters): CastingPredictionResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw IllegalStateException("Gemini API key is not configured in the Secrets panel. Please add GEMINI_API_KEY to your secrets.")
        }

        // Fetch user corrected samples for In-Context learning
        val manualCorrections = predictionDao.getCorrectionRecords()
        var learningExamples = ""
        if (manualCorrections.isNotEmpty()) {
            learningExamples = "\n\nCRITICAL CONTEXT (AI SELF-LEARNING REINFORCEMENT ON PREVIOUS MISTAKES):\n" +
                "Below are real physical inspect outcomes manually corrected by the foundry engineer (Rohit Chouhan) because the AI predicted incorrectly. Use this feedback to learn and adjust your metallurgical predictions:\n" +
                manualCorrections.joinToString("\n") { record ->
                    "• Case: [Heat:${record.heatCode}, C:${record.carbon}%, Si:${record.silicon}%, Mg:${record.magnesium}%, PourTemp:${record.pouringTemp}°C, SandMoisture:${record.sandMoisture}%]\n" +
                    "  - AI previous estimate was: Status ${record.status} with ${record.rejectionProbability}% probability.\n" +
                    "  - Correct Groundtruth Observation: ${record.userCorrections}\n"
                } + "\nMaintain alignment with these real ground-truth outcomes when analyzing new parameters!"
        }

        val prompt = """
            SGI (Spheroidal Graphite Iron / Ductile Iron) casting process parameters:
            
            Heat Code: ${if (params.heatCode.isBlank()) "N/A" else params.heatCode}
            Batch / Heat Entry number: ${if (params.batchNumber.isBlank()) "N/A" else params.batchNumber}
            Shift: ${if (params.shift.isBlank()) "N/A" else params.shift}
            
            Chemical Composition:
            - Carbon (C): ${params.carbon}% (Target: 3.4% - 4.0%)
            - Silicon (Si): ${params.silicon}% (Target: 2.0% - 3.0%)
            - Manganese (Mn): ${params.manganese}% (Target: 0.1% - 0.5%)
            - Phosphorus (P): ${params.phosphorus}% (Target: 0.0% - 0.05%)
            - Sulphur (S): ${params.sulphur}% (Target: 0.0% - 0.02%)
            - Chrome (Cr): ${params.chrome}% (Target: 0.0% - 0.05%)
            - Copper (Cu): ${params.copper}% (Target: 0.1% - 0.8%)
            - Magnesium (Mg): ${params.magnesium}% (Target: 0.035% - 0.055%)
            
            Thermal / Flow Parameters:
            - Pouring Temperature: ${params.pouringTemp}°C (Target: 1380°C - 1440°C)
            
            Green Sand Mould Parameters:
            - Sand Moisture: ${params.sandMoisture}% (Target: 2.8% - 3.8%)
            - Compactability: ${params.compactability}% (Target: 35.0% - 45.0%)
            - Mould Hardness: ${params.mouldHardness} GF (Target: 75.0 - 92.0)
            - Core Hardness: ${params.coreHardness} GF (Target: 65.0 - 85.0)
            - Permeability: ${params.permeability} (Target: 90.0 - 150.0)
            - GCS Value (Green Compression Strength): ${params.gcsValue} g/cm² (Target: 1200.0 - 1700.0)
            
            Perform a professional foundry metallurgical and sand control review of these parameters.
            Calculate the casting rejection probability (%) based on chemical imbalances, temperature issues, and sand deficiencies.
        """.trimIndent()

        val systemInstruction = """
            You are an expert foundry metallurgical engineer who specializes in Ductile Iron (SGS/SGI) casting and high-pressure moulding sand control.
            
            Analyze the input parameters and return a strict JSON response. Do not include any explanation or markdown formatting outside of JSON.
            
            The JSON schema MUST be:
            {
               "status": "GOOD" | "CAUTION" | "HIGH_RISK",
               "rejectionProbability": 0.0 to 100.0 (as a double),
               "primaryRisks": ["string listing specific chemical or sand deviations"],
               "defectWarnings": ["potential casting defects like Blowholes, Pinholes, Shrinkage Cavity, Chill/Carbides, Sand Wash, Mould Wall Expansion/Swell, Dross, Cold Shut, Misrun, Mismatch, etc."],
               "recommendations": ["bullet points of physical corrective actions, e.g. increase Mg inoculation, add bentonite, decrease moisture, adjust pouring temp"]
            }
            
            Be very precise. For example:
            - High moisture + Low permeability -> High risk of blowholes/pinholes.
            - Low Carbon/Silicon -> Risk of solidification shrinkage cavity/chilled edges.
            - Low Magnesium (<0.03%) -> Fail to nodularize graphite (flake graphite / unspheroidized), high rejection!
            - High Magnesium (>0.06%) -> Dross, pinholes, and micro-shrinkage.
            - Low Pouring Temp (<1360°C) -> Cold shuts, misruns, slag inclusion.
            - High sand moisture (>4.0%) -> Slag, gas, steam dross, pinhole.
            - High Chrome or low Carbon -> Chilled hard spots, difficult machining.
            - Low Mould hardness -> Mould deformation, shrinkage cavity due to lack of mold rigidity.
            
            Respond ONLY in the JSON format requested.$learningExamples
            
            Respond ONLY in the JSON format requested.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2
            ),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        val response = RetrofitClient.service.generateContent(apiKey, request)
        val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw IllegalStateException("Could not generate prediction from Gemini API.")

        RetrofitClient.predictionAdapter.fromJson(textResponse)
            ?: throw IllegalStateException("Failed to parse Gemini response: $textResponse")
    }
}
