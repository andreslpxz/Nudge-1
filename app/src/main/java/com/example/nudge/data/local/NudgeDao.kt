package com.example.nudge.data.local

import androidx.room.*
import com.example.nudge.data.models.ChatMessage
import com.example.nudge.data.models.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface NudgeDao {
    @Query("SELECT * FROM goals WHERE id = 1")
    fun getGoal(): Flow<Goal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert
    suspend fun insertMessage(message: ChatMessage)
}
