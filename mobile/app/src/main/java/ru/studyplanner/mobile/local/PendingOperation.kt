package ru.studyplanner.mobile.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_operations")
data class PendingOperation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val assignmentId: Long?,
    val payload: String,
    val createdAt: String
)
