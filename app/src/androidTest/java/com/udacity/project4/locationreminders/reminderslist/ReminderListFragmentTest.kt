package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock

import org.mockito.Mockito.verify
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.koin.core.context.GlobalContext
import org.koin.test.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: AutoCloseKoinTest() {
    private var reminderList= mutableListOf<ReminderDTO>(
        ReminderDTO(
            title = "title",
            description = "description",
            location ="location" ,
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

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @Before
    fun init() {
        stopKoin()
        appContext = getApplicationContext()
        val newModule = module {
            viewModel {
                RemindersListViewModel(appContext, get() as ReminderDataSource)
            }
            single {
                SaveReminderViewModel(appContext, get() as ReminderDataSource)
            }
            single {
                LocalDB.createRemindersDao(appContext)
            }
            single {
                RemindersLocalRepository(get()) as ReminderDataSource
            }
        }
        startKoin {
            modules(newModule)
        }
        repository = get()

        runBlocking {
            repository.deleteAllReminders()
        }

    }





    @Test
    fun check_fragment_in_ui(){
        // add reminder item in DataBase
        runBlocking {
            repository.saveReminder(reminderList[0])

        }
        //launch fragment scenario
         launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        //check if data displayed as expected
        onView(withId(R.id.noDataTextView)).check(matches(not(isDisplayed())))
        onView(withText(reminderList[0].title)).check(matches(isDisplayed()))
        onView(withText(reminderList[0].description)).check(matches(isDisplayed()))
        onView(withText(reminderList[0].location)).check(matches(isDisplayed()))

    }

    @Test
    fun delete_All_Reminders()  {
        //delete all item from dataBase
        runBlocking {
            repository.deleteAllReminders()

        }

        //launch fragment scenario
        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        fragmentScenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }

        //check if no data displayed
        onView(withText(R.string.no_data)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withText(reminderList[0].title)).check(doesNotExist())
    }

    @Test
    fun click_Fab_NavigateTo_ReminderFragment()  {

        //launch fragment scenario
        val fragmentScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        fragmentScenario.onFragment { Navigation.setViewNavController(it.view!!, navController) }
        //click in add reminder button
        onView(withId(R.id.addReminderFAB)).perform(click())
        //we will go to to set value in Reminder List Fragment
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }





}