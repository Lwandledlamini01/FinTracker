package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentLilac
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPink
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.CategoryItem
import com.example.ui.viewmodel.FINANCE_CATEGORIES
import com.example.ui.viewmodel.FinanceTotals
import com.example.ui.viewmodel.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatMoney(amount: Double, currency: String = "$"): String {
  val formatted = String.format(Locale.US, "%,.2f", amount)
  return "$currency$formatted"
}

fun formatHeaderDate(): String {
  val sdf = SimpleDateFormat("EEE, MMMM d", Locale.US)
  return sdf.format(Date()).uppercase()
}

fun getCategoryItem(categoryId: String): CategoryItem {
  return FINANCE_CATEGORIES.find { it.id.equals(categoryId, ignoreCase = true) || it.label.equals(categoryId, ignoreCase = true) }
    ?: FINANCE_CATEGORIES.last()
}

// 1. Top Header Bar (Exact from FinanceFlow)
@Composable
fun FinanceFlowHeader(
  view: String,
  userName: String,
  isDarkMode: Boolean,
  onOpenMenu: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = formatHeaderDate(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextSecondary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = when (view) {
          "dashboard" -> "Hi, ${userName.split(" ").firstOrNull() ?: "there"}"
          "search" -> "Search"
          "analytics" -> "Analytics & Reports"
          "history" -> "History"
          "goals" -> "Savings Goals"
          "profile" -> "Account Profile"
          "add" -> "Add New"
          else -> "FinanceFlow"
        },
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.5).sp,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    // iOS style 48x48 rounded button with 3 horizontal menu bars
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
        .border(
          BorderStroke(
            1.dp,
            if (isDarkMode) Color(0x1AFFFFFF) else Color(0x0D000000)
          ),
          RoundedCornerShape(16.dp)
        )
        .clickable { onOpenMenu() }
        .testTag("menu_drawer_button"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .width(20.dp)
            .height(2.dp)
            .clip(CircleShape)
            .background(if (isDarkMode) Color.White else Color.Black)
        )
        Box(
          modifier = Modifier
            .width(20.dp)
            .height(2.dp)
            .clip(CircleShape)
            .background(if (isDarkMode) Color.White else Color.Black)
        )
        Box(
          modifier = Modifier
            .width(20.dp)
            .height(2.dp)
            .clip(CircleShape)
            .background(if (isDarkMode) Color.White else Color.Black)
        )
      }
    }
  }
}

// 2. Bottom Navigation Bar (Exact FinanceFlow with center floating + button)
@Composable
fun FinanceFlowBottomNav(
  currentView: String,
  isDarkMode: Boolean,
  userInitials: String = "A",
  onSelectView: (String) -> Unit
) {
  // Idea 1 from design mockup: Sleek dark rounded bar with active capsule pill and minimal inactive icons
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
      .testTag("finance_bottom_nav"),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(
          elevation = 20.dp,
          shape = RoundedCornerShape(32.dp),
          spotColor = Color(0x66000000),
          ambientColor = Color(0x33000000)
        ),
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFF16171B)
      ),
      border = BorderStroke(
        1.dp,
        Color(0x24FFFFFF)
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // 1. Home
          Idea1NavItem(
            label = "Home",
            activeIcon = Icons.Filled.Home,
            inactiveIcon = Icons.Outlined.Home,
            active = currentView == "dashboard",
            onClick = { onSelectView("dashboard") },
            testTag = "nav_home"
          )

          // 2. Search
          Idea1NavItem(
            label = "Search",
            activeIcon = Icons.Filled.Search,
            inactiveIcon = Icons.Outlined.Search,
            active = currentView == "search",
            onClick = { onSelectView("search") },
            testTag = "nav_search"
          )

          // 3. Analytics
          Idea1NavItem(
            label = "Analytics",
            activeIcon = Icons.Filled.PieChart,
            inactiveIcon = Icons.Outlined.PieChart,
            active = currentView == "analytics",
            onClick = { onSelectView("analytics") },
            testTag = "nav_analytics"
          )

          // 4. History
          Idea1NavItem(
            label = "History",
            activeIcon = Icons.Filled.Schedule,
            inactiveIcon = Icons.Outlined.Schedule,
            active = currentView == "history",
            onClick = { onSelectView("history") },
            testTag = "nav_history"
          )

          // 5. Profile
          Idea1NavItem(
            label = "Profile",
            activeIcon = Icons.Filled.Person,
            inactiveIcon = Icons.Outlined.Person,
            active = currentView == "profile",
            onClick = { onSelectView("profile") },
            testTag = "nav_profile"
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom Home Indicator Bar
        Box(
          modifier = Modifier
            .width(96.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFF424450))
        )

        Spacer(modifier = Modifier.height(2.dp))
      }
    }
  }
}

