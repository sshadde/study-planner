package ru.studyplanner.mobile.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AssignmentDao {
    @Query(
        """
        select * from assignments
        where (:status is null or status = :status)
          and (:priority is null or priority = :priority)
          and (:courseId is null or courseId = :courseId)
          and (:query is null or title like '%' || :query || '%' or description like '%' || :query || '%')
        order by dueAt asc
        """
    )
    suspend fun search(
        status: String?,
        priority: String?,
        courseId: Long?,
        query: String?
    ): List<LocalAssignment>

    @Query("select * from assignments where id = :id")
    suspend fun findById(id: Long): LocalAssignment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(assignments: List<LocalAssignment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(assignment: LocalAssignment)

    @Delete
    suspend fun delete(assignment: LocalAssignment)

    @Query("delete from assignments")
    suspend fun clear()
}
