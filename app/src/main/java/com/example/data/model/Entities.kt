package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val amount: Double,
  val type: String, // "EXPENSE", "INCOME"
  val category: String,
  val accountId: Long,
  val timestamp: Long,
  val note: String = ""
)

@Entity(tableName = "accounts")
data class AccountEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val accountNumber: String, // Last 4 digits
  val type: String, // "CHECKING", "SAVINGS", "INVESTMENT", "CASH"
  val balance: Double,
  val cardType: String = "VISA",
  val gradientIndex: Int = 0,
  val isFrozen: Boolean = false
)

@Entity(tableName = "budgets")
data class BudgetEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val category: String,
  val monthlyLimit: Double
)
