package ru.studyplanner.mobile.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class LocalCourse(
    @PrimaryKey val id: Long,
    val title: String,
    val teacherName: String?,
    val semester: Int?,
    val color: String?
)
