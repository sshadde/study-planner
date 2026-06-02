package ru.studyplanner.mobile.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReminderApi {
    @GET("api/assignments/{assignmentId}/reminders")
    suspend fun getReminders(@Path("assignmentId") assignmentId: Long): List<ReminderDto>

    @POST("api/assignments/{assignmentId}/reminders")
    suspend fun createReminder(
        @Path("assignmentId") assignmentId: Long,
        @Body request: ReminderCreateDto
    ): ReminderDto

    @DELETE("api/reminders/{id}")
    suspend fun deleteReminder(@Path("id") id: Long)
}

data class ReminderDto(
    val id: Long,
    val assignmentId: Long,
    val remindAt: String,
    val message: String?,
    val enabled: Boolean,
    val sentAt: String?,
    val createdAt: String
)

data class ReminderCreateDto(
    val remindAt: String,
    val message: String?
)
