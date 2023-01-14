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
    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase



    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        fakeDataSource= FakeDataSource(database.reminderDao())

        repository= RemindersLocalRepository(database.reminderDao(),Dispatchers.Main)

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
        database.close()
    }

    @Test
    fun `when list is null or empty return no reminder to return `() = runBlockingTest {
        repository.saveReminder(reminderList[0])
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),
            fakeDataSource)


        reminderListViewModel.loadReminders()

        assertThat( reminderListViewModel.remindersList.getOrAwaitValueTest(), not (emptyList()))


    }
    @Test
    fun `when list have data return the data `() = runBlockingTest {
        repository.saveReminder(reminderList[0])
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        reminderListViewModel.loadReminders()

        assertThat( reminderListViewModel.remindersList.getOrAwaitValueTest(), (not(emptyList())))
        assertThat( reminderListViewModel.remindersList.getOrAwaitValueTest().size, `is`(1))
    }
    @Test
    fun `when list not loaded yet show loading `()= runBlockingTest{
        repository.saveReminder(ReminderDTO("","","",0.0,0.0,""))
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValueTest(), `is`(true))

    }
    fun `when list is null or empty show error `()= runBlockingTest{
        repository.saveReminder(ReminderDTO("","","",0.0,0.0,""))
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showErrorMessage.getOrAwaitValueTest(), `is`("No reminders to return"))

    }



}