@Composable
private fun Idea1NavItem(
  label: String,
  activeIcon: androidx.compose.ui.graphics.vector.ImageVector,
  inactiveIcon: androidx.compose.ui.graphics.vector.ImageVector,
  active: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Box(
    modifier = Modifier
      .animateContentSize(
        animationSpec = spring(
          stiffness = Spring.StiffnessMediumLow,
          dampingRatio = Spring.DampingRatioLowBouncy
        )
      )
      .clip(RoundedCornerShape(50))
      .background(if (active) Color(0xFF2E274D) else Color.Transparent)
      .then(
        if (active) Modifier.border(1.dp, Color(0x33A594FD), RoundedCornerShape(50)) else Modifier
      )
      .clickable { onClick() }
      .padding(
        horizontal = if (active) 14.dp else 10.dp,
        vertical = if (active) 9.dp else 10.dp
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = if (active) activeIcon else inactiveIcon,
        contentDescription = label,
        tint = if (active) Color(0xFFA594FD) else Color(0xFF757885),
        modifier = Modifier.size(22.dp)
      )
      if (active) {
        Text(
          text = label,
          color = Color(0xFFA594FD),
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
private fun Box(
  tint: Color,
  content: @Composable () -> Unit
) {
  androidx.compose.runtime.CompositionLocalProvider(
    androidx.compose.material3.LocalContentColor provides tint
  ) {
    content()
  }
}

// 3. Available Balance Hero Card (Exact from FinanceFlow)
@Composable
fun AvailableBalanceCard(
  totals: FinanceTotals,
  currency: String,
  isDarkMode: Boolean
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp),
    shape = RoundedCornerShape(32.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
    ),
    border = BorderStroke(
      1.dp,
      if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
    )
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      // Top gradient line
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .background(
            Brush.horizontalGradient(
              listOf(Color(0xFF3B82F6), Color(0xFFA855F7))
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "AVAILABLE BALANCE",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.5.sp,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = formatMoney(totals.balance, currency),
          fontSize = 38.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = (-0.5).sp,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dual Income / Expense Pills
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Income Pill
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(22.dp))
              .background(if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7))
              .padding(vertical = 14.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Income",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
              )
              Text(
                text = "$currency${totals.income.toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          // Expenses Pill
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(22.dp))
              .background(if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7))
              .padding(vertical = 14.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Expenses",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
              )
              Text(
                text = "$currency${totals.expense.toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}

// 4. Smart Insights Section (Exact logic from FinanceFlow)
@Composable
fun InsightsSection(
  transactions: List<TransactionEntity>,
  goals: List<GoalEntity>,
  currency: String,
  isDarkMode: Boolean
) {
  val tips = remember(transactions, goals) {
    val list = mutableListOf<InsightItem>()
    val completedGoal = goals.find { it.current >= it.target }
    if (completedGoal != null) {
      list.add(
        InsightItem(
          title = "Goal Achieved!",
          message = "Congratulations! You've reached your ${completedGoal.title} savings goal!",
          color = AccentGold,
          iconEmoji = "🏆"
        )
      )
    }

    val almostGoal = goals.find { it.current < it.target && (it.current / it.target) >= 0.75 }
    if (almostGoal != null) {
      val pct = ((almostGoal.current / almostGoal.target) * 100).toInt()
      list.add(
        InsightItem(
          title = "Almost There!",
          message = "You're $pct% to your ${almostGoal.title} goal. Just $currency${(almostGoal.target - almostGoal.current).toInt()} more to go!",
          color = AccentGreen,
          iconEmoji = "🎯"
        )
      )
    }

    if (transactions.size >= 3) {
      list.add(
        InsightItem(
          title = "Frugal Week!",
          message = "Great budget discipline this week. Keep up the high savings rate!",
          color = AccentPurple,
          iconEmoji = "💡"
        )
      )
    } else {
      list.add(
        InsightItem(
          title = "Keep Tracking!",
          message = "Add more transactions to get personalized insights about your spending habits.",
          color = AccentBlue,
          iconEmoji = "📈"
        )
      )
    }
    list
  }

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
        text = "Insights",
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "${tips.size} tips",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary
      )
    }

    tips.forEach { tip ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
        ),
        border = BorderStroke(
          1.dp,
          if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(tip.color.copy(alpha = if (isDarkMode) 0.2f else 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Text(tip.iconEmoji, fontSize = 20.sp)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = tip.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = tip.color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = tip.message,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}

data class InsightItem(
  val title: String,
  val message: String,
  val color: Color,
  val iconEmoji: String
)

// 5. Pending Tasks Section (Exact from FinanceFlow)
@Composable
fun PendingTasksSection(
  tasks: List<TaskEntity>,
  currency: String,
  isDarkMode: Boolean,
  onNewTaskClick: () -> Unit,
  onPayTask: (TaskEntity) -> Unit
) {
  val uncompleted = tasks.filter { !it.completed }

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
        text = "Pending Tasks",
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "+ New Task",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = AccentPurple,
        modifier = Modifier
          .clickable { onNewTaskClick() }
          .testTag("add_new_task_header_button")
      )
    }

    if (uncompleted.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "All tasks caught up!",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
          color = TextSecondary.copy(alpha = 0.6f)
        )
      }
    } else {
      uncompleted.forEach { task ->
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
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              // Circular completion button
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .border(BorderStroke(2.dp, AccentPurple), CircleShape)
                  .clickable { onPayTask(task) }
                  .testTag("pay_task_${task.id}"),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Complete task",
                  tint = AccentPurple,
                  modifier = Modifier.size(14.dp)
                )
              }

              Column {
                Text(
                  text = task.text,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "$currency${String.format(Locale.US, "%.2f", task.amount)} • Due ${task.dueDate}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextSecondary
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AccentRed.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "DUE SOON",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = AccentRed
              )
            }
          }
        }
      }
    }
  }
}

