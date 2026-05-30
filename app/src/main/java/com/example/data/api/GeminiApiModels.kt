package com.example.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Double? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

// The structure we tell Gemini to return
@JsonClass(generateAdapter = true)
data class CastingPredictionResponse(
    val status: String, // GOOD, CAUTION, HIGH_RISK
    val rejectionProbability: Double, // 0.0 to 100.0
    val primaryRisks: List<String>,
    val defectWarnings: List<String>,
    val recommendations: List<String>
)
