package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AccountDao
import com.example.data.dao.BudgetDao
import com.example.data.dao.GoalDao
import com.example.data.dao.TaskDao
import com.example.data.dao.TransactionDao
import com.example.data.model.AccountEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.GoalEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    TransactionEntity::class,
    AccountEntity::class,
    BudgetEntity::class,
    GoalEntity::class,
    TaskEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class WalletDatabase : RoomDatabase() {
  abstract fun transactionDao(): TransactionDao
  abstract fun accountDao(): AccountDao
  abstract fun budgetDao(): BudgetDao
  abstract fun goalDao(): GoalDao
  abstract fun taskDao(): TaskDao

  companion object {
    @Volatile
    private var INSTANCE: WalletDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): WalletDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          WalletDatabase::class.java,
          "wallet_database"
        )
          .fallbackToDestructiveMigration()
          .addCallback(WalletDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class WalletDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database)
          }
        }
      }
    }

    suspend fun populateInitialData(database: WalletDatabase) {
      val accountDao = database.accountDao()
      val budgetDao = database.budgetDao()
      val transactionDao = database.transactionDao()
      val goalDao = database.goalDao()
      val taskDao = database.taskDao()

      // Seed Accounts
      val accounts = listOf(
        AccountEntity(
          id = 1,
          name = "Main Checking",
          accountNumber = "4291",
          type = "CHECKING",
          balance = 8450.00,
          cardType = "VISA",
          gradientIndex = 0,
          isFrozen = false
        ),
        AccountEntity(
          id = 2,
          name = "High Yield Savings",
          accountNumber = "8823",
          type = "SAVINGS",
          balance = 14200.00,
          cardType = "MASTERCARD",
          gradientIndex = 1,
          isFrozen = false
        ),
        AccountEntity(
          id = 3,
          name = "Investment Portfolio",
          accountNumber = "1042",
          type = "INVESTMENT",
          balance = 6850.00,
          cardType = "VISA",
          gradientIndex = 2,
          isFrozen = false
        ),
        AccountEntity(
          id = 4,
          name = "Daily Cash",
          accountNumber = "9912",
          type = "CASH",
          balance = 750.00,
          cardType = "CASH",
          gradientIndex = 3,
          isFrozen = false
        )
      )
      accountDao.insertAccounts(accounts)

      // Seed Budgets
      val budgets = listOf(
        BudgetEntity(id = 1, category = "food", monthlyLimit = 650.0),
        BudgetEntity(id = 2, category = "essential", monthlyLimit = 1400.0),
        BudgetEntity(id = 3, category = "transport", monthlyLimit = 300.0),
        BudgetEntity(id = 4, category = "shopping", monthlyLimit = 400.0),
        BudgetEntity(id = 5, category = "entertainment", monthlyLimit = 250.0),
        BudgetEntity(id = 6, category = "health", monthlyLimit = 200.0)
      )
      budgetDao.insertBudgets(budgets)

      // Seed Goals (e.g. New Car, Vacation, Emergency Fund)
      val goals = listOf(
        GoalEntity(id = 1, title = "New Car", target = 25000.0, current = 14200.0, icon = "🚗"),
        GoalEntity(id = 2, title = "Vacation to Tokyo", target = 5000.0, current = 3850.0, icon = "✈️"),
        GoalEntity(id = 3, title = "Emergency Fund", target = 10000.0, current = 10000.0, icon = "🏠"),
        GoalEntity(id = 4, title = "MacBook Pro M3", target = 2400.0, current = 1800.0, icon = "💻")
      )
      goalDao.insertGoals(goals)

      // Seed Pending Tasks (e.g. Electric Bill, Internet)
      val tasks = listOf(
        TaskEntity(id = 1, text = "Electric Bill", amount = 124.50, dueDate = "Tomorrow", completed = false),
        TaskEntity(id = 2, text = "Internet & WiFi", amount = 79.99, dueDate = "In 3 days", completed = false),
        TaskEntity(id = 3, text = "Auto Insurance", amount = 185.00, dueDate = "Next week", completed = false)
      )
      taskDao.insertTasks(tasks)

      // Seed Transactions matching FinanceFlow's category schema
      val now = System.currentTimeMillis()
      val dayMillis = 86_400_000L
      val transactions = listOf(
        TransactionEntity(
          id = 1,
          title = "Tech Corp Salary",
          amount = 4250.00,
          type = "INCOME",
          category = "essential",
          accountId = 1,
          timestamp = now - (dayMillis * 1) - 10_000_000,
          note = "Bi-weekly direct deposit"
        ),
        TransactionEntity(
          id = 2,
          title = "Freelance Mobile Design",
          amount = 1148.25,
          type = "INCOME",
          category = "other",
          accountId = 1,
          timestamp = now - (dayMillis * 2) - 20_000_000,
          note = "Milestone payout"
        ),
        TransactionEntity(
          id = 3,
          title = "Whole Foods Market",
          amount = 128.50,
          type = "EXPENSE",
          category = "food",
          accountId = 1,
          timestamp = now - 3_600_000,
          note = "Weekly groceries"
        ),
        TransactionEntity(
          id = 4,
          title = "Equinox Fitness Club",
          amount = 85.00,
          type = "EXPENSE",
          category = "health",
          accountId = 1,
          timestamp = now - 14_400_000,
          note = "Monthly gym membership"
        ),
        TransactionEntity(
          id = 5,
          title = "Uber Premier Ride",
          amount = 34.20,
          type = "EXPENSE",
          category = "transport",
          accountId = 1,
          timestamp = now - (dayMillis * 2) - 30_000_000,
          note = "Airport transfer"
        ),
        TransactionEntity(
          id = 6,
          title = "Apple Store Online",
          amount = 299.00,
          type = "EXPENSE",
          category = "shopping",
          accountId = 1,
          timestamp = now - (dayMillis * 3) - 15_000_000,
          note = "AirPods Pro"
        ),
        TransactionEntity(
          id = 7,
          title = "Blue Bottle Coffee",
          amount = 6.75,
          type = "EXPENSE",
          category = "food",
          accountId = 4,
          timestamp = now - (dayMillis * 3) - 25_000_000,
          note = "Morning iced latte"
        ),
        TransactionEntity(
          id = 8,
          title = "Spotify Premium Duo",
          amount = 16.99,
          type = "EXPENSE",
          category = "entertainment",
          accountId = 1,
          timestamp = now - (dayMillis * 4) - 12_000_000,
          note = "Music subscription"
        ),
        TransactionEntity(
          id = 9,
          title = "Clean Energy Electric Bill",
          amount = 112.40,
          type = "EXPENSE",
          category = "essential",
          accountId = 1,
          timestamp = now - (dayMillis * 5) - 18_000_000,
          note = "Monthly power utility"
        ),
        TransactionEntity(
          id = 10,
          title = "Trader Joe's Groceries",
          amount = 74.30,
          type = "EXPENSE",
          category = "food",
          accountId = 4,
          timestamp = now - (dayMillis * 6) - 16_000_000,
          note = "Healthy snacks"
        )
      )
      transactionDao.insertTransactions(transactions)
    }
  }
}
