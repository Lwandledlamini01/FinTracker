package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
  @Query("SELECT * FROM savings_goals ORDER BY id ASC")
  fun getAllGoals(): Flow<List<GoalEntity>>

  @Query("SELECT * FROM savings_goals WHERE id = :id LIMIT 1")
  suspend fun getGoalById(id: Long): GoalEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGoal(goal: GoalEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGoals(goals: List<GoalEntity>)

  @Update
  suspend fun updateGoal(goal: GoalEntity)

  @Query("UPDATE savings_goals SET current = current + :amount WHERE id = :id")
  suspend fun addFunds(id: Long, amount: Double)

  @Delete
  suspend fun deleteGoal(goal: GoalEntity)

  @Query("DELETE FROM savings_goals")
  suspend fun clearAll()
}
