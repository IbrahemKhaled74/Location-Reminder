package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.getOrAwaitValueTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])

class RemindersListViewModelTest {

    private lateinit var reminderListViewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource



    @Before
    fun model(){ stopKoin()
        fakeDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule=MainCoroutineRule()

    private var reminderList= mutableListOf(
        ReminderDTO(
            title = "test1",
            description = "test1",
            location ="test2" ,
            latitude =15.0 ,
            longitude = 15.0
        ),
        ReminderDTO(
            title = "test2",
            description = "test2",
            location ="test2" ,
            latitude =14.0 ,
            longitude = 14.0
        ),
        ReminderDTO(
            title = "test3",
            description = "test3",
            location ="test3" ,
            latitude =14.0 ,
            longitude = 14.0
        )
    )

    @After
    fun finish() {
        stopKoin()
    }

    @Test
    fun `when list is null or empty return no reminder to return `() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        fakeDataSource.setError(false)


        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.remindersList.getOrAwaitValueTest().size, `is` (0))
        assertThat(reminderListViewModel.showNoData.getOrAwaitValueTest(), `is` (true))


    }
    @Test
    fun `when list have data return the data `() = runBlockingTest {

        fakeDataSource.saveReminder(reminderList[0])

        reminderListViewModel.loadReminders()

        assertThat( reminderListViewModel.remindersList.getOrAwaitValueTest(), (not(emptyList())))
        assertThat( reminderListViewModel.remindersList.getOrAwaitValueTest().size, `is`(1))
    }
    @Test
    fun `when list not loaded yet show loading `()= runBlockingTest{
        fakeDataSource.saveReminder(reminderList[0])

        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValueTest(), `is`(true))

    }

    @Test
    fun loadRemindersShouldReturnError()= runBlockingTest{

        fakeDataSource.setError(true)

        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValueTest(), `is`("Tasks not found"))
    }



}