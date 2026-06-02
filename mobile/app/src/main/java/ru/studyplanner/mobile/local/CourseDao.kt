package ru.studyplanner.mobile.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CourseDao {
    @Query("select * from courses order by title asc")
    suspend fun findAll(): List<LocalCourse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(courses: List<LocalCourse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(course: LocalCourse)

    @Query("delete from courses where id = :id")
    suspend fun deleteById(id: Long)
}
