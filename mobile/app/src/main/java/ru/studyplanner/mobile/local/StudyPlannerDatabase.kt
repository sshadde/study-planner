package ru.studyplanner.mobile.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocalAssignment::class,
        LocalCourse::class,
        PendingOperation::class
    ],
    version = 1,
    exportSchema = false
)
abstract class StudyPlannerDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao

    abstract fun courseDao(): CourseDao

    abstract fun pendingOperationDao(): PendingOperationDao
}
