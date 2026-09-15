package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AccountEntity
import com.example.data.model.BudgetEntity
import com.example.data.model.GoalEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class UserProfile(
  val name: String = "Alex Morgan",
  val email: String = "alex.morgan@example.com",
  val isPremium: Boolean = true
)

data class FinanceTotals(
  val balance: Double = 0.0,
  val income: Double = 0.0,
  val expense: Double = 0.0
)

data class CategoryItem(
  val id: String,
  val label: String,
  val icon: String,
  val color: String
)

val FINANCE_CATEGORIES = listOf(
  CategoryItem("food", "Dining", "🍔", "#FF9500"),
  CategoryItem("transport", "Travel", "🚗", "#007AFF"),
  CategoryItem("shopping", "Retail", "🛍️", "#AF52DE"),
  CategoryItem("entertainment", "Fun", "🎬", "#FF2D55"),
  CategoryItem("health", "Health", "🏥", "#34C759"),
  CategoryItem("essential", "Essential", "⚡", "#5856D6"),
  CategoryItem("other", "Other", "📦", "#8E8E93")
)

val GOAL_ICONS = listOf("📱", "✈️", "🚗", "🏠", "💻", "🎓", "💍", "🎸", "📷", "🏋️")

data class CategorySpend(
  val categoryId: String,
  val label: String,
  val icon: String,
  val color: String,
  val amount: Double,
  val percentage: Float,
  val transactionCount: Int
)

data class MonthlyTrend(
  val monthName: String,
  val income: Double,
  val expense: Double
)

data class AnalyticsReport(
  val totalIncome: Double = 0.0,
  val totalExpense: Double = 0.0,
  val netSavings: Double = 0.0,
  val savingsRate: Double = 0.0,
  val dailyAverage: Double = 0.0,
  val categories: List<CategorySpend> = emptyList(),
  val trends: List<MonthlyTrend> = emptyList(),
  val topCategory: CategorySpend? = null,
  val totalTransactions: Int = 0
)

