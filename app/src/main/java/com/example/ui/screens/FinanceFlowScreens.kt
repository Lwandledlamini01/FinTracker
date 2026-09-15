package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.AvailableBalanceCard
import com.example.ui.components.FinanceTransactionItem
import com.example.ui.components.InsightsSection
import com.example.ui.components.PendingTasksSection
import com.example.ui.components.formatMoney
import com.example.ui.components.getCategoryItem
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FINANCE_CATEGORIES
import com.example.ui.viewmodel.FinanceTotals
import com.example.ui.viewmodel.GOAL_ICONS
import com.example.ui.viewmodel.UserProfile
import java.util.Locale

// ==========================================
// 1. DASHBOARD SCREEN
// ==========================================
@Composable
fun FinanceDashboardScreen(
  totals: FinanceTotals,
  transactions: List<TransactionEntity>,
  goals: List<GoalEntity>,
  tasks: List<TaskEntity>,
  currency: String,
  isDarkMode: Boolean,
  onNewTaskClick: () -> Unit,
  onPayTask: (TaskEntity) -> Unit,
  onSeeAllTransactions: () -> Unit,
  onOpenAnalytics: () -> Unit = {}
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("dashboard_screen"),
    contentPadding = PaddingValues(bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Available Balance Card
    item {
      AvailableBalanceCard(
        totals = totals,
        currency = currency,
        isDarkMode = isDarkMode
      )
    }

    // Analytics & Reports Quick Shortcut Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
          .clickable { onOpenAnalytics() }
          .testTag("dashboard_analytics_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
        ),
        border = BorderStroke(1.dp, if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AccentPurple.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Text("📊", fontSize = 18.sp)
            }
            Column {
              Text(
                text = "Analytics & Reports",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
              Text(
                text = "Monthly breakdown & category trends",
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }
          Text(
            text = "View →",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AccentPurple
          )
        }
      }
    }

    // 2. Smart Insights Section
    item {
      InsightsSection(
        transactions = transactions,
        goals = goals,
        currency = currency,
        isDarkMode = isDarkMode
      )
    }

    // 3. Pending Tasks Section
    item {
      PendingTasksSection(
        tasks = tasks,
        currency = currency,
        isDarkMode = isDarkMode,
        onNewTaskClick = onNewTaskClick,
        onPayTask = onPayTask
      )
    }

    // 4. Recent Transactions Preview (Top 5)
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Recent Transactions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "See all",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = AccentPurple,
            modifier = Modifier
              .clickable { onSeeAllTransactions() }
              .testTag("see_all_transactions_link")
          )
        }

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
          ),
          border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
          )
        ) {
          Column {
            val recent = transactions.take(5)
            if (recent.isEmpty()) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "No transactions yet",
                  fontSize = 13.sp,
                  color = TextSecondary
                )
              }
            } else {
              recent.forEachIndexed { index, tx ->
                FinanceTransactionItem(
                  transaction = tx,
                  currency = currency,
                  isDarkMode = isDarkMode
                )
                if (index < recent.size - 1) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(1.dp)
                      .background(if (isDarkMode) Color(0x14FFFFFF) else Color(0x08000000))
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

// ==========================================
// 2. HISTORY SCREEN (Transaction History)
// ==========================================
@Composable
fun FinanceHistoryScreen(
  transactions: List<TransactionEntity>,
  currency: String,
  isDarkMode: Boolean,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  selectedType: String,
  onTypeSelect: (String) -> Unit,
  selectedCategory: String,
  onCategorySelect: (String) -> Unit,
  onDeleteTransaction: (Long) -> Unit
) {
  var txToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .testTag("history_screen")
  ) {
    // Search Bar & Filter Controls
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Search Box
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Search transactions...", fontSize = 13.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchChange("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("history_search_input"),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White,
          unfocusedContainerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White,
          focusedBorderColor = AccentPurple,
          unfocusedBorderColor = if (isDarkMode) Color(0x14FFFFFF) else Color(0x0D000000)
        )
      )

      // Segmented Type Filter (All, Expense, Income)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
          .padding(3.dp)
      ) {
        listOf("ALL" to "All", "EXPENSE" to "Expenses", "INCOME" to "Income").forEach { (typeKey, label) ->
          val isSelected = selectedType.equals(typeKey, ignoreCase = true)
          Box(
            modifier = Modifier
              .weight(1f)
              .height(32.dp)
              .clip(RoundedCornerShape(9.dp))
              .background(if (isSelected) (if (isDarkMode) Color(0xFF1C1C1E) else Color.White) else Color.Transparent)
              .clickable { onTypeSelect(typeKey) },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) MaterialTheme.colorScheme.onSurface else TextSecondary
            )
          }
        }
      }

      // Categories Horizontal Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        FilterChip(
          selected = selectedCategory == "ALL",
          onClick = { onCategorySelect("ALL") },
          label = { Text("All", fontSize = 11.sp) },
          shape = RoundedCornerShape(8.dp)
        )
        FINANCE_CATEGORIES.forEach { cat ->
          val isSelected = selectedCategory.equals(cat.id, ignoreCase = true) || selectedCategory.equals(cat.label, ignoreCase = true)
          FilterChip(
            selected = isSelected,
            onClick = { onCategorySelect(cat.id) },
            label = { Text("${cat.icon} ${cat.label}", fontSize = 11.sp) },
            shape = RoundedCornerShape(8.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Transactions List
    if (transactions.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("📊", fontSize = 48.sp)
          Text(
            text = "No transactions yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Your transaction history will appear here once you start tracking your spending.",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
        ),
        border = BorderStroke(
          1.dp,
          if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
        )
      ) {
        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(top = 6.dp, bottom = 96.dp)
        ) {
          itemsIndexed(transactions, key = { _, tx -> tx.id }) { index, tx ->
            FinanceTransactionItem(
              transaction = tx,
              currency = currency,
              isDarkMode = isDarkMode,
              onClick = { txToDelete = tx }
            )
            if (index < transactions.size - 1) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(1.dp)
                  .background(if (isDarkMode) Color(0x14FFFFFF) else Color(0x08000000))
              )
            }
          }
        }
      }
    }
  }

  // Delete transaction confirm dialog
  if (txToDelete != null) {
    val tx = txToDelete!!
    AlertDialog(
      onDismissRequest = { txToDelete = null },
      title = { Text("Delete Transaction", fontWeight = FontWeight.Bold) },
      text = { Text("Are you sure you want to delete \"${tx.title}\" (${formatMoney(tx.amount, currency)})?") },
      confirmButton = {
        Button(
          onClick = {
            onDeleteTransaction(tx.id)
            txToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { txToDelete = null }, shape = RoundedCornerShape(8.dp)) {
          Text("Cancel")
        }
      },
      shape = RoundedCornerShape(16.dp)
    )
  }
}

