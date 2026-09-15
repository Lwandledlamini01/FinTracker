package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM pending_tasks ORDER BY id DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM pending_tasks WHERE completed = 0 ORDER BY id DESC")
  fun getPendingTasks(): Flow<List<TaskEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskEntity>)

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Query("UPDATE pending_tasks SET completed = 1 WHERE id = :id")
  suspend fun markCompleted(id: Long)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  @Query("DELETE FROM pending_tasks")
  suspend fun clearAll()
}
