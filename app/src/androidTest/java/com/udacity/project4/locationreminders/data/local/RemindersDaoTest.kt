package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import junit.framework.Assert.assertNull

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java
        ).build()
    }

    @After
    fun finish() {
        database.close()
    }

    @Test
    fun check_getReminders_DB() = runBlockingTest {
        // add reminder item in DataBase
        val reminder = mutableListOf(
            ReminderDTO(
                title = "test1",
                description = "test1",
                location = "test2",
                latitude = 15.0,
                longitude = 15.0
            )
        )
        database.reminderDao().saveReminder(reminder[0])
        //return list from DataBase
        val reminderItem = database.reminderDao().getReminders()
        //check if list return expected values
        assertThat(reminder.size, `is`(1))
        assertThat(reminder[0].id, `is`(reminderItem[0].id))
        assertThat(reminder[0].title, `is`(reminderItem[0].title))
        assertThat(reminder[0].description, `is`(reminderItem[0].description))
        assertThat(reminder[0].location, `is`(reminderItem[0].location))
        assertThat(reminder[0].latitude, `is`(reminderItem[0].latitude))
        assertThat(reminder[0].longitude, `is`(reminderItem[0].longitude))
    }

    @Test
    fun check_getRemindersByID_DB() = runBlockingTest {
        // add reminder item in DataBase

        val reminder = mutableListOf(
            ReminderDTO(
                id = "1",
                title = "test1",
                description = "test1",
                location = "test2",
                latitude = 15.0,
                longitude = 15.0
            )
        )
        database.reminderDao().saveReminder(reminder[0])
        //return list from DataBase ById
        val reminderItem = database.reminderDao().getReminderById("1")
        //check if list return expected values

        assertThat(reminder.size, `is`(1))
        assertThat(reminder[0].id, `is`(reminderItem?.id))
        assertThat(reminder[0].title, `is`(reminderItem?.title))
        assertThat(reminder[0].description, `is`(reminderItem?.description))
        assertThat(reminder[0].location, `is`(reminderItem?.location))
        assertThat(reminder[0].latitude, `is`(reminderItem?.latitude))
        assertThat(reminder[0].longitude, `is`(reminderItem?.longitude))
    }

    @Test
    fun check_Delete_All_Item_DB() = runBlockingTest {
        // add reminder item in DataBase

        val reminder = mutableListOf(
            ReminderDTO(
                id = "1",
                title = "test1",
                description = "test1",
                location = "test2",
                latitude = 15.0,
                longitude = 15.0
            )
        )
        database.reminderDao().saveReminder(reminder[0])
        //Delete All Items
        database.reminderDao().deleteAllReminders()

        //return list from DataBase
        val reminders = database.reminderDao().getReminders()

        //check if list return expected values And list is empty
        assertThat(reminders.isEmpty(), `is`(true))
    }

    @Test
    fun check_NoItem_In_DB() = runBlockingTest {
        //return list from DataBase
        val reminder = database.reminderDao().getReminders()

        //check if list return expected values
        assertThat(reminder.isEmpty(), `is`(true))


    }

    @Test
    fun check_item_Not_Exist_Return_Null() = runBlockingTest {
        // add reminder item in DataBase

        val reminder = mutableListOf(
            ReminderDTO(
                id = "1",
                title = "test1",
                description = "test1",
                location = "test2",
                latitude = 15.0,
                longitude = 15.0
            )
        )
        database.reminderDao().saveReminder(reminder[0])
        //return list from DataBase and chose a wrong id

        val reminders = database.reminderDao().getReminderById("5")
        //check if list return expected values

        assertNull(reminders)


    }


}