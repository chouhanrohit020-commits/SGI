package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.CastingPrediction
import com.example.data.model.CastingParameters
import com.example.data.repository.CastingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed interface PredictionUiState {
    object Idle : PredictionUiState
    object Loading : PredictionUiState
    data class Success(
        val id: Int,
        val parameters: CastingParameters,
        val status: String,
        val probability: Double,
        val risks: List<String>,
        val defects: List<String>,
        val recommendations: List<String>,
        val initialRating: String,
        val initialCorrections: String
    ) : PredictionUiState
    data class Error(val message: String) : PredictionUiState
}

class CastingViewModel(private val repository: CastingRepository) : ViewModel() {

    // Reactive flow of saved historical predictions
    val predictionHistory: StateFlow<List<CastingPrediction>> = repository.allPredictions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Form inputs state mapped by parameter key
    private val _inputFields = MutableStateFlow<Map<String, String>>(emptyMap())
    val inputFields: StateFlow<Map<String, String>> = _inputFields.asStateFlow()

    // Prediction workflow state
    private val _uiState = MutableStateFlow<PredictionUiState>(PredictionUiState.Idle)
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    init {
        loadIdealValues()
    }

    fun updateField(key: String, value: String) {
        val current = _inputFields.value.toMutableMap()
        current[key] = value
        _inputFields.value = current
    }

