package ru.studyplanner.mobile.repository

import org.json.JSONObject
import ru.studyplanner.mobile.local.AssignmentDao
import ru.studyplanner.mobile.local.PendingOperation
import ru.studyplanner.mobile.local.PendingOperationDao
import ru.studyplanner.mobile.model.Assignment
import ru.studyplanner.mobile.model.AssignmentFilter
import ru.studyplanner.mobile.model.AssignmentForm
import ru.studyplanner.mobile.model.AssignmentPriority
import ru.studyplanner.mobile.model.AssignmentStatus
import ru.studyplanner.mobile.remote.AssignmentApi
import ru.studyplanner.mobile.remote.StatusChangeDto
import java.time.Instant

class AssignmentRepository(
    private val assignmentApi: AssignmentApi,
    private val assignmentDao: AssignmentDao,
    private val pendingOperationDao: PendingOperationDao
) {
    suspend fun getAssignments(filter: AssignmentFilter): ResultState<List<Assignment>> {
        return try {
            val remoteAssignments = assignmentApi.getAssignments(
                status = filter.status?.name,
                priority = filter.priority?.name,
                courseId = filter.courseId,
                query = filter.query.takeIf(String::isNotBlank)
            )
            assignmentDao.upsertAll(remoteAssignments.map { it.toLocal() })
            ResultState.Success(remoteAssignments.map { it.toLocal().toDomain() })
        } catch (exception: Exception) {
            val cached = assignmentDao.search(
                status = filter.status?.name,
                priority = filter.priority?.name,
                courseId = filter.courseId,
                query = filter.query.takeIf(String::isNotBlank)
            ).map { it.toDomain() }
            if (cached.isEmpty()) {
                ResultState.Error(exception.toUserMessage("Не удалось загрузить задания"))
            } else {
                ResultState.Success(cached, fromCache = true)
            }
        }
    }

    suspend fun getAssignment(id: Long): ResultState<Assignment> {
        return try {
            val remote = assignmentApi.getAssignment(id)
            assignmentDao.upsert(remote.toLocal())
            ResultState.Success(remote.toLocal().toDomain())
        } catch (exception: Exception) {
            val cached = assignmentDao.findById(id)?.toDomain()
            if (cached == null) {
                ResultState.Error(exception.toUserMessage("Не удалось загрузить задание"))
            } else {
                ResultState.Success(cached, fromCache = true)
            }
        }
    }

    suspend fun createAssignment(form: AssignmentForm): ResultState<Assignment> {
        return try {
            val remote = assignmentApi.createAssignment(form.toCreateDto())
            assignmentDao.upsert(remote.toLocal())
            ResultState.Success(remote.toLocal().toDomain())
        } catch (exception: Exception) {
            pendingOperationDao.insert(
                PendingOperation(
                    type = "CREATE_ASSIGNMENT",
                    assignmentId = null,
                    payload = form.toPendingJson(),
                    createdAt = Instant.now().toString()
                )
            )
            ResultState.Error(exception.toUserMessage("Сохранено локально. Синхронизация повторится при доступной сети."))
        }
    }

    suspend fun updateAssignment(id: Long, form: AssignmentForm): ResultState<Assignment> {
        return try {
            val remote = assignmentApi.updateAssignment(id, form.toUpdateDto())
            assignmentDao.upsert(remote.toLocal())
            ResultState.Success(remote.toLocal().toDomain())
        } catch (exception: Exception) {
            pendingOperationDao.insert(
                PendingOperation(
                    type = "UPDATE_ASSIGNMENT",
                    assignmentId = id,
                    payload = form.toPendingJson(),
                    createdAt = Instant.now().toString()
                )
            )
            ResultState.Error(exception.toUserMessage("Изменения поставлены в очередь синхронизации."))
        }
    }

    suspend fun changeStatus(id: Long, status: AssignmentStatus): ResultState<Assignment> {
        return try {
            val remote = assignmentApi.changeStatus(id, StatusChangeDto(status.name))
            assignmentDao.upsert(remote.toLocal())
            ResultState.Success(remote.toLocal().toDomain())
        } catch (exception: Exception) {
            pendingOperationDao.insert(
                PendingOperation(
                    type = "CHANGE_STATUS",
                    assignmentId = id,
                    payload = status.name,
                    createdAt = Instant.now().toString()
                )
            )
            ResultState.Error(exception.toUserMessage("Изменение статуса поставлено в очередь синхронизации."))
        }
    }

    suspend fun deleteAssignment(id: Long): ResultState<Unit> {
        return try {
            assignmentApi.deleteAssignment(id)
            assignmentDao.findById(id)?.let { assignmentDao.delete(it) }
            ResultState.Success(Unit)
        } catch (exception: Exception) {
            pendingOperationDao.insert(
                PendingOperation(
                    type = "DELETE_ASSIGNMENT",
                    assignmentId = id,
                    payload = "",
                    createdAt = Instant.now().toString()
                )
            )
            ResultState.Error(exception.toUserMessage("Удаление поставлено в очередь синхронизации."))
        }
    }

    suspend fun syncPendingOperations() {
        pendingOperationDao.findAll().forEach { operation ->
            runCatching {
                when (operation.type) {
                    "CREATE_ASSIGNMENT" -> {
                        val remote = assignmentApi.createAssignment(operation.payload.toAssignmentForm().toCreateDto())
                        assignmentDao.upsert(remote.toLocal())
                    }
                    "UPDATE_ASSIGNMENT" -> {
                        val id = requireNotNull(operation.assignmentId)
                        val remote = assignmentApi.updateAssignment(id, operation.payload.toAssignmentForm().toUpdateDto())
                        assignmentDao.upsert(remote.toLocal())
                    }
                    "CHANGE_STATUS" -> {
                        val id = requireNotNull(operation.assignmentId)
                        val remote = assignmentApi.changeStatus(id, StatusChangeDto(operation.payload))
                        assignmentDao.upsert(remote.toLocal())
                    }
                    "DELETE_ASSIGNMENT" -> {
                        val id = requireNotNull(operation.assignmentId)
                        assignmentApi.deleteAssignment(id)
                        assignmentDao.findById(id)?.let { assignmentDao.delete(it) }
                    }
                }
                pendingOperationDao.delete(operation)
            }
        }
    }

    private fun AssignmentForm.toPendingJson(): String {
        return JSONObject()
            .put("courseId", requireNotNull(courseId))
            .put("title", title)
            .put("description", description)
            .put("dueAt", dueAt.toString())
            .put("priority", priority.name)
            .toString()
    }

    private fun String.toAssignmentForm(): AssignmentForm {
        val json = JSONObject(this)
        return AssignmentForm(
            courseId = json.getLong("courseId"),
            title = json.getString("title"),
            description = json.optString("description"),
            dueAt = Instant.parse(json.getString("dueAt")),
            priority = AssignmentPriority.valueOf(json.getString("priority"))
        )
    }
}
