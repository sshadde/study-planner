package ru.studyplanner.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.studyplanner.mobile.model.Course
import ru.studyplanner.mobile.model.CourseForm
import ru.studyplanner.mobile.state.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(viewModel: CourseViewModel) {
    val state by viewModel.state.collectAsState()
    var editingCourseId by remember { mutableStateOf<Long?>(null) }
    var title by remember { mutableStateOf("") }
    var teacherName by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCourses()
    }

    LaunchedEffect(state.successMessage) {
        if (state.successMessage != null) {
            editingCourseId = null
            title = ""
            teacherName = ""
            semester = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дисциплины") },
                actions = {
                    IconButton(onClick = { viewModel.loadCourses() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            CourseFormBlock(
                editing = editingCourseId != null,
                title = title,
                teacherName = teacherName,
                semester = semester,
                saving = state.saving,
                localError = localError,
                backendError = state.error,
                successMessage = state.successMessage,
                onTitleChange = { title = it },
                onTeacherNameChange = { teacherName = it },
                onSemesterChange = { semester = it.filter(Char::isDigit) },
                onCancel = {
                    editingCourseId = null
                    title = ""
                    teacherName = ""
                    semester = ""
                    localError = null
                },
                onSubmit = {
                    val semesterNumber = semester.toIntOrNull()
                    localError = validateCourse(title, teacherName, semester, semesterNumber)
                    if (localError == null) {
                        val form = CourseForm(
                            title = title,
                            teacherName = teacherName,
                            semester = semesterNumber,
                            color = "#4F7CAC"
                        )
                        val id = editingCourseId
                        if (id == null) {
                            viewModel.createCourse(form)
                        } else {
                            viewModel.updateCourse(id, form)
                        }
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            if (state.offline) {
                AssistChip(onClick = {}, label = { Text("Показаны сохраненные данные") })
            }
            when {
                state.loading -> CircularProgressIndicator(Modifier.padding(24.dp))
                state.courses.isEmpty() -> Text(
                    "Дисциплин пока нет. Добавьте предмет, чтобы создавать задания.",
                    style = MaterialTheme.typography.bodyMedium
                )
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(state.courses, key = { it.id }) { course ->
                        CourseCard(
                            course = course,
                            onEdit = {
                                editingCourseId = course.id
                                title = course.title
                                teacherName = course.teacherName.orEmpty()
                                semester = course.semester?.toString().orEmpty()
                                localError = null
                            },
                            onDelete = { viewModel.deleteCourse(course.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseFormBlock(
    editing: Boolean,
    title: String,
    teacherName: String,
    semester: String,
    saving: Boolean,
    localError: String?,
    backendError: String?,
    successMessage: String?,
    onTitleChange: (String) -> Unit,
    onTeacherNameChange: (String) -> Unit,
    onSemesterChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Название дисциплины") }
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = teacherName,
        onValueChange = onTeacherNameChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Преподаватель") }
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = semester,
        onValueChange = onSemesterChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Семестр") },
        supportingText = { Text("Число от 1 до 10") }
    )
    localError?.let {
        Spacer(Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.error)
    }
    successMessage?.let {
        Spacer(Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.primary)
    }
    backendError?.let {
        Spacer(Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.error)
    }
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp),
            enabled = !saving
        ) {
            if (saving) {
                CircularProgressIndicator()
            } else {
                Text(
                    if (editing) "Сохранить" else "Добавить",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (editing) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                Text("Отмена")
            }
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    course.teacherName?.let {
                        Text("Преподаватель: $it")
                    }
                    course.semester?.let {
                        Text("Семестр: $it")
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                    }
                }
            }
        }
    }
}

private fun validateCourse(title: String, teacherName: String, semester: String, semesterNumber: Int?): String? {
    return when {
        title.isBlank() -> "Введите название дисциплины"
        title.length > 160 -> "Название дисциплины не должно быть длиннее 160 символов"
        teacherName.length > 160 -> "Имя преподавателя не должно быть длиннее 160 символов"
        semester.isNotBlank() && semesterNumber == null -> "Семестр должен быть числом"
        semesterNumber != null && semesterNumber !in 1..10 -> "Семестр должен быть от 1 до 10"
        else -> null
    }
}
