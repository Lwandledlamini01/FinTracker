package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.WalletDatabase
import com.example.data.repository.WalletRepository
import com.example.ui.components.FinanceFlowBottomNav
import com.example.ui.components.FinanceFlowHeader
import com.example.ui.components.SettingsDrawer
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ContributeToGoalDialog
import com.example.ui.screens.CreateGoalDialog
import com.example.ui.screens.FinanceAddScreen
import com.example.ui.screens.FinanceDashboardScreen
import com.example.ui.screens.FinanceGoalsScreen
import com.example.ui.screens.FinanceHistoryScreen
import com.example.ui.screens.FinanceProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WalletViewModel
import com.example.ui.viewmodel.WalletViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = WalletDatabase.getDatabase(applicationContext, lifecycleScope)
    val repository = WalletRepository(database)

    setContent {
      val viewModel: WalletViewModel = viewModel(
        factory = WalletViewModelFactory(repository)
      )
      val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
      val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

      MyApplicationTheme(darkTheme = isDarkMode) {
        if (!isAuthenticated) {
          AuthScreen(
            isDarkMode = isDarkMode,
            onLogin = { email, password ->
              viewModel.login(email)
            },
            onSignUp = { name, email, password ->
              viewModel.signUp(name, email)
            },
            onDemoLogin = {
              viewModel.login("alex.morgan@example.com", "Alex Morgan")
            }
          )
        } else {
          FinanceFlowApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun FinanceFlowApp(viewModel: WalletViewModel) {
  val activeView by viewModel.activeView.collectAsStateWithLifecycle()
  val addMode by viewModel.addMode.collectAsStateWithLifecycle()
  val currency by viewModel.currency.collectAsStateWithLifecycle()
  val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
  val user by viewModel.user.collectAsStateWithLifecycle()
  val totals by viewModel.totals.collectAsStateWithLifecycle()

  val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
  val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
  val goals by viewModel.allGoals.collectAsStateWithLifecycle()
  val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
  val pendingTasks by viewModel.pendingTasks.collectAsStateWithLifecycle()

  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val typeFilter by viewModel.typeFilter.collectAsStateWithLifecycle()
  val categoryFilter by viewModel.categoryFilter.collectAsStateWithLifecycle()

  val analyticsReport by viewModel.analyticsReport.collectAsStateWithLifecycle()
  val analyticsTimeframe by viewModel.analyticsTimeframe.collectAsStateWithLifecycle()

  val isSettingsOpen by viewModel.isSettingsDrawerOpen.collectAsStateWithLifecycle()
  val isAddGoalOpen by viewModel.isAddGoalModalOpen.collectAsStateWithLifecycle()
  val isContributeOpen by viewModel.isContributeModalOpen.collectAsStateWithLifecycle()
  val selectedGoalForContribute by viewModel.selectedGoalForContribute.collectAsStateWithLifecycle()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing),
      containerColor = MaterialTheme.colorScheme.background,
      topBar = {
        FinanceFlowHeader(
          view = activeView,
          userName = user.name,
          isDarkMode = isDarkMode,
          onOpenMenu = { viewModel.isSettingsDrawerOpen.value = true }
        )
      },
      bottomBar = {
        FinanceFlowBottomNav(
          currentView = activeView,
          isDarkMode = isDarkMode,
          userInitials = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
          onSelectView = { viewModel.setView(it) }
        )
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (activeView) {
          "dashboard" -> {
            FinanceDashboardScreen(
              totals = totals,
              transactions = transactions,
              goals = goals,
              tasks = tasks,
              currency = currency,
              isDarkMode = isDarkMode,
              onNewTaskClick = {
                viewModel.setAddMode("task")
                viewModel.setView("add")
              },
              onPayTask = { viewModel.payTask(it) },
              onSeeAllTransactions = { viewModel.setView("history") },
              onOpenAnalytics = { viewModel.setView("analytics") }
            )
          }

          "search" -> {
            SearchScreen(
              transactions = transactions,
              tasks = tasks,
              goals = goals,
              currency = currency,
              isDarkMode = isDarkMode,
              searchQuery = searchQuery,
              onSearchChange = { viewModel.searchQuery.value = it },
              onSelectGoal = { goal ->
                viewModel.selectedGoalForContribute.value = goal
                viewModel.isContributeModalOpen.value = true
              }
            )
          }

          "analytics" -> {
            AnalyticsScreen(
              report = analyticsReport,
              recentTransactions = transactions,
              currency = currency,
              isDarkMode = isDarkMode,
              selectedTimeframe = analyticsTimeframe,
              onSelectTimeframe = { viewModel.setAnalyticsTimeframe(it) }
            )
          }

          "history" -> {
            FinanceHistoryScreen(
              transactions = filteredTransactions,
              currency = currency,
              isDarkMode = isDarkMode,
              searchQuery = searchQuery,
              onSearchChange = { viewModel.searchQuery.value = it },
              selectedType = typeFilter,
              onTypeSelect = { viewModel.typeFilter.value = it },
              selectedCategory = categoryFilter,
              onCategorySelect = { viewModel.categoryFilter.value = it },
              onDeleteTransaction = { viewModel.deleteTransaction(it) }
            )
          }

          "goals" -> {
            FinanceGoalsScreen(
              goals = goals,
              currency = currency,
              isDarkMode = isDarkMode,
              onAddFundsClick = {
                viewModel.selectedGoalForContribute.value = goals.firstOrNull()
                viewModel.isContributeModalOpen.value = true
              },
              onNewGoalClick = { viewModel.isAddGoalModalOpen.value = true },
              onContributeToGoal = { goal ->
                viewModel.selectedGoalForContribute.value = goal
                viewModel.isContributeModalOpen.value = true
              }
            )
          }

          "profile" -> {
            FinanceProfileScreen(
              user = user,
              totals = totals,
              goalsCount = goals.size,
              tasksCount = pendingTasks.size,
              currency = currency,
              isDarkMode = isDarkMode,
              onToggleDarkMode = { viewModel.toggleDarkMode() },
              onSelectCurrency = { viewModel.setCurrency(it) },
              onNavigate = { viewModel.setView(it) },
              onSignOut = { viewModel.signOut() }
            )
          }

          "add" -> {
            FinanceAddScreen(
              addMode = addMode,
              currency = currency,
              isDarkMode = isDarkMode,
              onSetAddMode = { viewModel.setAddMode(it) },
              onAddTransaction = { text, amount, type, category, note ->
                viewModel.addTransaction(text, amount, type, category, note)
              },
              onAddTask = { text, amount, dueDate ->
                viewModel.addTask(text, amount, dueDate)
              },
              onCancel = { viewModel.setView("dashboard") }
            )
          }
        }
      }
    }

    // Settings Drawer (overlay)
    SettingsDrawer(
      isOpen = isSettingsOpen,
      user = user,
      isDarkMode = isDarkMode,
      currency = currency,
      currentView = activeView,
      onClose = { viewModel.isSettingsDrawerOpen.value = false },
      onSelectView = { viewModel.setView(it) },
      onToggleDarkMode = { viewModel.toggleDarkMode() },
      onSelectCurrency = { viewModel.setCurrency(it) },
      onSignOut = { viewModel.signOut() }
    )

    // Create Goal Dialog
    if (isAddGoalOpen) {
      CreateGoalDialog(
        currency = currency,
        isDarkMode = isDarkMode,
        onDismiss = { viewModel.isAddGoalModalOpen.value = false },
        onCreateGoal = { title, target, icon ->
          viewModel.addGoal(title, target, icon)
        }
      )
    }

    // Contribute To Goal Dialog
    if (isContributeOpen && goals.isNotEmpty()) {
      ContributeToGoalDialog(
        goals = goals,
        initialGoal = selectedGoalForContribute,
        currency = currency,
        isDarkMode = isDarkMode,
        onDismiss = {
          viewModel.isContributeModalOpen.value = false
          viewModel.selectedGoalForContribute.value = null
        },
        onContribute = { goalId, amount ->
          viewModel.contributeToGoal(goalId, amount)
        }
      )
    }
  }
}
