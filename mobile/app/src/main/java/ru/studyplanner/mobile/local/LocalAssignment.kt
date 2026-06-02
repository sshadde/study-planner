package ru.studyplanner.mobile.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class LocalAssignment(
    @PrimaryKey val id: Long,
    val courseId: Long,
    val courseTitle: String,
    val title: String,
    val description: String?,
    val dueAt: String,
    val priority: String,
    val status: String,
    val completedAt: String?,
    val pendingSync: Boolean = false
)
