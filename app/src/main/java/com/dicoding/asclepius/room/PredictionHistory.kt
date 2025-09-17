package com.dicoding.asclepius.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class PredictionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String,
    val prediction: Boolean,
    val confidenceScore: Float,
    val createdAt: Long = System.currentTimeMillis()
)
