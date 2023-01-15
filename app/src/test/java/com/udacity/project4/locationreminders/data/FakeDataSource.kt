package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource( private var reminders: MutableList<ReminderDTO>? = mutableListOf()
) : ReminderDataSource {

    private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }


    override suspend fun getReminders(): Result<List<ReminderDTO>> =
        if (shouldReturnError) {
            Result.Error("Error occurred")
        } else {
            Result.Success(ArrayList(reminders))
        }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> =
        if (shouldReturnError) {
            Result.Error("Error occurred")
        } else {
            val reminder = reminders?.find { it.id == id }

            if (reminder == null) {
                Result.Error("Not found")
            } else {
                Result.Success(reminder)
            }
        }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}