package ru.studyplanner.mobile.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.POST

interface CourseApi {
    @GET("api/courses")
    suspend fun getCourses(): List<CourseDto>

    @POST("api/courses")
    suspend fun createCourse(@Body request: CourseCreateDto): CourseDto

    @PUT("api/courses/{id}")
    suspend fun updateCourse(
        @Path("id") id: Long,
        @Body request: CourseUpdateDto
    ): CourseDto

    @DELETE("api/courses/{id}")
    suspend fun deleteCourse(@Path("id") id: Long)
}

data class CourseDto(
    val id: Long,
    val title: String,
    val teacherName: String?,
    val semester: Int?,
    val color: String?
)

data class CourseCreateDto(
    val title: String,
    val teacherName: String?,
    val semester: Int?,
    val color: String?
)

data class CourseUpdateDto(
    val title: String,
    val teacherName: String?,
    val semester: Int?,
    val color: String?
)
