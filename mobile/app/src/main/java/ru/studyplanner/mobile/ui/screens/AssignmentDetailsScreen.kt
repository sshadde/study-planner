package ru.studyplanner.mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import ru.studyplanner.mobile.model.Reminder
import ru.studyplanner.mobile.model.ReminderForm
import ru.studyplanner.mobile.model.AssignmentStatus
import ru.studyplanner.mobile.state.AssignmentViewModel
import ru.studyplanner.mobile.state.ReminderViewModel
import ru.studyplanner.mobile.state.ReminderUiState
import ru.studyplanner.mobile.ui.DateTimePickerField
import ru.studyplanner.mobile.ui.toRuDateTimeText
import ru.studyplanner.mobile.ui.toRuLabel
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDetailsScreen(
    id: Long,
    viewModel: AssignmentViewModel,
    reminderViewModel: ReminderViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val state by viewModel.detailsState.collectAsState()
    val reminderState by reminderViewModel.state.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadAssignment(id)
        reminderViewModel.loadReminders(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Задание") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = { viewModel.deleteAssignment(id, onBack) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            when {
                state.loading -> CircularProgressIndicator()
                state.error != null -> Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                state.assignment != null -> {
                    val assignment = requireNotNull(state.assignment)
                    Text(assignment.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Дисциплина: ${assignment.courseTitle}")
                    Text("Дедлайн: ${assignment.dueAt.toRuDateTimeText()}")
                    Text("Приоритет: ${assignment.priority.toRuLabel()}")
                    Text("Статус: ${assignment.status.toRuLabel()}")
                    Spacer(Modifier.height(16.dp))
                    Text(assignment.description ?: "Описание не указано")
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.changeStatus(id, AssignmentStatus.DONE) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                            Text("Готово")
                        }
                        Button(
                            onClick = { viewModel.changeStatus(id, AssignmentStatus.IN_PROGRESS) },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Text("В работу")
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    ReminderSection(
                        assignmentDueAt = assignment.dueAt,
                        state = reminderState,
                        onCreate = { form -> reminderViewModel.createReminder(id, form) },
                        onDelete = reminderViewModel::deleteReminder
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderSection(
    assignmentDueAt: Instant,
    state: ReminderUiState,
    onCreate: (ReminderForm) -> Unit,
    onDelete: (Long) -> Unit
) {
    var remindAt by remember(assignmentDueAt) { mutableStateOf(assignmentDueAt) }
    var message by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Text("Напоминания", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(8.dp))
    DateTimePickerField(
        label = "Когда напомнить",
        value = remindAt,
        onValueChange = { remindAt = it }
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = message,
        onValueChange = { message = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("Текст напоминания") }
    )
    localError?.let {
        Spacer(Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.error)
    }
    state.error?.let {
        Spacer(Modifier.height(8.dp))
        Text(it, color = MaterialTheme.colorScheme.error)
    }
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = {
            localError = when {
                remindAt.isBefore(Instant.now()) -> "Напоминание не может быть в прошлом"
                remindAt.isAfter(assignmentDueAt) -> "Напоминание не может быть позже дедлайна"
                message.length > 500 -> "Текст напоминания не должен быть длиннее 500 символов"
                else -> null
            }
            if (localError == null) {
                onCreate(ReminderForm(remindAt = remindAt, message = message))
                message = ""
            }
        },
        enabled = !state.saving,
        modifier = Modifier.heightIn(min = 48.dp)
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Text("Добавить")
    }
    Spacer(Modifier.height(12.dp))
    when {
        state.loading -> CircularProgressIndicator()
        state.reminders.isEmpty() -> Text("Напоминаний пока нет")
        else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            state.reminders.forEach { reminder ->
                ReminderRow(reminder = reminder, onDelete = onDelete)
            }
        }
    }
}

@Composable
private fun ReminderRow(reminder: Reminder, onDelete: (Long) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(reminder.remindAt.toRuDateTimeText(), fontWeight = FontWeight.SemiBold)
            Text(reminder.message ?: "Без текста")
        }
        IconButton(onClick = { onDelete(reminder.id) }) {
            Icon(Icons.Filled.Delete, contentDescription = "Удалить напоминание")
        }
    }
}
