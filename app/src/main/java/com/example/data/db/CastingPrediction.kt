package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CastingParameters

@Entity(tableName = "casting_predictions")
data class CastingPrediction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    
    // Heat ID / Batch No
    val heatCode: String = "",
    val batchNumber: String = "",
    val shift: String = "",
    
    // Chemical Composition inputs
    val carbon: Double,
    val silicon: Double,
    val manganese: Double,
    val phosphorus: Double,
    val sulphur: Double,
    val chrome: Double,
    val copper: Double,
    val magnesium: Double,
    
    // Process input
    val pouringTemp: Double,
    
    // Sand parameters inputs
    val sandMoisture: Double,
    val compactability: Double,
    val mouldHardness: Double,
    val coreHardness: Double,
    val permeability: Double,
    val gcsValue: Double,
    
    // Prediction Output from AI
    val status: String, // GOOD / CAUTION / HIGH_RISK
    val rejectionProbability: Double, // 0.0 to 100.0
    val primaryRisks: String, // Semi-colon separated primary risk items
    val defectWarnings: String, // Semi-colon separated potential defects predicted
    val recommendations: String, // Bulleted recommendations to fix
    
    // AI self-learning reinforcement fields 
    val userRating: String = "NONE", // NONE, CORRECT, INCORRECT
    val userCorrections: String = "" // Actual physical inspection findings entered by engineer
) {
    fun toParameters(): CastingParameters {
        return CastingParameters(
            heatCode = heatCode,
            batchNumber = batchNumber,
            shift = shift,
            carbon = carbon,
            silicon = silicon,
            manganese = manganese,
            phosphorus = phosphorus,
            sulphur = sulphur,
            chrome = chrome,
            copper = copper,
            magnesium = magnesium,
            pouringTemp = pouringTemp,
            sandMoisture = sandMoisture,
            compactability = compactability,
            mouldHardness = mouldHardness,
            coreHardness = coreHardness,
            permeability = permeability,
            gcsValue = gcsValue
        )
    }
}
