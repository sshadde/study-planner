package ru.studyplanner.mobile.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import ru.studyplanner.mobile.model.AssignmentForm
import ru.studyplanner.mobile.model.AssignmentPriority
import ru.studyplanner.mobile.state.AssignmentViewModel
import ru.studyplanner.mobile.state.CourseViewModel
import ru.studyplanner.mobile.ui.DateTimePickerField
import ru.studyplanner.mobile.ui.toRuLabel
import java.time.Instant
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentEditScreen(
    assignmentId: Long?,
    assignmentViewModel: AssignmentViewModel,
    courseViewModel: CourseViewModel,
    onBack: () -> Unit
) {
    val coursesState by courseViewModel.state.collectAsState()
    val detailsState by assignmentViewModel.detailsState.collectAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCourseId by remember { mutableStateOf<Long?>(null) }
    var selectedPriority by remember { mutableStateOf(AssignmentPriority.MEDIUM) }
    var deadline by remember { mutableStateOf(Instant.now().plus(7, ChronoUnit.DAYS)) }
    var formInitialized by remember(assignmentId) { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(assignmentId) {
        courseViewModel.loadCourses()
        if (assignmentId != null) {
            assignmentViewModel.loadAssignment(assignmentId)
        }
    }

    LaunchedEffect(coursesState.courses, detailsState.assignment) {
        val assignment = detailsState.assignment
        if (assignmentId != null && assignment != null && !formInitialized) {
            title = assignment.title
            description = assignment.description.orEmpty()
            selectedCourseId = assignment.courseId
            selectedPriority = assignment.priority
            deadline = assignment.dueAt
            formInitialized = true
        }
        if (assignmentId == null && selectedCourseId == null) {
            selectedCourseId = coursesState.courses.firstOrNull()?.id
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (assignmentId == null) "Новое задание" else "Редактирование") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            if (coursesState.courses.isEmpty()) {
                Text(
                    "Сначала добавьте дисциплину на вкладке \"Дисциплины\".",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(12.dp))
            }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Название задания") }
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                label = { Text("Описание") }
            )
            Spacer(Modifier.height(12.dp))
            DropdownSelector(
                label = "Дисциплина",
                selectedText = coursesState.courses.firstOrNull { it.id == selectedCourseId }?.title ?: "Выберите дисциплину",
                options = coursesState.courses,
                optionText = { course -> course.title },
                enabled = coursesState.courses.isNotEmpty(),
                onSelect = { course -> selectedCourseId = course.id }
            )
            Spacer(Modifier.height(12.dp))
            DropdownSelector(
                label = "Приоритет",
                selectedText = selectedPriority.toRuLabel(),
                options = AssignmentPriority.entries,
                optionText = { priority -> priority.toRuLabel() },
                onSelect = { priority -> selectedPriority = priority }
            )
            Spacer(Modifier.height(12.dp))
            DateTimePickerField(
                label = "Дедлайн",
                value = deadline,
                onValueChange = { deadline = it }
            )
            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            detailsState.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val courseId = selectedCourseId
                    when {
                        title.isBlank() -> error = "Введите название задания"
                        courseId == null -> error = "Выберите дисциплину"
                        deadline.isBefore(Instant.now()) -> error = "Дедлайн не может быть в прошлом"
                        else -> {
                            error = null
                            val form = AssignmentForm(
                                courseId = courseId,
                                title = title,
                                description = description,
                                dueAt = deadline,
                                priority = selectedPriority
                            )
                            if (assignmentId == null) {
                                assignmentViewModel.createAssignment(form, onBack)
                            } else {
                                assignmentViewModel.updateAssignment(assignmentId, form, onBack)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = coursesState.courses.isNotEmpty()
            ) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
private fun <T> DropdownSelector(
    label: String,
    selectedText: String,
    options: List<T>,
    optionText: (T) -> String,
    enabled: Boolean = true,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(4.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(selectedText, modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionText(option)) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
