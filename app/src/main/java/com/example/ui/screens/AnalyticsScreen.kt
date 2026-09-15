package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.components.formatMoney
import com.example.ui.components.getCategoryItem
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AnalyticsReport
import com.example.ui.viewmodel.CategorySpend
import com.example.ui.viewmodel.MonthlyTrend
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
  report: AnalyticsReport,
  recentTransactions: List<TransactionEntity>,
  currency: String,
  isDarkMode: Boolean,
  selectedTimeframe: String,
  onSelectTimeframe: (String) -> Unit
) {
  val cardBackground = if (isDarkMode) Color(0xFF1C1C1E) else Color.White
  val borderColor = if (isDarkMode) Color(0x1FFFFFFF) else Color(0x0F000000)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("analytics_screen"),
    contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Timeframe Filter Selector
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFEAEAEE))
          .padding(4.dp)
      ) {
        listOf(
          "month" to "This Month",
          "30days" to "30 Days",
          "year" to "This Year",
          "all" to "All Time"
        ).forEach { (id, label) ->
          val active = selectedTimeframe == id
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (active) cardBackground else Color.Transparent)
              .clickable { onSelectTimeframe(id) }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
              color = if (active) MaterialTheme.colorScheme.onBackground else TextSecondary
            )
          }
        }
      }
    }

    // 2. Net Cash Flow & Savings Rate Hero Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderColor)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(AccentPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.TrendingUp,
                  contentDescription = null,
                  tint = AccentPurple,
                  modifier = Modifier.size(18.dp)
                )
              }
              Text(
                text = "NET SAVINGS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary
              )
            }

            // Savings Rate Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                  if (report.savingsRate >= 20.0) AccentGreen.copy(alpha = 0.15f)
                  else AccentGold.copy(alpha = 0.15f)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${String.format(Locale.US, "%.1f", report.savingsRate)}% saved",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (report.savingsRate >= 20.0) AccentGreen else AccentGold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = formatMoney(report.netSavings, currency),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp,
            color = if (report.netSavings >= 0) MaterialTheme.colorScheme.onBackground else AccentRed
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Income vs Expense Visual Bar
          val totalFlow = report.totalIncome + report.totalExpense
          val incomeRatio = if (totalFlow > 0) (report.totalIncome / totalFlow).toFloat() else 0.5f

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Income ${(incomeRatio * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AccentGreen)
              Text("Expense ${((1f - incomeRatio) * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AccentRed)
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFEAEAEE))
            ) {
              Box(
                modifier = Modifier
                  .weight(incomeRatio.coerceIn(0.01f, 0.99f))
                  .fillMaxHeight()
                  .background(AccentGreen)
              )
              Box(
                modifier = Modifier
                  .weight((1f - incomeRatio).coerceIn(0.01f, 0.99f))
                  .fillMaxHeight()
                  .background(AccentRed)
              )
            }
          }
        }
      }
    }

    // 3. Dual Stats Cards (Total Income & Total Expenses)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Income Stat Card
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = cardBackground),
          border = BorderStroke(1.dp, borderColor)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(AccentGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ArrowUpward,
                  contentDescription = null,
                  tint = AccentGreen,
                  modifier = Modifier.size(14.dp)
                )
              }
              Text(
                text = "INCOME",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = TextSecondary
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = formatMoney(report.totalIncome, currency),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = AccentGreen
            )
          }
        }

        // Expense Stat Card
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = cardBackground),
          border = BorderStroke(1.dp, borderColor)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(AccentRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ArrowDownward,
                  contentDescription = null,
                  tint = AccentRed,
                  modifier = Modifier.size(14.dp)
                )
              }
              Text(
                text = "EXPENSES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = TextSecondary
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = formatMoney(report.totalExpense, currency),
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = AccentRed
            )
          }
        }
      }
    }

    // 4. Monthly Trend Visual Chart (Income vs Expense)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderColor)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Monthly Cash Flow",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )

            // Legend
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentGreen))
                Text("Income", fontSize = 11.sp, color = TextSecondary)
              }
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentRed))
                Text("Expense", fontSize = 11.sp, color = TextSecondary)
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Visual Bar Chart
          AnalyticsBarChart(
            trends = report.trends,
            isDarkMode = isDarkMode
          )
        }
      }
    }

    // 5. Spending by Category Breakdown
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderColor)
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
            Text(
              text = "Spending by Category",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "${report.categories.size} categories",
              fontSize = 12.sp,
              color = TextSecondary
            )
          }

          if (report.categories.isEmpty()) {
            Text(
              text = "No expenses recorded in this period.",
              fontSize = 13.sp,
              color = TextSecondary,
              modifier = Modifier.padding(vertical = 12.dp)
            )
          } else {
            report.categories.forEach { cat ->
              CategorySpendRow(
                item = cat,
                currency = currency,
                isDarkMode = isDarkMode
              )
            }
          }
        }
      }
    }

    // 6. Smart Financial Health Insights
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderColor)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = AccentGold,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Financial Health Insights",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }

          // Insight 1
          report.topCategory?.let { top ->
            InsightItem(
              icon = "📊",
              title = "Highest Spending Area",
              description = "${top.label} is your top expense category, representing ${top.percentage.toInt()}% of your spending (${formatMoney(top.amount, currency)})."
            )
          }

          // Insight 2
          InsightItem(
            icon = if (report.savingsRate >= 30.0) "🌟" else "💡",
            title = if (report.savingsRate >= 30.0) "Strong Savings Rate" else "Budget Optimization",
            description = if (report.savingsRate >= 30.0) {
              "You are currently saving ${String.format(Locale.US, "%.1f", report.savingsRate)}% of your earnings, outpacing standard 20% financial guidelines."
            } else {
              "Your savings rate is ${String.format(Locale.US, "%.1f", report.savingsRate)}%. Setting limits on dining and retail can help boost emergency savings."
            }
          )

          // Insight 3
          InsightItem(
            icon = "⚡",
            title = "Daily Spending Rhythm",
            description = "You average ${formatMoney(report.dailyAverage, currency)} in expenses per day during this timeframe across ${report.totalTransactions} recorded transactions."
          )
        }
      }
    }
  }
}