    companion object {
        fun generateSystemHeatCode(): String {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) // Jan Offset=0, April Offset=3
            
            // Starts April as 'A' (offset 0), May as 'B' (offset 1), ..., March as 'L' (offset 11)
            val offset = (month - Calendar.APRIL + 12) % 12
            val letter = ('A'.code + offset).toChar()
            
            val yearStr = SimpleDateFormat("yy", Locale.getDefault()).format(calendar.time)
            return "$day$letter$yearStr"
        }
    }

    fun loadIdealValues() {
        val ideal = CastingParameters.IDEAL
        // Generate heat code using Rohit's special formula
        val currentHeatCode = generateSystemHeatCode()
        _inputFields.value = mapOf(
            "heatCode" to currentHeatCode,
            "carbon" to ideal.carbon.toString(),
            "silicon" to ideal.silicon.toString(),
            "manganese" to ideal.manganese.toString(),
            "phosphorus" to ideal.phosphorus.toString(),
            "sulphur" to ideal.sulphur.toString(),
            "chrome" to ideal.chrome.toString(),
            "copper" to ideal.copper.toString(),
            "magnesium" to ideal.magnesium.toString(),
            "pouringTemp" to ideal.pouringTemp.toString(),
            "sandMoisture" to ideal.sandMoisture.toString(),
            "compactability" to ideal.compactability.toString(),
            "mouldHardness" to ideal.mouldHardness.toString(),
            "coreHardness" to ideal.coreHardness.toString(),
            "permeability" to ideal.permeability.toString(),
            "gcsValue" to ideal.gcsValue.toString()
        )
        resetUiState()
    }

    fun clearFields() {
        _inputFields.value = mapOf(
            "heatCode" to "",
            "carbon" to "", "silicon" to "", "manganese" to "", "phosphorus" to "", "sulphur" to "",
            "chrome" to "", "copper" to "", "magnesium" to "", "pouringTemp" to "", "sandMoisture" to "",
            "compactability" to "", "mouldHardness" to "", "coreHardness" to "", "permeability" to "", "gcsValue" to ""
        )
        resetUiState()
    }

    fun resetUiState() {
        _uiState.value = PredictionUiState.Idle
    }

    fun loadPredictionRecord(record: CastingPrediction) {
        _inputFields.value = mapOf(
            "heatCode" to record.heatCode,
            "carbon" to record.carbon.toString(),
            "silicon" to record.silicon.toString(),
            "manganese" to record.manganese.toString(),
            "phosphorus" to record.phosphorus.toString(),
            "sulphur" to record.sulphur.toString(),
            "chrome" to record.chrome.toString(),
            "copper" to record.copper.toString(),
            "magnesium" to record.magnesium.toString(),
            "pouringTemp" to record.pouringTemp.toString(),
            "sandMoisture" to record.sandMoisture.toString(),
            "compactability" to record.compactability.toString(),
            "mouldHardness" to record.mouldHardness.toString(),
            "coreHardness" to record.coreHardness.toString(),
            "permeability" to record.permeability.toString(),
            "gcsValue" to record.gcsValue.toString()
        )
        
        _uiState.value = PredictionUiState.Success(
            id = record.id,
            parameters = record.toParameters(),
            status = record.status,
            probability = record.rejectionProbability,
            risks = if (record.primaryRisks.isBlank()) emptyList() else record.primaryRisks.split(";"),
            defects = if (record.defectWarnings.isBlank()) emptyList() else record.defectWarnings.split(";"),
            recommendations = if (record.recommendations.isBlank()) emptyList() else record.recommendations.split(";"),
            initialRating = record.userRating,
            initialCorrections = record.userCorrections
        )
    }

    fun submitFeedback(id: Int, rating: String, corrections: String) {
        viewModelScope.launch {
            repository.saveFeedback(id, rating, corrections)
            val current = _uiState.value
            if (current is PredictionUiState.Success && current.id == id) {
                _uiState.value = current.copy(initialRating = rating, initialCorrections = corrections)
            }
        }
    }

    fun getPrediction() {
        val fields = _inputFields.value
        
        // Parse the parameters safely - use Ideal defaults if user left them empty, to avoid crashes
        val params = CastingParameters(
            heatCode = fields["heatCode"] ?: "",
            carbon = fields["carbon"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.carbon,
            silicon = fields["silicon"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.silicon,
            manganese = fields["manganese"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.manganese,
            phosphorus = fields["phosphorus"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.phosphorus,
            sulphur = fields["sulphur"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.sulphur,
            chrome = fields["chrome"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.chrome,
            copper = fields["copper"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.copper,
            magnesium = fields["magnesium"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.magnesium,
            pouringTemp = fields["pouringTemp"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.pouringTemp,
            sandMoisture = fields["sandMoisture"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.sandMoisture,
            compactability = fields["compactability"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.compactability,
            mouldHardness = fields["mouldHardness"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.mouldHardness,
            coreHardness = fields["coreHardness"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.coreHardness,
            permeability = fields["permeability"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.permeability,
            gcsValue = fields["gcsValue"]?.toDoubleOrNull() ?: CastingParameters.IDEAL.gcsValue
        )

        _uiState.value = PredictionUiState.Loading

        viewModelScope.launch {
            try {
                val apiResponse = repository.predictRejection(params)
                
                // Format lists into DB-friendly semi-colon strings
                val risksStr = apiResponse.primaryRisks.joinToString(";")
                val defectsStr = apiResponse.defectWarnings.joinToString(";")
                val recsStr = apiResponse.recommendations.joinToString(";")

                val predictionRecord = CastingPrediction(
                    heatCode = params.heatCode,
                    carbon = params.carbon,
                    silicon = params.silicon,
                    manganese = params.manganese,
                    phosphorus = params.phosphorus,
                    sulphur = params.sulphur,
                    chrome = params.chrome,
                    copper = params.copper,
                    magnesium = params.magnesium,
                    pouringTemp = params.pouringTemp,
                    sandMoisture = params.sandMoisture,
                    compactability = params.compactability,
                    mouldHardness = params.mouldHardness,
                    coreHardness = params.coreHardness,
                    permeability = params.permeability,
                    gcsValue = params.gcsValue,
                    status = apiResponse.status,
                    rejectionProbability = apiResponse.rejectionProbability,
                    primaryRisks = risksStr,
                    defectWarnings = defectsStr,
                    recommendations = recsStr
                )

                val savedId = repository.insertPrediction(predictionRecord)

                _uiState.value = PredictionUiState.Success(
                    id = savedId,
                    parameters = params,
                    status = apiResponse.status,
                    probability = apiResponse.rejectionProbability,
                    risks = apiResponse.primaryRisks,
                    defects = apiResponse.defectWarnings,
                    recommendations = apiResponse.recommendations,
                    initialRating = "NONE",
                    initialCorrections = ""
                )
            } catch (e: Exception) {
                _uiState.value = PredictionUiState.Error(e.message ?: "An unknown network error occurred")
            }
        }
    }

    fun deleteHistoryRecord(id: Int) {
        viewModelScope.launch {
            repository.deletePrediction(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}

class CastingViewModelFactory(private val repository: CastingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CastingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CastingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
