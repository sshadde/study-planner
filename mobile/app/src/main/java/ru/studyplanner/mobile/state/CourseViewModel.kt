package ru.studyplanner.mobile.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.studyplanner.mobile.model.Course
import ru.studyplanner.mobile.model.CourseForm
import ru.studyplanner.mobile.repository.CourseRepository
import ru.studyplanner.mobile.repository.ResultState

data class CourseUiState(
    val loading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val offline: Boolean = false,
    val saving: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

class CourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private val mutableState = MutableStateFlow(CourseUiState())
    val state: StateFlow<CourseUiState> = mutableState.asStateFlow()

    fun loadCourses() {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(loading = true, error = null)
            mutableState.value = when (val result = courseRepository.getCourses()) {
                is ResultState.Success -> CourseUiState(
                    courses = result.data,
                    offline = result.fromCache
                )
                is ResultState.Error -> CourseUiState(error = result.message)
            }
        }
    }

    fun createCourse(form: CourseForm) {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(saving = true, error = null)
            mutableState.value = when (val result = courseRepository.createCourse(form)) {
                is ResultState.Success -> mutableState.value.copy(
                    saving = false,
                    courses = (mutableState.value.courses + result.data).distinctBy { it.id }.sortedBy { it.title },
                    successMessage = "Дисциплина добавлена",
                    error = null
                )
                is ResultState.Error -> mutableState.value.copy(
                    saving = false,
                    successMessage = null,
                    error = result.message
                )
            }
        }
    }

    fun updateCourse(id: Long, form: CourseForm) {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(saving = true, error = null)
            mutableState.value = when (val result = courseRepository.updateCourse(id, form)) {
                is ResultState.Success -> mutableState.value.copy(
                    saving = false,
                    courses = mutableState.value.courses
                        .map { course -> if (course.id == id) result.data else course }
                        .sortedBy { it.title },
                    successMessage = "Дисциплина обновлена",
                    error = null
                )
                is ResultState.Error -> mutableState.value.copy(
                    saving = false,
                    successMessage = null,
                    error = result.message
                )
            }
        }
    }

    fun deleteCourse(id: Long) {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(saving = true, error = null)
            mutableState.value = when (val result = courseRepository.deleteCourse(id)) {
                is ResultState.Success -> mutableState.value.copy(
                    saving = false,
                    courses = mutableState.value.courses.filterNot { it.id == id },
                    successMessage = "Дисциплина удалена",
                    error = null
                )
                is ResultState.Error -> mutableState.value.copy(
                    saving = false,
                    successMessage = null,
                    error = result.message
                )
            }
        }
    }
}
