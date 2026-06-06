package com.example.data.model

data class CastingParameters(
    val heatCode: String = "",
    val batchNumber: String = "",
    val shift: String = "",
    val carbon: Double = 3.7,
    val silicon: Double = 2.5,
    val manganese: Double = 0.3,
    val phosphorus: Double = 0.025,
    val sulphur: Double = 0.012,
    val chrome: Double = 0.02,
    val copper: Double = 0.4,
    val magnesium: Double = 0.045,
    val pouringTemp: Double = 1410.0,
    val sandMoisture: Double = 3.3,
    val compactability: Double = 40.0,
    val mouldHardness: Double = 85.0,
    val coreHardness: Double = 75.0,
    val permeability: Double = 120.0,
    val gcsValue: Double = 1450.0
) {
    companion object {
        val IDEAL = CastingParameters()
        
        // Define ranges for normal quality control values
        val RANGES = mapOf(
            "carbon" to (3.4..4.0),
            "silicon" to (2.0..3.0),
            "manganese" to (0.1..0.5),
            "phosphorus" to (0.0..0.05),
            "sulphur" to (0.0..0.02),
            "chrome" to (0.0..0.05),
            "copper" to (0.1..0.8),
            "magnesium" to (0.035..0.055),
            "pouringTemp" to (1380.0..1440.0),
            "sandMoisture" to (2.8..3.8),
            "compactability" to (35.0..45.0),
            "mouldHardness" to (75.0..92.0),
            "coreHardness" to (65.0..85.0),
            "permeability" to (90.0..150.0),
            "gcsValue" to (1200.0..1700.0)
        )

        val LABELS = mapOf(
            "carbon" to "Carbon (C) %",
            "silicon" to "Silicon (Si) %",
            "manganese" to "Manganese (Mn) %",
            "phosphorus" to "Phosphorus (P) %",
            "sulphur" to "Sulphur (S) %",
            "chrome" to "Chrome (Cr) %",
            "copper" to "Copper (Cu) %",
            "magnesium" to "Magnesium (Mg) %",
            "pouringTemp" to "Pouring Temp (°C)",
            "sandMoisture" to "Sand Moisture %",
            "compactability" to "Compactability %",
            "mouldHardness" to "Mould Hardness (GF)",
            "coreHardness" to "Core Hardness (GF)",
            "permeability" to "Permeability No.",
            "gcsValue" to "GCS Value (g/cm²)"
        )
        
        fun validateValue(key: String, value: Double): Boolean {
            val range = RANGES[key] ?: return true
            return value in range
        }
    }
}
