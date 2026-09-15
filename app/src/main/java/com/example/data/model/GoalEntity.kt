package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class GoalEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val target: Double,
  val current: Double = 0.0,
  val icon: String = "📱",
  val color: String = "#34C759"
)
