package ru.studyplanner.mobile.ui

import ru.studyplanner.mobile.model.AssignmentPriority
import ru.studyplanner.mobile.model.AssignmentStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val displayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

fun AssignmentPriority.toRuLabel(): String = when (this) {
    AssignmentPriority.LOW -> "Низкий"
    AssignmentPriority.MEDIUM -> "Обычный"
    AssignmentPriority.HIGH -> "Высокий"
}

fun AssignmentStatus.toRuLabel(): String = when (this) {
    AssignmentStatus.NEW -> "Новое"
    AssignmentStatus.IN_PROGRESS -> "В работе"
    AssignmentStatus.DONE -> "Выполнено"
    AssignmentStatus.OVERDUE -> "Просрочено"
    AssignmentStatus.ARCHIVED -> "В архиве"
}

fun Instant.toRuDateTimeText(): String {
    return displayFormatter.withZone(ZoneId.systemDefault()).format(this)
}

fun parseRuDateTime(value: String): Instant? {
    return try {
        LocalDateTime.parse(value.trim(), displayFormatter)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    } catch (exception: DateTimeParseException) {
        null
    }
}
