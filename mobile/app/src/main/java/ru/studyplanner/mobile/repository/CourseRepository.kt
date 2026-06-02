package ru.studyplanner.mobile.repository

import ru.studyplanner.mobile.local.CourseDao
import ru.studyplanner.mobile.model.Course
import ru.studyplanner.mobile.model.CourseForm
import ru.studyplanner.mobile.remote.CourseApi

class CourseRepository(
    private val courseApi: CourseApi,
    private val courseDao: CourseDao
) {
    suspend fun getCourses(): ResultState<List<Course>> {
        return try {
            val remoteCourses = courseApi.getCourses()
            courseDao.upsertAll(remoteCourses.map { it.toLocal() })
            ResultState.Success(remoteCourses.map { it.toLocal().toDomain() })
        } catch (exception: Exception) {
            val cached = courseDao.findAll().map { it.toDomain() }
            if (cached.isEmpty()) {
                ResultState.Error(exception.toUserMessage("Не удалось загрузить дисциплины"))
            } else {
                ResultState.Success(cached, fromCache = true)
            }
        }
    }

    suspend fun createCourse(form: CourseForm): ResultState<Course> {
        return try {
            val remote = courseApi.createCourse(form.toCreateDto())
            courseDao.upsert(remote.toLocal())
            ResultState.Success(remote.toLocal().toDomain())
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось добавить дисциплину"))
        }
    }

    suspend fun updateCourse(id: Long, form: CourseForm): ResultState<Course> {
        return try {
            val remote = courseApi.updateCourse(id, form.toUpdateDto())
            courseDao.upsert(remote.toLocal())
            ResultState.Success(remote.toLocal().toDomain())
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось обновить дисциплину"))
        }
    }

    suspend fun deleteCourse(id: Long): ResultState<Unit> {
        return try {
            courseApi.deleteCourse(id)
            courseDao.deleteById(id)
            ResultState.Success(Unit)
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось удалить дисциплину"))
        }
    }
}
