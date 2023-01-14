package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.getOrAwaitValueTest
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])

class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private var reminderDataItemList= mutableListOf<ReminderDataItem>(
        ReminderDataItem(
        title ="" ,
        description ="test" ,
        longitude = 13.0,
        latitude =14.0,
        location ="55"
        ) ,
        ReminderDataItem(
        title ="test1" ,
        description ="test1" ,
        longitude = 13.0,
        latitude =14.0,
        location =""
        ) ,ReminderDataItem(
        title ="test2" ,
        description ="test2" ,
        longitude = 13.0,
        latitude =14.0,
        location ="test2"
        )
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        fakeDataSource= FakeDataSource(database.reminderDao())

        repository= RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)

    }



    @After
    fun finish() {
        stopKoin()
        database.close()
    }

    @Test
    fun `when list is no title   return enter your title `() = runBlockingTest {
        repository.saveReminder(ReminderDTO("","desc","test",0.0,0.0))

        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        saveReminderViewModel.validateEnteredData(reminderDataItemList[0])

        MatcherAssert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValueTest(), Matchers.`is`(
            R.string.err_enter_title)
        )

    }
    @Test
    fun `when list is no location   return enter your title `() = runBlockingTest {
        repository.saveReminder(ReminderDTO("ddd","desc","",0.0,0.0))
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        saveReminderViewModel.validateEnteredData(reminderDataItemList[1])

        MatcherAssert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValueTest(), Matchers.`is`(
            R.string.err_select_location)
        )

    }
    @Test
    fun `when list is not loaded  show loading `() = runBlockingTest {
        repository.saveReminder(ReminderDTO("dd","desc","dd",0.0,0.0))

        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminderDataItemList[2])

        MatcherAssert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValueTest(), Matchers.`is`(
            true)
        )

    }



}