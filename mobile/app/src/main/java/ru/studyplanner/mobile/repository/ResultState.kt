package ru.studyplanner.mobile.repository

sealed interface ResultState<out T> {
    data class Success<T>(val data: T, val fromCache: Boolean = false) : ResultState<T>
    data class Error(val message: String) : ResultState<Nothing>
}
