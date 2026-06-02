package ru.studyplanner.mobile.repository

import ru.studyplanner.mobile.model.Reminder
import ru.studyplanner.mobile.model.ReminderForm
import ru.studyplanner.mobile.remote.ReminderApi

class ReminderRepository(
    private val reminderApi: ReminderApi
) {
    suspend fun getReminders(assignmentId: Long): ResultState<List<Reminder>> {
        return try {
            ResultState.Success(reminderApi.getReminders(assignmentId).map { it.toDomain() })
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось загрузить напоминания"))
        }
    }

    suspend fun createReminder(assignmentId: Long, form: ReminderForm): ResultState<Reminder> {
        return try {
            val remote = reminderApi.createReminder(assignmentId, form.toCreateDto())
            ResultState.Success(remote.toDomain())
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось создать напоминание"))
        }
    }

    suspend fun deleteReminder(id: Long): ResultState<Unit> {
        return try {
            reminderApi.deleteReminder(id)
            ResultState.Success(Unit)
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось удалить напоминание"))
        }
    }
}
