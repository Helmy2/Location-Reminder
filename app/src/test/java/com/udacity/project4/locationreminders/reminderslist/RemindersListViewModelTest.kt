package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            getApplicationContext(),
            dataSource
        )
    }

    @After
    fun stopDown() {
        stopKoin()
    }

    @Test
    fun loadRemindersTest_emptyList() = runBlocking {
        viewModel.loadReminders()

        assertEquals(emptyList<ReminderDataItem>(), viewModel.remindersList.getOrAwaitValue())
    }

    @Test
    fun loadRemindersTest_twoElementList() = runBlocking {
        val reminder1 = ReminderDTO(
            "title1",
            "description1",
            "location1",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        val reminder2 = ReminderDTO(
            "title2",
            "description2",
            "location2",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        val reminderList = mutableListOf(reminder1, reminder2)
        dataSource = FakeDataSource(reminderList)
        viewModel = RemindersListViewModel(
            getApplicationContext(),
            dataSource
        )

        viewModel.loadReminders()

        assertNotEquals(emptyList<ReminderDataItem>(), viewModel.remindersList.getOrAwaitValue())
        assertEquals(reminderList.size, viewModel.remindersList.getOrAwaitValue().size)
    }

    @Test
    fun checkLoading() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertTrue(viewModel.showLoading.getOrAwaitValue())

        mainCoroutineRule.resumeDispatcher()
        assertFalse(viewModel.showLoading.getOrAwaitValue())
    }


    @Test
    fun checkError() {
        dataSource.setReturnError(true)

        viewModel.loadReminders()
        assertEquals("Location reminder data not found",viewModel.showSnackBar.getOrAwaitValue())
    }
}