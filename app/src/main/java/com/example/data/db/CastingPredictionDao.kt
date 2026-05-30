package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CastingPredictionDao {
    @Query("SELECT * FROM casting_predictions ORDER BY timestamp DESC")
    fun getAllPredictions(): Flow<List<CastingPrediction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: CastingPrediction): Long

    @Query("DELETE FROM casting_predictions WHERE id = :id")
    suspend fun deletePredictionById(id: Int)

    @Query("DELETE FROM casting_predictions")
    suspend fun clearAllPredictions()

    @Query("UPDATE casting_predictions SET userRating = :rating, userCorrections = :corrections WHERE id = :id")
    suspend fun updateFeedback(id: Int, rating: String, corrections: String)

    @Query("SELECT * FROM casting_predictions WHERE userRating = 'INCORRECT' AND userCorrections != '' ORDER BY timestamp DESC LIMIT 5")
    suspend fun getCorrectionRecords(): List<CastingPrediction>
}
