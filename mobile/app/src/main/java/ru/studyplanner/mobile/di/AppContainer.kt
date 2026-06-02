package ru.studyplanner.mobile.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.studyplanner.mobile.BuildConfig
import ru.studyplanner.mobile.local.StudyPlannerDatabase
import ru.studyplanner.mobile.remote.AssignmentApi
import ru.studyplanner.mobile.remote.AuthApi
import ru.studyplanner.mobile.remote.CourseApi
import ru.studyplanner.mobile.remote.ReminderApi
import ru.studyplanner.mobile.repository.AssignmentRepository
import ru.studyplanner.mobile.repository.AuthRepository
import ru.studyplanner.mobile.repository.CourseRepository
import ru.studyplanner.mobile.repository.ReminderRepository
import ru.studyplanner.mobile.repository.TokenStore
import ru.studyplanner.mobile.sync.AssignmentSyncManager

class AppContainer(context: Context) {

    private val tokenStore = TokenStore(context)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = tokenStore.cachedToken
            val request = if (token.isNullOrBlank()) {
                chain.request()
            } else {
                chain.request()
                    .newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            }
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val database = Room.databaseBuilder(
        context,
        StudyPlannerDatabase::class.java,
        "study-planner.db"
    ).build()

    private val authApi = retrofit.create(AuthApi::class.java)
    private val assignmentApi = retrofit.create(AssignmentApi::class.java)
    private val courseApi = retrofit.create(CourseApi::class.java)
    private val reminderApi = retrofit.create(ReminderApi::class.java)

    val authRepository = AuthRepository(authApi, tokenStore)
    val assignmentRepository = AssignmentRepository(
        assignmentApi = assignmentApi,
        assignmentDao = database.assignmentDao(),
        pendingOperationDao = database.pendingOperationDao()
    )
    val courseRepository = CourseRepository(
        courseApi = courseApi,
        courseDao = database.courseDao()
    )
    val reminderRepository = ReminderRepository(
        reminderApi = reminderApi
    )
    val syncManager = AssignmentSyncManager(
        assignmentRepository = assignmentRepository
    )
}
