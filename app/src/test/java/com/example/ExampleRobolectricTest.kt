package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.WalletDatabase
import com.example.data.model.TransactionEntity
import com.example.data.repository.WalletRepository
import com.example.ui.viewmodel.WalletViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: WalletDatabase
  private lateinit var repository: WalletRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, WalletDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = WalletRepository(database)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Wallet", appName)
  }

  @Test
  fun `room database inserts and queries transactions`() = runBlocking {
    val txDao = database.transactionDao()
    val initialList = txDao.getAllTransactions().first()
    assertEquals(0, initialList.size)

    val tx = TransactionEntity(
      title = "Apple Store MacBook",
      amount = 1999.0,
      type = "EXPENSE",
      category = "shopping",
      accountId = 1,
      timestamp = System.currentTimeMillis(),
      note = "New work gear"
    )
    val insertedId = txDao.insertTransaction(tx)
    assertTrue(insertedId > 0)

    val updatedList = txDao.getAllTransactions().first()
    assertEquals(1, updatedList.size)
    assertEquals("Apple Store MacBook", updatedList[0].title)
    assertEquals(1999.0, updatedList[0].amount, 0.001)
  }

  @Test
  fun `viewmodel auth flow transitions correctly`() {
    val viewModel = WalletViewModel(repository)
    assertTrue(viewModel.isAuthenticated.value)

    viewModel.signOut()
    assertFalse(viewModel.isAuthenticated.value)

    viewModel.login("alex.morgan@example.com", "Alex Morgan")
    assertTrue(viewModel.isAuthenticated.value)
    assertEquals("Alex Morgan", viewModel.user.value.name)
    assertEquals("alex.morgan@example.com", viewModel.user.value.email)
  }

  @Test
  fun `viewmodel analytics timeframe updates`() {
    val viewModel = WalletViewModel(repository)
    assertEquals("month", viewModel.analyticsTimeframe.value)

    viewModel.setAnalyticsTimeframe("year")
    assertEquals("year", viewModel.analyticsTimeframe.value)
  }

  @Test
  fun `viewmodel view routing changes to search and menu destinations`() {
    val viewModel = WalletViewModel(repository)
    assertEquals("dashboard", viewModel.activeView.value)

    viewModel.setView("search")
    assertEquals("search", viewModel.activeView.value)

    viewModel.setView("analytics")
    assertEquals("analytics", viewModel.activeView.value)

    viewModel.setView("history")
    assertEquals("history", viewModel.activeView.value)

    viewModel.setView("profile")
    assertEquals("profile", viewModel.activeView.value)
  }
}
