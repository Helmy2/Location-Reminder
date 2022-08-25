package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataSource(
    private val reminderList: MutableList<ReminderDTO> = mutableListOf()
) : ReminderDataSource {

    private var shouldReturnError = false
    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError)
            return Result.Error("Location reminder data not found")

        return Result.Success(reminderList)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError)
            return Result.Error("Location reminder data not found")

        val reminder = reminderList.find { it.id == id }
        return if (reminder != null)
            Result.Success(reminder)
        else
            Result.Error("Reminder not found", 404)
    }

    override suspend fun deleteAllReminders() {
        reminderList.clear()
    }
}