// ==========================================
// 3. GOALS SCREEN (Savings Goals)
// ==========================================
@Composable
fun FinanceGoalsScreen(
  goals: List<GoalEntity>,
  currency: String,
  isDarkMode: Boolean,
  onAddFundsClick: () -> Unit,
  onNewGoalClick: () -> Unit,
  onContributeToGoal: (GoalEntity) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("goals_screen"),
    contentPadding = PaddingValues(start = 20.dp, top = 6.dp, end = 20.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header & Actions
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Savings Goals",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Invest in your future",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          if (goals.isNotEmpty()) {
            Button(
              onClick = onAddFundsClick,
              colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
              shape = RoundedCornerShape(20.dp),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
              modifier = Modifier.height(36.dp).testTag("add_funds_header_button")
            ) {
              Text("+ Add Funds", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          Button(
            onClick = onNewGoalClick,
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp).testTag("new_goal_header_button")
          ) {
            Text("+ New Goal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    if (goals.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(32.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
          ),
          border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Text("🎯", fontSize = 52.sp)
            Text(
              text = "No goals yet",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Start saving for something special by creating your first goal.",
              fontSize = 13.sp,
              color = TextSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
              onClick = onNewGoalClick,
              colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
              shape = RoundedCornerShape(20.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Create First Goal", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      items(goals, key = { it.id }) { goal ->
        val ratio = if (goal.target > 0) (goal.current / goal.target).toFloat() else 0f
        val pct = (ratio * 100).toInt()
        val isCompleted = goal.current >= goal.target

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onContributeToGoal(goal) }
            .testTag("goal_card_${goal.id}"),
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
          ),
          border = BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
          )
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(goal.icon, fontSize = 28.sp)
                }

                Column {
                  Text(
                    text = goal.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${formatMoney(goal.current, currency)} of ${formatMoney(goal.target, currency)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                  )
                }
              }

              // Percentage / Completed badge
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    if (isCompleted) AccentGreen.copy(alpha = 0.15f)
                    else AccentPurple.copy(alpha = 0.15f)
                  )
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (isCompleted) "Completed!" else "$pct%",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = if (isCompleted) AccentGreen else AccentPurple
                )
              }
            }

            // Progress Bar
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7))
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(fraction = ratio.coerceIn(0f, 1f))
                  .fillMaxHeight()
                  .clip(CircleShape)
                  .background(if (isCompleted) AccentGreen else AccentPurple)
              )
            }
          }
        }
      }
    }
  }
}

