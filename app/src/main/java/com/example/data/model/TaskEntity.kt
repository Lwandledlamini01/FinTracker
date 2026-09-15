package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val text: String,
  val amount: Double,
  val dueDate: String,
  val completed: Boolean = false
)
