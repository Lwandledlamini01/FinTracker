package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.formatMoney
import com.example.ui.components.getCategoryItem
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FINANCE_CATEGORIES

@Composable
fun SearchScreen(
  transactions: List<TransactionEntity>,
  tasks: List<TaskEntity>,
  goals: List<GoalEntity>,
  currency: String,
  isDarkMode: Boolean,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onSelectTransaction: (TransactionEntity) -> Unit = {},
  onSelectGoal: (GoalEntity) -> Unit = {}
) {
  var selectedScope by remember { mutableStateOf("ALL") }

  // Quick suggestions for fast search exploration
  val popularSearches = listOf("Coffee", "Apple", "Salary", "Food", "Rent", "Tech", "Uber", "Netflix")

  // Filter transactions
  val matchingTransactions = remember(transactions, searchQuery, selectedScope) {
    transactions.filter { tx ->
      val matchesScope = when (selectedScope) {
        "EXPENSE" -> tx.type.equals("EXPENSE", ignoreCase = true)
        "INCOME" -> tx.type.equals("INCOME", ignoreCase = true)
        else -> true
      }
      val matchesQuery = searchQuery.isBlank() ||
        tx.title.contains(searchQuery, ignoreCase = true) ||
        tx.category.contains(searchQuery, ignoreCase = true) ||
        tx.note.contains(searchQuery, ignoreCase = true) ||
        tx.amount.toString().contains(searchQuery)
      matchesScope && matchesQuery
    }
  }

  // Filter tasks
  val matchingTasks = remember(tasks, searchQuery, selectedScope) {
    if (selectedScope == "EXPENSE" || selectedScope == "INCOME") emptyList()
    else {
      tasks.filter { task ->
        searchQuery.isNotBlank() && (
          task.text.contains(searchQuery, ignoreCase = true) ||
          task.amount.toString().contains(searchQuery)
        )
      }
    }
  }

  // Filter goals
  val matchingGoals = remember(goals, searchQuery, selectedScope) {
    if (selectedScope == "EXPENSE" || selectedScope == "INCOME") emptyList()
    else {
      goals.filter { goal ->
        searchQuery.isNotBlank() && (
          goal.title.contains(searchQuery, ignoreCase = true) ||
          goal.target.toString().contains(searchQuery)
        )
      }
    }
  }

  val totalFound = matchingTransactions.size + matchingTasks.size + matchingGoals.size

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("search_screen"),
    contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Search Bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Search transactions, payees, notes...", fontSize = 14.sp) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = AccentPurple,
            modifier = Modifier.size(20.dp)
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchChange("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_screen_input"),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White,
          unfocusedContainerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White,
          focusedBorderColor = AccentPurple,
          unfocusedBorderColor = if (isDarkMode) Color(0x1AFFFFFF) else Color(0x0D000000)
        )
      )
    }

    // 2. Filter Scope Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf(
          "ALL" to "All Results",
          "EXPENSE" to "Expenses",
          "INCOME" to "Income"
        ).forEach { (scopeKey, label) ->
          val isSelected = selectedScope == scopeKey
          FilterChip(
            selected = isSelected,
            onClick = { selectedScope = scopeKey },
            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = AccentPurple.copy(alpha = 0.18f),
              selectedLabelColor = AccentPurple
            )
          )
        }
      }
    }

    // 3. Popular Searches (Quick Tap)
    if (searchQuery.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
          ),
          border = BorderStroke(1.dp, if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "POPULAR SEARCHES",
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
              popularSearches.forEach { keyword ->
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) Color(0xFF1E1E22) else Color(0xFFF2F2F7))
                    .clickable { onSearchChange(keyword) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(
                    text = keyword,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }
      }
    }

    // 4. Results Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (searchQuery.isBlank()) "Recent Transactions" else "Search Results",
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "$totalFound items",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = TextSecondary
        )
      }
    }

    // 5. Goals matches if any
    if (matchingGoals.isNotEmpty()) {
      item {
        Text(
          text = "SAVINGS GOALS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
          color = TextSecondary
        )
      }
      items(matchingGoals, key = { "goal_${it.id}" }) { goal ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectGoal(goal) },
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF2C2C2E) else Color.White
          ),
          border = BorderStroke(1.dp, if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text(goal.icon, fontSize = 24.sp)
              Column {
                Text(goal.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Target: ${formatMoney(goal.target, currency)}", fontSize = 11.sp, color = TextSecondary)
              }
            }
            Text(
              text = "${formatMoney(goal.current, currency)} saved",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = AccentPurple
            )
          }
        }
      }
    }

    // 6. Transactions List
    if (matchingTransactions.isEmpty() && matchingGoals.isEmpty() && matchingTasks.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("🔍", fontSize = 42.sp)
            Text(
              text = "No results found for \"$searchQuery\"",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Try searching by category, amount, or check spelling",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }
        }
      }
    } else {
      items(matchingTransactions, key = { "tx_${it.id}" }) { tx ->
        val catItem = getCategoryItem(tx.category)
        val catBg = try {
          Color(android.graphics.Color.parseColor(catItem.color)).copy(alpha = 0.15f)
        } catch (e: Exception) {
          AccentPurple.copy(alpha = 0.15f)
        }
        val isIncome = tx.type.equals("INCOME", ignoreCase = true)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectTransaction(tx) }
            .testTag("search_result_item_${tx.id}"),
          shape = RoundedCornerShape(18.dp),
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
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(catBg),
                contentAlignment = Alignment.Center
              ) {
                Text(catItem.icon, fontSize = 20.sp)
              }
              Column {
                Text(
                  text = tx.title,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = catItem.label,
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Text(
              text = "${if (isIncome) "+" else "-"}${formatMoney(tx.amount, currency)}",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = if (isIncome) AccentGreen else MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }
  }
}
