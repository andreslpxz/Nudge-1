package com.example.nudge.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: Int = 1, // We only have one goal for now
    val name: String,
    val targetAmount: Double,
    val currentSaved: Double
)
