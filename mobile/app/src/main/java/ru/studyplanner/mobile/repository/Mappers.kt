package ru.studyplanner.mobile.repository

import ru.studyplanner.mobile.local.LocalAssignment
import ru.studyplanner.mobile.local.LocalCourse
import ru.studyplanner.mobile.model.Assignment
import ru.studyplanner.mobile.model.AssignmentForm
import ru.studyplanner.mobile.model.AssignmentPriority
import ru.studyplanner.mobile.model.AssignmentStatus
import ru.studyplanner.mobile.model.Course
import ru.studyplanner.mobile.model.CourseForm
import ru.studyplanner.mobile.model.Reminder
import ru.studyplanner.mobile.model.ReminderForm
import ru.studyplanner.mobile.remote.AssignmentCreateDto
import ru.studyplanner.mobile.remote.AssignmentDto
import ru.studyplanner.mobile.remote.AssignmentUpdateDto
import ru.studyplanner.mobile.remote.CourseDto
import ru.studyplanner.mobile.remote.CourseCreateDto
import ru.studyplanner.mobile.remote.CourseUpdateDto
import ru.studyplanner.mobile.remote.ReminderCreateDto
import ru.studyplanner.mobile.remote.ReminderDto
import java.time.Instant

fun AssignmentDto.toLocal() = LocalAssignment(
    id = id,
    courseId = courseId,
    courseTitle = courseTitle,
    title = title,
    description = description,
    dueAt = dueAt,
    priority = priority,
    status = status,
    completedAt = completedAt
)

fun LocalAssignment.toDomain() = Assignment(
    id = id,
    courseId = courseId,
    courseTitle = courseTitle,
    title = title,
    description = description,
    dueAt = Instant.parse(dueAt),
    priority = AssignmentPriority.valueOf(priority),
    status = AssignmentStatus.valueOf(status),
    completedAt = completedAt?.let(Instant::parse)
)

fun AssignmentForm.toCreateDto() = AssignmentCreateDto(
    courseId = requireNotNull(courseId),
    title = title.trim(),
    description = description.takeIf(String::isNotBlank),
    dueAt = dueAt.toString(),
    priority = priority.name
)

fun AssignmentForm.toUpdateDto() = AssignmentUpdateDto(
    courseId = requireNotNull(courseId),
    title = title.trim(),
    description = description.takeIf(String::isNotBlank),
    dueAt = dueAt.toString(),
    priority = priority.name
)

fun CourseDto.toLocal() = LocalCourse(
    id = id,
    title = title,
    teacherName = teacherName,
    semester = semester,
    color = color
)

fun LocalCourse.toDomain() = Course(
    id = id,
    title = title,
    teacherName = teacherName,
    semester = semester,
    color = color
)

fun CourseForm.toCreateDto() = CourseCreateDto(
    title = title.trim(),
    teacherName = teacherName.trim().takeIf(String::isNotBlank),
    semester = semester,
    color = color
)

fun CourseForm.toUpdateDto() = CourseUpdateDto(
    title = title.trim(),
    teacherName = teacherName.trim().takeIf(String::isNotBlank),
    semester = semester,
    color = color
)

fun ReminderDto.toDomain() = Reminder(
    id = id,
    assignmentId = assignmentId,
    remindAt = Instant.parse(remindAt),
    message = message,
    enabled = enabled,
    sentAt = sentAt?.let(Instant::parse),
    createdAt = Instant.parse(createdAt)
)

fun ReminderForm.toCreateDto() = ReminderCreateDto(
    remindAt = remindAt.toString(),
    message = message.trim().takeIf(String::isNotBlank)
)
