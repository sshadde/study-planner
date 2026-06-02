package ru.studyplanner.mobile.sync

import ru.studyplanner.mobile.repository.AssignmentRepository

class AssignmentSyncManager(
    private val assignmentRepository: AssignmentRepository
) {
    suspend fun synchronize() {
        assignmentRepository.syncPendingOperations()
    }
}
