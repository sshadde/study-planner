package ru.studyplanner.mobile.repository

import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserMessage(defaultMessage: String): String {
    return when (this) {
        is HttpException -> httpErrorMessage(defaultMessage)
        is ConnectException -> "Не удалось подключиться к серверу. Проверьте, что он запущен."
        is UnknownHostException -> "Сервер недоступен. Проверьте подключение и адрес backend."
        is SocketTimeoutException -> "Сервер слишком долго не отвечает. Попробуйте повторить запрос."
        else -> message ?: defaultMessage
    }
}

private fun HttpException.httpErrorMessage(defaultMessage: String): String {
    val responseText = response()?.errorBody()?.string().orEmpty()
    val parsed = parseBackendError(responseText)
    return when (code()) {
        400 -> parsed ?: "$defaultMessage: сервер отклонил данные формы."
        401 -> parsed ?: "Сессия недействительна. Войдите в приложение заново."
        403 -> parsed ?: "Недостаточно прав для выполнения действия."
        404 -> parsed ?: "Запрошенная запись не найдена."
        409 -> parsed ?: "$defaultMessage: такая запись уже существует."
        in 500..599 -> "Ошибка сервера. Попробуйте повторить действие позже."
        else -> parsed ?: "$defaultMessage: HTTP ${code()}."
    }
}

private fun parseBackendError(responseText: String): String? {
    if (responseText.isBlank()) {
        return null
    }
    return runCatching {
        val json = JSONObject(responseText)
        val error = json.optString("error")
        val details = json.optJSONArray("details")
        if (details != null && details.length() > 0) {
            (0 until details.length())
                .map { index -> translateDetail(details.getString(index)) }
                .joinToString(separator = "\n")
        } else {
            translateSummary(error).takeIf(String::isNotBlank)
        }
    }.getOrNull()
}

private fun translateSummary(value: String): String = when {
    value.equals("Validation failed", ignoreCase = true) -> "Проверьте заполнение формы."
    value.contains("already exists", ignoreCase = true) -> "Такая запись уже существует."
    value.contains("not found", ignoreCase = true) -> "Запись не найдена."
    value.contains("bad credentials", ignoreCase = true) -> "Неверный email или пароль."
    value.contains("Reminder cannot be later", ignoreCase = true) -> "Напоминание не может быть позже дедлайна."
    value.contains("Course has assignments", ignoreCase = true) -> "Нельзя удалить дисциплину, пока к ней привязаны задания."
    else -> value
}

private fun translateDetail(value: String): String {
    val field = value.substringBefore(":").trim()
    val rule = value.substringAfter(":", "").trim()
    val fieldName = when (field) {
        "title" -> "Название"
        "teacherName" -> "Преподаватель"
        "semester" -> "Семестр"
        "color" -> "Цвет"
        "email" -> "Email"
        "password" -> "Пароль"
        "fullName" -> "ФИО"
        "groupName" -> "Группа"
        "courseId" -> "Дисциплина"
        "dueAt" -> "Дедлайн"
        "description" -> "Описание"
        else -> field.ifBlank { "Поле" }
    }
    val ruleText = when {
        rule.contains("must not be blank", ignoreCase = true) -> "не должно быть пустым"
        rule.contains("must not be null", ignoreCase = true) -> "должно быть заполнено"
        rule.contains("well-formed email", ignoreCase = true) -> "должен быть корректным email"
        rule.contains("greater than or equal to 1", ignoreCase = true) -> "должен быть от 1 до 10"
        rule.contains("less than or equal to 10", ignoreCase = true) -> "должен быть от 1 до 10"
        rule.contains("future", ignoreCase = true) -> "не может быть в прошлом"
        rule.contains("size must be between", ignoreCase = true) -> "имеет недопустимую длину"
        else -> rule.ifBlank { "заполнено неверно" }
    }
    return "$fieldName: $ruleText"
}
