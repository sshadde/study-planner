package ru.studyplanner.mobile.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AssignmentApi {
    @GET("api/assignments")
    suspend fun getAssignments(
        @Query("status") status: String?,
        @Query("priority") priority: String?,
        @Query("courseId") courseId: Long?,
        @Query("query") query: String?
    ): List<AssignmentDto>

    @GET("api/assignments/{id}")
    suspend fun getAssignment(@Path("id") id: Long): AssignmentDto

    @POST("api/assignments")
    suspend fun createAssignment(@Body request: AssignmentCreateDto): AssignmentDto

    @PUT("api/assignments/{id}")
    suspend fun updateAssignment(
        @Path("id") id: Long,
        @Body request: AssignmentUpdateDto
    ): AssignmentDto

    @PATCH("api/assignments/{id}/status")
    suspend fun changeStatus(
        @Path("id") id: Long,
        @Body request: StatusChangeDto
    ): AssignmentDto

    @DELETE("api/assignments/{id}")
    suspend fun deleteAssignment(@Path("id") id: Long)
}

data class AssignmentDto(
    val id: Long,
    val courseId: Long,
    val courseTitle: String,
    val title: String,
    val description: String?,
    val dueAt: String,
    val priority: String,
    val status: String,
    val completedAt: String?
)

data class AssignmentCreateDto(
    val courseId: Long,
    val title: String,
    val description: String?,
    val dueAt: String,
    val priority: String,
    val status: String = "NEW"
)

data class AssignmentUpdateDto(
    val courseId: Long,
    val title: String,
    val description: String?,
    val dueAt: String,
    val priority: String
)

data class StatusChangeDto(
    val status: String
)
