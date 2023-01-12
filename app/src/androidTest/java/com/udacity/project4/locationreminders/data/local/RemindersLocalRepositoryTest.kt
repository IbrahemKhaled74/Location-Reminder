package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

@get:Rule
var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private var reminderList= mutableListOf<ReminderDTO>(
        ReminderDTO(
            id = "1",
            title = "test1",
            description = "test1",
            location ="test2" ,
            latitude =15.0 ,
            longitude = 15.0
        ),
        ReminderDTO(
            id = "2",
            title = "test2",
            description = "test2",
            location ="test2" ,
            latitude =14.0 ,
            longitude = 14.0
        ),
        ReminderDTO(
            id = "3",
            title = "test3",
            description = "test3",
            location ="test3" ,
            latitude =14.0 ,
            longitude = 14.0
        )
    )

    private lateinit var localRepo: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup(){
        database=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
        RemindersDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        localRepo=RemindersLocalRepository(database.reminderDao(),Dispatchers.Main)

    }
    @After
    fun finish(){
        database.close()
    }
    @Test
    fun save_Reminder_List_return_RemindersByID()= runBlockingTest{
        // add reminder item in DataBase
        localRepo.saveReminder(reminderList[0])

        //return list from DataBase
        val item=localRepo.getReminder("1")
        item as Result.Success

        //check if list return expected values
        assertThat(reminderList[0].id, `is`(item.data.id))
        assertThat(reminderList[0].title, `is`(item.data.title))
        assertThat(reminderList[0].description, `is`(item.data.description))
        assertThat(reminderList[0].location, `is`(item.data.location))
        assertThat(reminderList[0].latitude, `is`(item.data.latitude))
        assertThat(reminderList[0].longitude, `is`(item.data.longitude))
    }
    @Test
    fun save_Reminder_List_return_Reminders()= runBlockingTest{
        // add reminder item in DataBase
        localRepo.saveReminder(reminderList[0])

        //return list from DataBase
        val item=localRepo.getReminders()
        item as Result.Success

        //check if list return expected values
        assertThat(item.data.size, `is`(1))
    }



    @Test
    fun delete_Reminder_List_return_size_0()= runBlockingTest{
        // add reminder item in DataBase
        localRepo.saveReminder(reminderList[0])
        localRepo.saveReminder(reminderList[1])
        localRepo.saveReminder(reminderList[2])

        //delete all item from dataBase
        localRepo.deleteAllReminders()

        //return list from DataBase
        val item=localRepo.getReminders()
        item as Result.Success

        //check if list return expected values
        assertThat(item.data.size, `is`(0))
    }
    @Test
    fun get_Empty_Reminder_Return_Error() = runBlocking {
        //delete all item from dataBase
        localRepo.deleteAllReminders()
        //return list from dataBase but no item
        val res = localRepo.getReminder(reminderList[0].id) as Result.Error
        //show error massage
        assertThat(res.message, `is`("Reminder not found!"))
    }







}