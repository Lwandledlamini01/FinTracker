package com.example.data.repository

import com.example.data.database.WalletDatabase
import com.example.data.model.AccountEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.GoalEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class WalletRepository(private val database: WalletDatabase) {
  private val transactionDao = database.transactionDao()
  private val accountDao = database.accountDao()
  private val budgetDao = database.budgetDao()
  private val goalDao = database.goalDao()
  private val taskDao = database.taskDao()

  val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
  val allAccounts: Flow<List<AccountEntity>> = accountDao.getAllAccounts()
  val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
  val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
  val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
  val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()

  suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
    val accounts = allAccounts.firstOrNull()
    if (accounts.isNullOrEmpty()) {
      WalletDatabase.populateInitialData(database)
    }
  }

  suspend fun addTransaction(
    title: String,
    amount: Double,
    type: String,
    category: String,
    accountId: Long = 1,
    note: String = "",
    timestamp: Long = System.currentTimeMillis()
  ): Long = withContext(Dispatchers.IO) {
    val transaction = TransactionEntity(
      title = title,
      amount = amount,
      type = type,
      category = category,
      accountId = accountId,
      timestamp = timestamp,
      note = note
    )
    val id = transactionDao.insertTransaction(transaction)

    val account = accountDao.getAccountById(accountId)
    if (account != null) {
      val newBalance = if (type == "INCOME") {
        account.balance + amount
      } else {
        account.balance - amount
      }
      accountDao.updateAccount(account.copy(balance = newBalance))
    }

    id
  }

  suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
    val tx = transactionDao.getTransactionById(id)
    if (tx != null) {
      val account = accountDao.getAccountById(tx.accountId)
      if (account != null) {
        val revertedBalance = if (tx.type == "INCOME") {
          account.balance - tx.amount
        } else {
          account.balance + tx.amount
        }
        accountDao.updateAccount(account.copy(balance = revertedBalance))
      }
      transactionDao.deleteTransaction(tx)
    }
  }

  suspend fun transferFunds(
    fromAccountId: Long,
    toAccountId: Long,
    amount: Double,
    note: String = ""
  ): Boolean = withContext(Dispatchers.IO) {
    val fromAccount = accountDao.getAccountById(fromAccountId) ?: return@withContext false
    val toAccount = accountDao.getAccountById(toAccountId) ?: return@withContext false

    if (fromAccount.balance < amount) return@withContext false

    accountDao.updateAccount(fromAccount.copy(balance = fromAccount.balance - amount))
    accountDao.updateAccount(toAccount.copy(balance = toAccount.balance + amount))

    val now = System.currentTimeMillis()
    transactionDao.insertTransaction(
      TransactionEntity(
        title = "Transfer to ${toAccount.name}",
        amount = amount,
        type = "EXPENSE",
        category = "Transfer",
        accountId = fromAccountId,
        timestamp = now,
        note = note
      )
    )
    transactionDao.insertTransaction(
      TransactionEntity(
        title = "Transfer from ${fromAccount.name}",
        amount = amount,
        type = "INCOME",
        category = "Transfer",
        accountId = toAccountId,
        timestamp = now,
        note = note
      )
    )

    true
  }

  suspend fun toggleFreezeAccount(accountId: Long) = withContext(Dispatchers.IO) {
    val account = accountDao.getAccountById(accountId)
    if (account != null) {
      accountDao.updateAccount(account.copy(isFrozen = !account.isFrozen))
    }
  }

  suspend fun upsertBudget(category: String, monthlyLimit: Double) = withContext(Dispatchers.IO) {
    val existing = budgetDao.getBudgetByCategory(category)
    if (existing != null) {
      budgetDao.updateBudget(existing.copy(monthlyLimit = monthlyLimit))
    } else {
      budgetDao.insertBudget(BudgetEntity(category = category, monthlyLimit = monthlyLimit))
    }
  }

  suspend fun addGoal(title: String, target: Double, icon: String) = withContext(Dispatchers.IO) {
    goalDao.insertGoal(
      GoalEntity(
        title = title,
        target = target,
        current = 0.0,
        icon = icon
      )
    )
  }

  suspend fun contributeToGoal(goalId: Long, amount: Double): Boolean = withContext(Dispatchers.IO) {
    val goal = goalDao.getGoalById(goalId) ?: return@withContext false
    val accounts = allAccounts.firstOrNull() ?: emptyList()
    val mainAccount = accounts.firstOrNull() ?: return@withContext false

    goalDao.addFunds(goalId, amount)
    accountDao.updateAccount(mainAccount.copy(balance = mainAccount.balance - amount))

    transactionDao.insertTransaction(
      TransactionEntity(
        title = "Saved: ${goal.title}",
        amount = amount,
        type = "EXPENSE",
        category = "other",
        accountId = mainAccount.id,
        timestamp = System.currentTimeMillis(),
        note = "Contribution to savings goal"
      )
    )

    true
  }

  suspend fun addTask(text: String, amount: Double, dueDate: String) = withContext(Dispatchers.IO) {
    taskDao.insertTask(
      TaskEntity(
        text = text,
        amount = amount,
        dueDate = dueDate,
        completed = false
      )
    )
  }

  suspend fun markTaskDone(taskId: Long) = withContext(Dispatchers.IO) {
    taskDao.markCompleted(taskId)
  }

  suspend fun payTask(task: TaskEntity) = withContext(Dispatchers.IO) {
    taskDao.markCompleted(task.id)
    val accounts = allAccounts.firstOrNull() ?: emptyList()
    val mainAccount = accounts.firstOrNull()
    val accId = mainAccount?.id ?: 1L

    if (mainAccount != null) {
      accountDao.updateAccount(mainAccount.copy(balance = mainAccount.balance - task.amount))
    }

    transactionDao.insertTransaction(
      TransactionEntity(
        title = "Paid: ${task.text}",
        amount = task.amount,
        type = "EXPENSE",
        category = "essential",
        accountId = accId,
        timestamp = System.currentTimeMillis(),
        note = "Completed pending bill/task"
      )
    )
  }

  suspend fun resetAllData() = withContext(Dispatchers.IO) {
    database.clearAllTables()
    WalletDatabase.populateInitialData(database)
  }
}
