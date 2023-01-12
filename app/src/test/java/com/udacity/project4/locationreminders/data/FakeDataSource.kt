package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource( private var reminderList:MutableList<ReminderDTO>?) : ReminderDataSource {

private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
         if (shouldReturnError){
            Result.Error("No Reminders")
        }
            reminderList?.let {
                return Result.Success(it)
            }
        return Result.Error("No reminders to return")

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderList.let {
           it?.add(reminder)
        }

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
         if (shouldReturnError){
            Result.Error("No Reminders")
        }
        reminderList?.let {
            return Result.Success(it[0])
        }
        return Result.Error("No reminders to return")

    }

    override suspend fun deleteAllReminders() {
        reminderList= mutableListOf()
    }


}