// ==========================================
// 4. PROFILE SCREEN (Account Profile)
// ==========================================
@Composable
fun FinanceProfileScreen(
  user: UserProfile,
  totals: FinanceTotals,
  goalsCount: Int,
  tasksCount: Int,
  currency: String,
  isDarkMode: Boolean,
  onToggleDarkMode: () -> Unit,
  onSelectCurrency: (String) -> Unit,
  onNavigate: (String) -> Unit,
  onSignOut: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("profile_screen"),
    contentPadding = PaddingValues(start = 20.dp, top = 6.dp, end = 20.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // User Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
        ),
        border = BorderStroke(
          1.dp,
          if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(Color(0xFF4B5563), Color(0xFF111827))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.joinToString(""),
              color = Color.White,
              fontSize = 28.sp,
              fontWeight = FontWeight.Black
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = user.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = user.email,
            fontSize = 12.sp,
            color = TextSecondary
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(AccentGreen.copy(alpha = 0.15f))
              .padding(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Premium Member",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = AccentGreen
            )
          }
        }
      }
    }

    // Quick Shortcuts (2x2 Grid)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Goals shortcut
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .background(if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
              .border(
                BorderStroke(
                  1.dp,
                  if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
                ),
                RoundedCornerShape(20.dp)
              )
              .clickable { onNavigate("goals") }
              .padding(14.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("🎯", fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Goals", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text("$goalsCount Active", fontSize = 10.sp, color = TextSecondary)
            }
          }

          // Analytics shortcut
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .background(if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
              .border(
                BorderStroke(
                  1.dp,
                  if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
                ),
                RoundedCornerShape(20.dp)
              )
              .clickable { onNavigate("analytics") }
              .padding(14.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("📈", fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Analytics", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text("Trends & Stats", fontSize = 10.sp, color = TextSecondary)
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // History shortcut
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .background(if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
              .border(
                BorderStroke(
                  1.dp,
                  if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
                ),
                RoundedCornerShape(20.dp)
              )
              .clickable { onNavigate("history") }
              .padding(14.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("📊", fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("History", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text("All Logs", fontSize = 10.sp, color = TextSecondary)
            }
          }

          // Dashboard shortcut
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .background(if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
              .border(
                BorderStroke(
                  1.dp,
                  if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
                ),
                RoundedCornerShape(20.dp)
              )
              .clickable { onNavigate("dashboard") }
              .padding(14.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("⚡", fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text("Tasks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text("$tasksCount Pending", fontSize = 10.sp, color = TextSecondary)
            }
          }
        }
      }
    }

    // Settings Options
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
        ),
        border = BorderStroke(
          1.dp,
          if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
        )
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Dark Mode Toggle
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.DarkMode, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(18.dp))
              }
              Text("Dark Mode", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Switch(
              checked = isDarkMode,
              onCheckedChange = { onToggleDarkMode() },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentGreen
              )
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(if (isDarkMode) Color(0x14FFFFFF) else Color(0x08000000))
          )

          // Currency Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Paid, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(18.dp))
              }
              Text("Currency", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf("$" to "USD", "€" to "EUR", "£" to "GBP", "¥" to "JPY").forEach { (curr, _) ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (currency == curr) AccentPurple else if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7))
                    .clickable { onSelectCurrency(curr) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = curr,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currency == curr) Color.White else MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }
      }
    }

    // Reset Data Button
    item {
      Button(
        onClick = onSignOut,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(18.dp)
      ) {
        Text("Sign Out / Reset Sample Data", color = AccentRed, fontWeight = FontWeight.Bold)
      }
    }
  }
}

