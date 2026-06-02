package ru.studyplanner.mobile.model

import java.time.Instant

enum class AssignmentStatus {
    NEW,
    IN_PROGRESS,
    DONE,
    OVERDUE,
    ARCHIVED
}

enum class AssignmentPriority {
    LOW,
    MEDIUM,
    HIGH
}

data class Course(
    val id: Long,
    val title: String,
    val teacherName: String?,
    val semester: Int?,
    val color: String?
)

data class CourseForm(
    val title: String = "",
    val teacherName: String = "",
    val semester: Int? = null,
    val color: String = "#4F7CAC"
)

data class Assignment(
    val id: Long,
    val courseId: Long,
    val courseTitle: String,
    val title: String,
    val description: String?,
    val dueAt: Instant,
    val priority: AssignmentPriority,
    val status: AssignmentStatus,
    val completedAt: Instant?
)

data class AssignmentForm(
    val courseId: Long? = null,
    val title: String = "",
    val description: String = "",
    val dueAt: Instant = Instant.now().plusSeconds(86_400),
    val priority: AssignmentPriority = AssignmentPriority.MEDIUM
)

data class AssignmentFilter(
    val status: AssignmentStatus? = null,
    val priority: AssignmentPriority? = null,
    val courseId: Long? = null,
    val query: String = ""
)

data class Reminder(
    val id: Long,
    val assignmentId: Long,
    val remindAt: Instant,
    val message: String?,
    val enabled: Boolean,
    val sentAt: Instant?,
    val createdAt: Instant
)

data class ReminderForm(
    val remindAt: Instant,
    val message: String = ""
)
