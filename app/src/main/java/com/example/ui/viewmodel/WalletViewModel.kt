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

class WalletViewModel(
  private val repository: WalletRepository
) : ViewModel() {

  init {
    viewModelScope.launch {
      repository.ensureInitialized()
    }
  }

  // Active Screen / Tab
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

  // Actions
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