// ==========================================
// 5. ADD SCREEN (Transaction vs Task)
// ==========================================
@Composable
fun FinanceAddScreen(
  addMode: String,
  currency: String,
  isDarkMode: Boolean,
  onSetAddMode: (String) -> Unit,
  onAddTransaction: (text: String, amount: Double, type: String, category: String, note: String) -> Unit,
  onAddTask: (text: String, amount: Double, dueDate: String) -> Unit,
  onCancel: () -> Unit
) {
  var txType by remember { mutableStateOf("EXPENSE") }
  var txAmount by remember { mutableStateOf("") }
  var txText by remember { mutableStateOf("") }
  var txCategory by remember { mutableStateOf("food") }
  var txError by remember { mutableStateOf<String?>(null) }

  var taskText by remember { mutableStateOf("") }
  var taskAmount by remember { mutableStateOf("") }
  var taskDueDate by remember { mutableStateOf("Tomorrow") }
  var taskError by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Segmented Mode Toggle (Transaction vs Task)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFE5E5EA))
        .padding(4.dp)
    ) {
      Box(
        modifier = Modifier
          .weight(1f)
          .height(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(if (addMode == "transaction") (if (isDarkMode) Color(0xFF1C1C1E) else Color.White) else Color.Transparent)
          .clickable { onSetAddMode("transaction") },
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Transaction",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (addMode == "transaction") MaterialTheme.colorScheme.onSurface else TextSecondary
        )
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .height(40.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(if (addMode == "task") (if (isDarkMode) Color(0xFF1C1C1E) else Color.White) else Color.Transparent)
          .clickable { onSetAddMode("task") },
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Task",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = if (addMode == "task") MaterialTheme.colorScheme.onSurface else TextSecondary
        )
      }
    }

    if (addMode == "transaction") {
      // Transaction Type Toggle (Expense vs Income)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (txType == "EXPENSE") AccentRed else if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
            .clickable { txType = "EXPENSE" },
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Expense",
            fontWeight = FontWeight.Bold,
            color = if (txType == "EXPENSE") Color.White else MaterialTheme.colorScheme.onSurface
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (txType == "INCOME") AccentGreen else if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
            .clickable { txType = "INCOME" },
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Income",
            fontWeight = FontWeight.Bold,
            color = if (txType == "INCOME") Color.White else MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Amount Input
      OutlinedTextField(
        value = txAmount,
        onValueChange = { txAmount = it },
        label = { Text("Amount ($currency)") },
        placeholder = { Text("0.00") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_tx_amount_input"),
        shape = RoundedCornerShape(14.dp)
      )

      // Description
      OutlinedTextField(
        value = txText,
        onValueChange = { txText = it },
        label = { Text("Description") },
        placeholder = { Text(if (txType == "EXPENSE") "e.g. Whole Foods, Uber" else "e.g. Salary, Client Payout") },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_tx_desc_input"),
        shape = RoundedCornerShape(14.dp)
      )

      // Category Chips
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          FINANCE_CATEGORIES.forEach { cat ->
            val isSelected = txCategory == cat.id
            FilterChip(
              selected = isSelected,
              onClick = { txCategory = cat.id },
              label = { Text("${cat.icon} ${cat.label}", fontSize = 12.sp) },
              shape = RoundedCornerShape(8.dp)
            )
          }
        }
      }

      if (txError != null) {
        Text(txError!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onCancel,
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            val amt = txAmount.toDoubleOrNull()
            if (amt == null || amt <= 0) {
              txError = "Please enter a valid amount"
              return@Button
            }
            if (txText.isBlank()) {
              txError = "Please enter a description"
              return@Button
            }
            onAddTransaction(txText.trim(), amt, txType, txCategory, "")
          },
          modifier = Modifier
            .weight(1.5f)
            .height(48.dp)
            .testTag("confirm_add_tx_button"),
          colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Add Transaction", fontWeight = FontWeight.Bold)
        }
      }
    } else {
      // Task Mode
      OutlinedTextField(
        value = taskText,
        onValueChange = { taskText = it },
        label = { Text("Task name (e.g., Electric Bill)") },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_task_name_input"),
        shape = RoundedCornerShape(14.dp)
      )

      OutlinedTextField(
        value = taskAmount,
        onValueChange = { taskAmount = it },
        label = { Text("Amount ($currency)") },
        placeholder = { Text("0.00") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_task_amount_input"),
        shape = RoundedCornerShape(14.dp)
      )

      OutlinedTextField(
        value = taskDueDate,
        onValueChange = { taskDueDate = it },
        label = { Text("Due date (e.g. Tomorrow, In 3 days)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
      )

      if (taskError != null) {
        Text(taskError!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onCancel,
          modifier = Modifier
            .weight(1f)
            .height(48.dp),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Cancel")
        }

        Button(
          onClick = {
            val amt = taskAmount.toDoubleOrNull()
            if (amt == null || amt <= 0) {
              taskError = "Please enter a valid amount"
              return@Button
            }
            if (taskText.isBlank()) {
              taskError = "Please enter a task name"
              return@Button
            }
            onAddTask(taskText.trim(), amt, taskDueDate.trim().ifBlank { "Soon" })
          },
          modifier = Modifier
            .weight(1.5f)
            .height(48.dp)
            .testTag("confirm_add_task_button"),
          colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Add Task", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// ==========================================
// 6. CREATE GOAL DIALOG
// ==========================================
@Composable
fun CreateGoalDialog(
  currency: String,
  isDarkMode: Boolean,
  onDismiss: () -> Unit,
  onCreateGoal: (title: String, target: Double, icon: String) -> Unit
) {
  var selectedIcon by remember { mutableStateOf(GOAL_ICONS.first()) }
  var goalTitle by remember { mutableStateOf("") }
  var goalTarget by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Create New Goal",
        fontWeight = FontWeight.Black,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Icon Picker
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "CHOOSE ICON",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = TextSecondary
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            GOAL_ICONS.forEach { iconEmoji ->
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(
                    if (selectedIcon == iconEmoji) AccentPurple
                    else if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
                  )
                  .clickable { selectedIcon = iconEmoji },
                contentAlignment = Alignment.Center
              ) {
                Text(iconEmoji, fontSize = 22.sp)
              }
            }
          }
        }

        // Goal Name
        OutlinedTextField(
          value = goalTitle,
          onValueChange = { goalTitle = it },
          label = { Text("Goal Name") },
          placeholder = { Text("e.g., Vacation, New Car") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("create_goal_title_input"),
          shape = RoundedCornerShape(14.dp)
        )

        // Target Amount
        OutlinedTextField(
          value = goalTarget,
          onValueChange = { goalTarget = it },
          label = { Text("Target Amount ($currency)") },
          placeholder = { Text("0.00") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("create_goal_target_input"),
          shape = RoundedCornerShape(14.dp)
        )

        if (errorMessage != null) {
          Text(errorMessage!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val tgt = goalTarget.toDoubleOrNull()
          if (tgt == null || tgt <= 0) {
            errorMessage = "Please enter a valid target amount"
            return@Button
          }
          if (goalTitle.isBlank()) {
            errorMessage = "Please enter a goal name"
            return@Button
          }
          onCreateGoal(goalTitle.trim(), tgt, selectedIcon)
        },
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("confirm_create_goal_button")
      ) {
        Text("Create Goal", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
        Text("Cancel")
      }
    },
    shape = RoundedCornerShape(24.dp)
  )
}

// ==========================================
// 7. CONTRIBUTE TO GOAL DIALOG
// ==========================================
@Composable
fun ContributeToGoalDialog(
  goals: List<GoalEntity>,
  initialGoal: GoalEntity?,
  currency: String,
  isDarkMode: Boolean,
  onDismiss: () -> Unit,
  onContribute: (goalId: Long, amount: Double) -> Unit
) {
  var selectedGoalId by remember { mutableStateOf(initialGoal?.id ?: goals.firstOrNull()?.id ?: 1L) }
  var amountText by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Add Funds to Goal",
        fontWeight = FontWeight.Black,
        fontSize = 18.sp
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Select Goal",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = TextSecondary
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          goals.forEach { goal ->
            val isSelected = selectedGoalId == goal.id
            FilterChip(
              selected = isSelected,
              onClick = { selectedGoalId = goal.id },
              label = { Text("${goal.icon} ${goal.title}", fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp)
            )
          }
        }

        OutlinedTextField(
          value = amountText,
          onValueChange = { amountText = it },
          label = { Text("Amount to contribute ($currency)") },
          placeholder = { Text("0.00") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("contribute_amount_input"),
          shape = RoundedCornerShape(14.dp)
        )

        if (errorMessage != null) {
          Text(errorMessage!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = amountText.toDoubleOrNull()
          if (amt == null || amt <= 0) {
            errorMessage = "Please enter a valid amount"
            return@Button
          }
          onContribute(selectedGoalId, amt)
        },
        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("confirm_contribute_button")
      ) {
        Text("Contribute", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
        Text("Cancel")
      }
    },
    shape = RoundedCornerShape(24.dp)
  )
}
