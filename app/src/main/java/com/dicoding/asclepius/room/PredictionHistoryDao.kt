package com.dicoding.asclepius.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PredictionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PredictionHistory)

    @Query("SELECT * FROM prediction_history ORDER BY id DESC")
    suspend fun getAllHistory(): List<PredictionHistory>

    @Query("DELETE FROM prediction_history WHERE id = :historyId")
    suspend fun deleteHistoryById(historyId: Int)

    @Query("DELETE FROM prediction_history WHERE imageUri = :imageUri")
    suspend fun deleteHistoryByImageUri(imageUri: String)

    @Query("SELECT * FROM prediction_history WHERE imageUri = :imageUri LIMIT 1")
    suspend fun getHistoryByImageUri(imageUri: String): PredictionHistory?
}

