package com.udacity.project4

import android.app.Application
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get


@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @After
    fun finish(){
        stopKoin()
    }

    @Test
    fun check_add_reminder() {
        //Launch Activity Scenario
        val activityScenario = launchActivity<RemindersActivity>()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        //Click to Fab Button
        onView(withId(R.id.addReminderFAB)).perform(click())

        //Enter title
        onView(withId(R.id.reminderTitle)).perform(typeText("title"))
        onView(withText("title")).check(matches(isDisplayed()))

        //Enter Description
        onView(withId(R.id.reminderDescription)).perform(typeText("desc"))
        onView(withText("desc")).check(matches(isDisplayed()))
        //close keyboard
        closeSoftKeyboard()

        //click in select location button navigate to map to chose my location
        onView(withId(R.id.selectLocation)).perform(click())

        val uiDevice = UiDevice.getInstance(getInstrumentation())

        SystemClock.sleep(5000)
        //click to chose my current location
        uiDevice.findObject(UiSelector().descriptionContains("My Location")).click()

        SystemClock.sleep(3000)

        //click in map to select location
        onView(withId(R.id.mapFragment)).perform(click())
        SystemClock.sleep(1000)
        //click in save button navigate back
        onView(withId(R.id.save_button)).perform(click())

        //click in save reminder navigate to activity
        onView(withId(R.id.saveReminder)).perform(click())
        //check the data is displayed as expected
        onView(withText("desc")).check(matches(isDisplayed()))
        //close Scenario
        activityScenario.close()
    }



    @Test
    fun show_Snack_When_NoLocation_Added(){

        //Launch Activity Scenario
        val scenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)
        //click in map to select location
        onView(withId(R.id.addReminderFAB)).perform(click())
        //click in save button navigate back
        onView(withId(R.id.saveReminder)).perform(click())
        //check error massage when no location is founded
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(R.string.err_enter_title)))
        //close Scenario
        scenario.close()
    }





}