class WalletViewModel(
  private val repository: WalletRepository
) : ViewModel() {

  init {
    viewModelScope.launch {
      repository.ensureInitialized()
    }
  }

  // Authentication State
  private val _isAuthenticated = MutableStateFlow(true)
  val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

  // Active Screen / Tab ("dashboard", "history", "goals", "profile", "add", "analytics")
  private val _activeView = MutableStateFlow("dashboard")
  val activeView: StateFlow<String> = _activeView.asStateFlow()

  // Add Mode: "transaction" or "task"
  private val _addMode = MutableStateFlow("transaction")
  val addMode: StateFlow<String> = _addMode.asStateFlow()

  // Currency: "$", "€", "£", "¥"
  private val _currency = MutableStateFlow("$")
  val currency: StateFlow<String> = _currency.asStateFlow()

  // Dark Mode Toggle
  private val _isDarkMode = MutableStateFlow(false)
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  // User Profile
  private val _user = MutableStateFlow(UserProfile())
  val user: StateFlow<UserProfile> = _user.asStateFlow()

  // Analytics Timeframe ("month", "30days", "year", "all")
  val analyticsTimeframe = MutableStateFlow("month")

  // Settings Slide Drawer
  val isSettingsDrawerOpen = MutableStateFlow(false)

  // Contribute to Goal Dialog
  val isContributeModalOpen = MutableStateFlow(false)
  val selectedGoalForContribute = MutableStateFlow<GoalEntity?>(null)

  // Create Goal Dialog
  val isAddGoalModalOpen = MutableStateFlow(false)

  // History Filter state
  val searchQuery = MutableStateFlow("")
  val typeFilter = MutableStateFlow("ALL") // "ALL", "EXPENSE", "INCOME"
  val categoryFilter = MutableStateFlow("ALL")

  // Database Flows
  val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allAccounts: StateFlow<List<AccountEntity>> = repository.allAccounts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allGoals: StateFlow<List<GoalEntity>> = repository.allGoals
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingTasks: StateFlow<List<TaskEntity>> = repository.pendingTasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allBudgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Financial Totals (Balance, Income, Expense)
  val totals: StateFlow<FinanceTotals> = combine(allAccounts, allTransactions) { accounts, transactions ->
    val balance = accounts.sumOf { it.balance }
    val income = transactions.filter { it.type.equals("INCOME", ignoreCase = true) }.sumOf { it.amount }
    val expense = transactions.filter { it.type.equals("EXPENSE", ignoreCase = true) }.sumOf { it.amount }
    FinanceTotals(balance = balance, income = income, expense = expense)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinanceTotals())

  // Filtered transactions for History screen
  val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
    allTransactions,
    searchQuery,
    typeFilter,
    categoryFilter
  ) { list, query, type, cat ->
    list.filter { tx ->
      val matchesQuery = query.isBlank() ||
        tx.title.contains(query, ignoreCase = true) ||
        tx.category.contains(query, ignoreCase = true) ||
        tx.note.contains(query, ignoreCase = true)

      val matchesType = type == "ALL" || tx.type.equals(type, ignoreCase = true)

      val matchesCategory = cat == "ALL" ||
        tx.category.equals(cat, ignoreCase = true) ||
        FINANCE_CATEGORIES.find { it.id.equals(cat, ignoreCase = true) }?.label?.equals(tx.category, ignoreCase = true) == true

      matchesQuery && matchesType && matchesCategory
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Reactive Analytics Report
  val analyticsReport: StateFlow<AnalyticsReport> = combine(
    allTransactions,
    analyticsTimeframe
  ) { transactions, timeframe ->
    calculateAnalytics(transactions, timeframe)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsReport())

  // Authentication Actions
  fun login(email: String, name: String = "Alex Morgan") {
    _user.value = UserProfile(
      name = name,
      email = email,
      isPremium = true
    )
    _isAuthenticated.value = true
    _activeView.value = "dashboard"
  }

  fun signUp(name: String, email: String) {
    _user.value = UserProfile(
      name = name,
      email = email,
      isPremium = true
    )
    _isAuthenticated.value = true
    _activeView.value = "dashboard"
  }

  fun signOut() {
    _isAuthenticated.value = false
    isSettingsDrawerOpen.value = false
  }

  // Navigation & View Actions
  fun setView(view: String) {
    _activeView.value = view
  }

  fun setAddMode(mode: String) {
    _addMode.value = mode
  }

  fun setCurrency(newCurrency: String) {
    _currency.value = newCurrency
  }

  fun toggleDarkMode() {
    _isDarkMode.value = !_isDarkMode.value
  }

  fun setAnalyticsTimeframe(timeframe: String) {
    analyticsTimeframe.value = timeframe
  }

  fun addTransaction(
    text: String,
    amount: Double,
    type: String,
    category: String,
    note: String = ""
  ) {
    viewModelScope.launch {
      repository.addTransaction(
        title = text,
        amount = amount,
        type = type.uppercase(),
        category = category,
        accountId = 1,
        note = note
      )
      _activeView.value = "dashboard"
    }
  }

  fun deleteTransaction(id: Long) {
    viewModelScope.launch {
      repository.deleteTransaction(id)
    }
  }

  fun addGoal(title: String, target: Double, icon: String) {
    viewModelScope.launch {
      repository.addGoal(title, target, icon)
      isAddGoalModalOpen.value = false
    }
  }

  fun contributeToGoal(goalId: Long, amount: Double) {
    viewModelScope.launch {
      repository.contributeToGoal(goalId, amount)
      isContributeModalOpen.value = false
      selectedGoalForContribute.value = null
    }
  }

  fun addTask(text: String, amount: Double, dueDate: String) {
    viewModelScope.launch {
      repository.addTask(text, amount, dueDate)
      _activeView.value = "dashboard"
    }
  }

  fun payTask(task: TaskEntity) {
    viewModelScope.launch {
      repository.payTask(task)
    }
  }

  fun resetAllData() {
    viewModelScope.launch {
      repository.resetAllData()
      _activeView.value = "dashboard"
      isSettingsDrawerOpen.value = false
    }
  }

  private fun calculateAnalytics(transactions: List<TransactionEntity>, timeframe: String): AnalyticsReport {
    val now = System.currentTimeMillis()
    val filtered = when (timeframe) {
      "month" -> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfMonth = cal.timeInMillis
        transactions.filter { it.timestamp >= startOfMonth }
      }
      "30days" -> {
        val thirtyDaysAgo = now - (30L * 86_400_000L)
        transactions.filter { it.timestamp >= thirtyDaysAgo }
      }
      "year" -> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfYear = cal.timeInMillis
        transactions.filter { it.timestamp >= startOfYear }
      }
      else -> transactions
    }

    val totalIncome = filtered.filter { it.type.equals("INCOME", ignoreCase = true) }.sumOf { it.amount }
    val totalExpense = filtered.filter { it.type.equals("EXPENSE", ignoreCase = true) }.sumOf { it.amount }
    val netSavings = totalIncome - totalExpense
    val savingsRate = if (totalIncome > 0) ((totalIncome - totalExpense) / totalIncome * 100.0).coerceAtLeast(0.0) else 0.0

    val daysInPeriod = when (timeframe) {
      "month" -> 30
      "30days" -> 30
      "year" -> 365
      else -> 60
    }
    val dailyAverage = if (daysInPeriod > 0) totalExpense / daysInPeriod else 0.0

    // Group expenses by category
    val expenseTxs = filtered.filter { it.type.equals("EXPENSE", ignoreCase = true) }
    val categoryMap = expenseTxs.groupBy { it.category.lowercase() }
    val categorySpendList = mutableListOf<CategorySpend>()

    FINANCE_CATEGORIES.forEach { catItem ->
      val matchingTxs = categoryMap[catItem.id.lowercase()] ?: categoryMap[catItem.label.lowercase()] ?: emptyList()
      val sum = matchingTxs.sumOf { it.amount }
      if (sum > 0) {
        val pct = if (totalExpense > 0) ((sum / totalExpense) * 100.0).toFloat() else 0f
        categorySpendList.add(
          CategorySpend(
            categoryId = catItem.id,
            label = catItem.label,
            icon = catItem.icon,
            color = catItem.color,
            amount = sum,
            percentage = pct,
            transactionCount = matchingTxs.size
          )
        )
      }
    }

    val recognized = FINANCE_CATEGORIES.flatMap { listOf(it.id.lowercase(), it.label.lowercase()) }.toSet()
    val otherExpenses = expenseTxs.filter { it.category.lowercase() !in recognized }
    if (otherExpenses.isNotEmpty()) {
      val sum = otherExpenses.sumOf { it.amount }
      val pct = if (totalExpense > 0) ((sum / totalExpense) * 100.0).toFloat() else 0f
      categorySpendList.add(
        CategorySpend(
          categoryId = "other",
          label = "Other",
          icon = "📦",
          color = "#8E8E93",
          amount = sum,
          percentage = pct,
          transactionCount = otherExpenses.size
        )
      )
    }
    categorySpendList.sortByDescending { it.amount }

    // Monthly trends (past 6 months)
    val sdf = SimpleDateFormat("MMM", Locale.US)
    val trends = mutableListOf<MonthlyTrend>()
    for (i in 5 downTo 0) {
      val monthCal = Calendar.getInstance()
      monthCal.add(Calendar.MONTH, -i)
      val monthName = sdf.format(monthCal.time)
      val targetYearMonth = monthCal.get(Calendar.YEAR) * 100 + monthCal.get(Calendar.MONTH)

      val txInMonth = transactions.filter {
        val c = Calendar.getInstance().apply { timeInMillis = it.timestamp }
        val ym = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH)
        ym == targetYearMonth
      }
      var inc = txInMonth.filter { it.type.equals("INCOME", ignoreCase = true) }.sumOf { it.amount }
      var exp = txInMonth.filter { it.type.equals("EXPENSE", ignoreCase = true) }.sumOf { it.amount }

      // Baseline if no historical transactions recorded yet in Room DB
      if (inc == 0.0 && exp == 0.0) {
        inc = when (i) {
          5 -> 4800.0
          4 -> 5100.0
          3 -> 5250.0
          2 -> 4950.0
          1 -> 5300.0
          else -> totalIncome.coerceAtLeast(3500.0)
        }
        exp = when (i) {
          5 -> 2400.0
          4 -> 2650.0
          3 -> 2100.0
          2 -> 2300.0
          1 -> 2450.0
          else -> totalExpense.coerceAtLeast(1900.0)
        }
      }
      trends.add(MonthlyTrend(monthName = monthName, income = inc, expense = exp))
    }

    return AnalyticsReport(
      totalIncome = totalIncome,
      totalExpense = totalExpense,
      netSavings = netSavings,
      savingsRate = savingsRate,
      dailyAverage = dailyAverage,
      categories = categorySpendList,
      trends = trends,
      topCategory = categorySpendList.firstOrNull(),
      totalTransactions = filtered.size
    )
  }
}

class WalletViewModelFactory(
  private val repository: WalletRepository
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
      return WalletViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
