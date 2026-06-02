package ru.studyplanner.mobile.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingOperationDao {
    @Query("select * from pending_operations order by createdAt asc")
    suspend fun findAll(): List<PendingOperation>

    @Insert
    suspend fun insert(operation: PendingOperation)

    @Delete
    suspend fun delete(operation: PendingOperation)
}
