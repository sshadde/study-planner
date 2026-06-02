package ru.studyplanner.mobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ru.studyplanner.mobile.model.Assignment
import ru.studyplanner.mobile.model.AssignmentFilter
import ru.studyplanner.mobile.model.AssignmentPriority
import ru.studyplanner.mobile.model.AssignmentStatus
import ru.studyplanner.mobile.model.Course
import ru.studyplanner.mobile.state.AssignmentViewModel
import ru.studyplanner.mobile.state.CourseViewModel
import ru.studyplanner.mobile.ui.toRuDateTimeText
import ru.studyplanner.mobile.ui.toRuLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentListScreen(
    viewModel: AssignmentViewModel,
    courseViewModel: CourseViewModel,
    onCreate: () -> Unit,
    onOpen: (Long) -> Unit
) {
    val state by viewModel.listState.collectAsState()
    val coursesState by courseViewModel.state.collectAsState()
    var query by remember { mutableStateOf(state.filter.query) }
    var statusFilter by remember { mutableStateOf<AssignmentStatus?>(null) }
    var priorityFilter by remember { mutableStateOf<AssignmentPriority?>(null) }
    var courseFilter by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        courseViewModel.loadCourses()
        viewModel.loadAssignments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задания") },
                actions = {
                    IconButton(onClick = { viewModel.synchronizeAndLoad() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate) {
                Icon(Icons.Filled.Add, contentDescription = "Создать")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.loadAssignments(
                        AssignmentFilter(status = statusFilter, priority = priorityFilter, courseId = courseFilter, query = it)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Поиск") }
            )
            Spacer(Modifier.height(8.dp))
            FilterPanel(
                courses = coursesState.courses,
                status = statusFilter,
                priority = priorityFilter,
                courseId = courseFilter,
                onStatusChange = {
                    statusFilter = it
                    viewModel.loadAssignments(
                        AssignmentFilter(status = it, priority = priorityFilter, courseId = courseFilter, query = query)
                    )
                },
                onPriorityChange = {
                    priorityFilter = it
                    viewModel.loadAssignments(
                        AssignmentFilter(status = statusFilter, priority = it, courseId = courseFilter, query = query)
                    )
                },
                onCourseChange = {
                    courseFilter = it
                    viewModel.loadAssignments(
                        AssignmentFilter(status = statusFilter, priority = priorityFilter, courseId = it, query = query)
                    )
                },
                onReset = {
                    query = ""
                    statusFilter = null
                    priorityFilter = null
                    courseFilter = null
                    viewModel.loadAssignments(AssignmentFilter())
                }
            )
            Spacer(Modifier.height(8.dp))
            if (state.offline) {
                AssistChip(onClick = {}, label = { Text("Данные из локального кэша") })
            }
            when {
                state.loading -> CircularProgressIndicator(Modifier.padding(24.dp))
                state.error != null -> Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                state.assignments.isEmpty() -> EmptyState()
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(state.assignments, key = { it.id }) { assignment ->
                        AssignmentCard(assignment = assignment, onClick = { onOpen(assignment.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPanel(
    courses: List<Course>,
    status: AssignmentStatus?,
    priority: AssignmentPriority?,
    courseId: Long?,
    onStatusChange: (AssignmentStatus?) -> Unit,
    onPriorityChange: (AssignmentPriority?) -> Unit,
    onCourseChange: (Long?) -> Unit,
    onReset: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterButton(
                text = status?.toRuLabel() ?: "Статус",
                modifier = Modifier.weight(1f),
                options = listOf(null) + AssignmentStatus.entries,
                optionText = { it?.toRuLabel() ?: "Статус" },
                onSelect = onStatusChange
            )
            FilterButton(
                text = priority?.toRuLabel() ?: "Приоритет",
                modifier = Modifier.weight(1f),
                options = listOf(null) + AssignmentPriority.entries,
                optionText = { it?.toRuLabel() ?: "Приоритет" },
                onSelect = onPriorityChange
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterButton(
                text = courses.firstOrNull { it.id == courseId }?.title ?: "Дисциплина",
                modifier = Modifier.fillMaxWidth(),
                options = listOf(null) + courses.map { it.id },
                optionText = { id -> courses.firstOrNull { it.id == id }?.title ?: "Дисциплина" },
                onSelect = onCourseChange
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onReset) {
                Text("Сбросить фильтры")
            }
        }
    }
}

@Composable
private fun <T> FilterButton(
    text: String,
    modifier: Modifier,
    options: List<T>,
    optionText: (T) -> String,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(
                text,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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

@Composable
private fun AssignmentCard(assignment: Assignment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(assignment.title, fontWeight = FontWeight.SemiBold)
                Text(assignment.priority.toRuLabel())
            }
            Spacer(Modifier.height(4.dp))
            Text(assignment.courseTitle, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Text("Дедлайн: ${assignment.dueAt.toRuDateTimeText()} | ${assignment.status.toRuLabel()}")
        }
    }
}

@Composable
private fun EmptyState() {
    Column(Modifier.padding(vertical = 32.dp)) {
        Text("Заданий пока нет", style = MaterialTheme.typography.titleMedium)
        Text("Создайте первое задание или обновите список после запуска сервера.")
    }
}