// 6. Transaction Row (FinanceFlow History / Recent List item)
@Composable
fun FinanceTransactionItem(
  transaction: TransactionEntity,
  currency: String,
  isDarkMode: Boolean,
  onClick: () -> Unit = {}
) {
  val catItem = getCategoryItem(transaction.category)
  val isIncome = transaction.type.equals("INCOME", ignoreCase = true)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 18.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)),
        contentAlignment = Alignment.Center
      ) {
        Text(catItem.icon, fontSize = 20.sp)
      }

      Column {
        Text(
          text = transaction.title,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = catItem.label.uppercase(),
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp,
          color = TextSecondary
        )
      }
    }

    Text(
      text = "${if (isIncome) "+" else "-"}$currency${String.format(Locale.US, "%.2f", transaction.amount)}",
      fontSize = 15.sp,
      fontWeight = FontWeight.Bold,
      color = if (isIncome) AccentGreen else MaterialTheme.colorScheme.onSurface
    )
  }
}

// 7. Settings Slide-Over Drawer
@Composable
fun SettingsDrawer(
  isOpen: Boolean,
  user: UserProfile,
  isDarkMode: Boolean,
  currency: String,
  currentView: String,
  onClose: () -> Unit,
  onSelectView: (String) -> Unit,
  onToggleDarkMode: () -> Unit,
  onSelectCurrency: (String) -> Unit,
  onSignOut: () -> Unit
) {
  if (!isOpen) return

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.5f))
      .clickable { onClose() }
  ) {
    Card(
      modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(0.82f)
        .clickable(enabled = false) {}, // prevent click-through
      shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp)
      ) {
        // User Info Header
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
                .clip(RoundedCornerShape(20.dp))
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Column {
              Text(
                text = user.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = user.email,
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }

          IconButton(onClick = onClose) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Navigation Section
        Text(
          text = "NAVIGATION",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))

        DrawerNavItem(
          icon = { Icon(Icons.Outlined.Dashboard, contentDescription = null) },
          label = "Dashboard",
          active = currentView == "dashboard",
          onClick = { onSelectView("dashboard"); onClose() }
        )
        DrawerNavItem(
          icon = { Icon(Icons.Outlined.Search, contentDescription = null) },
          label = "Search & Discover",
          active = currentView == "search",
          onClick = { onSelectView("search"); onClose() }
        )
        DrawerNavItem(
          icon = { Icon(Icons.Outlined.AutoGraph, contentDescription = null) },
          label = "Analytics & Reports",
          active = currentView == "analytics",
          onClick = { onSelectView("analytics"); onClose() }
        )
        DrawerNavItem(
          icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
          label = "Account Profile",
          active = currentView == "profile",
          onClick = { onSelectView("profile"); onClose() }
        )
        DrawerNavItem(
          icon = { Text("🎯", fontSize = 18.sp) },
          label = "Savings Goals",
          active = currentView == "goals",
          onClick = { onSelectView("goals"); onClose() }
        )
        DrawerNavItem(
          icon = { Icon(Icons.Outlined.History, contentDescription = null) },
          label = "Transaction History",
          active = currentView == "history",
          onClick = { onSelectView("history"); onClose() }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000))
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Settings Section
        Text(
          text = "SETTINGS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Dark Mode Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(Icons.Default.DarkMode, contentDescription = null, tint = TextSecondary)
            Text("Dark Mode", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Currency Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(Icons.Default.Paid, contentDescription = null, tint = TextSecondary)
            Text("Currency", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("$" to "USD", "€" to "EUR", "£" to "GBP").forEach { (curr, label) ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (currency == curr) AccentPurple else if (isDarkMode) Color(0xFF2C2C2E) else Color.White)
                  .clickable { onSelectCurrency(curr) }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
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

        Spacer(modifier = Modifier.weight(1f))

        // Sign Out / Reset Button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSignOut() }
            .padding(vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(Icons.Default.Logout, contentDescription = null, tint = AccentRed)
          Text("Sign Out / Reset", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AccentRed)
        }
      }
    }
  }
}

@Composable
private fun DrawerNavItem(
  icon: @Composable () -> Unit,
  label: String,
  active: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (active) AccentPurple.copy(alpha = 0.12f) else Color.Transparent)
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(tint = if (active) AccentPurple else TextSecondary) {
      icon()
    }
    Text(
      text = label,
      fontSize = 14.sp,
      fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
      color = if (active) AccentPurple else MaterialTheme.colorScheme.onSurface
    )
  }
}
