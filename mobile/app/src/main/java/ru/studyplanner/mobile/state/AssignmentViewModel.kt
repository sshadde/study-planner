package ru.studyplanner.mobile.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.studyplanner.mobile.model.Assignment
import ru.studyplanner.mobile.model.AssignmentFilter
import ru.studyplanner.mobile.model.AssignmentForm
import ru.studyplanner.mobile.model.AssignmentStatus
import ru.studyplanner.mobile.repository.AssignmentRepository
import ru.studyplanner.mobile.repository.ResultState

data class AssignmentListUiState(
    val loading: Boolean = false,
    val assignments: List<Assignment> = emptyList(),
    val filter: AssignmentFilter = AssignmentFilter(),
    val offline: Boolean = false,
    val error: String? = null
)

data class AssignmentDetailsUiState(
    val loading: Boolean = false,
    val assignment: Assignment? = null,
    val error: String? = null
)

class AssignmentViewModel(
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {
    private val mutableListState = MutableStateFlow(AssignmentListUiState())
    val listState: StateFlow<AssignmentListUiState> = mutableListState.asStateFlow()

    private val mutableDetailsState = MutableStateFlow(AssignmentDetailsUiState())
    val detailsState: StateFlow<AssignmentDetailsUiState> = mutableDetailsState.asStateFlow()

    fun loadAssignments(filter: AssignmentFilter = mutableListState.value.filter) {
        viewModelScope.launch {
            mutableListState.value = mutableListState.value.copy(loading = true, filter = filter, error = null)
            mutableListState.value = when (val result = assignmentRepository.getAssignments(filter)) {
                is ResultState.Success -> AssignmentListUiState(
                    assignments = result.data,
                    filter = filter,
                    offline = result.fromCache
                )
                is ResultState.Error -> mutableListState.value.copy(loading = false, error = result.message)
            }
        }
    }

    fun synchronizeAndLoad() {
        viewModelScope.launch {
            mutableListState.value = mutableListState.value.copy(loading = true, error = null)
            assignmentRepository.syncPendingOperations()
            mutableListState.value = when (val result = assignmentRepository.getAssignments(mutableListState.value.filter)) {
                is ResultState.Success -> mutableListState.value.copy(
                    loading = false,
                    assignments = result.data,
                    offline = result.fromCache,
                    error = null
                )
                is ResultState.Error -> mutableListState.value.copy(loading = false, error = result.message)
            }
        }
    }

    fun loadAssignment(id: Long) {
        viewModelScope.launch {
            mutableDetailsState.value = AssignmentDetailsUiState(loading = true)
            mutableDetailsState.value = when (val result = assignmentRepository.getAssignment(id)) {
                is ResultState.Success -> AssignmentDetailsUiState(assignment = result.data)
                is ResultState.Error -> AssignmentDetailsUiState(error = result.message)
            }
        }
    }

    fun createAssignment(form: AssignmentForm, onDone: () -> Unit) {
        viewModelScope.launch {
            when (val result = assignmentRepository.createAssignment(form)) {
                is ResultState.Success -> {
                    loadAssignments()
                    onDone()
                }
                is ResultState.Error -> {
                    mutableDetailsState.value = mutableDetailsState.value.copy(error = result.message)
                    loadAssignments()
                }
            }
        }
    }

    fun updateAssignment(id: Long, form: AssignmentForm, onDone: () -> Unit) {
        viewModelScope.launch {
            when (val result = assignmentRepository.updateAssignment(id, form)) {
                is ResultState.Success -> {
                    loadAssignments()
                    onDone()
                }
                is ResultState.Error -> {
                    mutableDetailsState.value = mutableDetailsState.value.copy(error = result.message)
                    loadAssignments()
                }
            }
        }
    }

    fun changeStatus(id: Long, status: AssignmentStatus) {
        viewModelScope.launch {
            assignmentRepository.changeStatus(id, status)
            loadAssignments()
            loadAssignment(id)
        }
    }

    fun deleteAssignment(id: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            assignmentRepository.deleteAssignment(id)
            loadAssignments()
            onDone()
        }
    }
}
