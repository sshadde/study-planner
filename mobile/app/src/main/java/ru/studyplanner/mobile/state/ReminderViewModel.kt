package ru.studyplanner.mobile.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.studyplanner.mobile.model.Reminder
import ru.studyplanner.mobile.model.ReminderForm
import ru.studyplanner.mobile.repository.ReminderRepository
import ru.studyplanner.mobile.repository.ResultState

data class ReminderUiState(
    val loading: Boolean = false,
    val reminders: List<Reminder> = emptyList(),
    val saving: Boolean = false,
    val error: String? = null
)

class ReminderViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    private val mutableState = MutableStateFlow(ReminderUiState())
    val state: StateFlow<ReminderUiState> = mutableState.asStateFlow()

    fun loadReminders(assignmentId: Long) {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(loading = true, error = null)
            mutableState.value = when (val result = reminderRepository.getReminders(assignmentId)) {
                is ResultState.Success -> ReminderUiState(reminders = result.data)
                is ResultState.Error -> mutableState.value.copy(loading = false, error = result.message)
            }
        }
    }

    fun createReminder(assignmentId: Long, form: ReminderForm) {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(saving = true, error = null)
            mutableState.value = when (val result = reminderRepository.createReminder(assignmentId, form)) {
                is ResultState.Success -> mutableState.value.copy(
                    saving = false,
                    reminders = (mutableState.value.reminders + result.data).sortedBy { it.remindAt },
                    error = null
                )
                is ResultState.Error -> mutableState.value.copy(saving = false, error = result.message)
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(saving = true, error = null)
            mutableState.value = when (val result = reminderRepository.deleteReminder(id)) {
                is ResultState.Success -> mutableState.value.copy(
                    saving = false,
                    reminders = mutableState.value.reminders.filterNot { it.id == id },
                    error = null
                )
                is ResultState.Error -> mutableState.value.copy(saving = false, error = result.message)
            }
        }
    }
}