@Composable
private fun CategorySpendRow(
  item: CategorySpend,
  currency: String,
  isDarkMode: Boolean
) {
  val hexColor = try {
    Color(android.graphics.Color.parseColor(item.color))
  } catch (e: Exception) {
    AccentPurple
  }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(item.icon, fontSize = 16.sp)
        Text(
          text = item.label,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = formatMoney(item.amount, currency),
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "${item.percentage.toInt()}%",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextSecondary
        )
      }
    }

    // Colored progress bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .clip(RoundedCornerShape(3.dp))
        .background(if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFEAEAEE))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth((item.percentage / 100f).coerceIn(0.02f, 1f))
          .fillMaxHeight()
          .clip(RoundedCornerShape(3.dp))
          .background(hexColor)
      )
    }
  }
}

@Composable
private fun InsightItem(
  icon: String,
  title: String,
  description: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    Text(icon, fontSize = 20.sp)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = description,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = TextSecondary
      )
    }
  }
}

@Composable
private fun AnalyticsBarChart(
  trends: List<MonthlyTrend>,
  isDarkMode: Boolean
) {
  val maxVal = trends.maxOfOrNull { maxOf(it.income, it.expense) }?.coerceAtLeast(100.0) ?: 100.0
  val gridColor = if (isDarkMode) Color(0x14FFFFFF) else Color(0x0A000000)

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Bars row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Bottom
    ) {
      trends.forEach { trend ->
        val incomeHeightFraction = (trend.income / maxVal).toFloat().coerceIn(0.04f, 1f)
        val expenseHeightFraction = (trend.expense / maxVal).toFloat().coerceIn(0.04f, 1f)

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom,
          modifier = Modifier.weight(1f)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.height(110.dp)
          ) {
            // Income bar
            Box(
              modifier = Modifier
                .width(10.dp)
                .fillMaxHeight(incomeHeightFraction)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(AccentGreen)
            )

            // Expense bar
            Box(
              modifier = Modifier
                .width(10.dp)
                .fillMaxHeight(expenseHeightFraction)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(AccentRed)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = trend.monthName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
          )
        }
      }
    }
  }
}
