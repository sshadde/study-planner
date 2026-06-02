# REST API

## Аутентификация

| Метод | Endpoint | Назначение | Доступ |
|---|---|---|---|
| `POST` | `/api/auth/register` | Регистрация пользователя | Public |
| `POST` | `/api/auth/login` | Вход и получение JWT | Public |
| `GET` | `/api/auth/me` | Данные текущего пользователя | Authenticated |

## Учебные задания

| Метод | Endpoint | Назначение | Доступ |
|---|---|---|---|
| `GET` | `/api/assignments` | Список заданий с фильтрами | Authenticated |
| `GET` | `/api/assignments/search` | Поиск заданий с фильтрами | Authenticated |
| `GET` | `/api/assignments/{id}` | Детали задания | Authenticated |
| `POST` | `/api/assignments` | Создание задания | Authenticated |
| `PUT` | `/api/assignments/{id}` | Обновление задания | Authenticated |
| `PATCH` | `/api/assignments/{id}/status` | Изменение статуса | Authenticated |
| `DELETE` | `/api/assignments/{id}` | Удаление задания | Authenticated |

## Дисциплины

| Метод | Endpoint | Назначение | Доступ |
|---|---|---|---|
| `GET` | `/api/courses` | Список дисциплин | Authenticated |
| `POST` | `/api/courses` | Создание дисциплины | Authenticated |
| `PUT` | `/api/courses/{id}` | Обновление дисциплины | Authenticated |
| `DELETE` | `/api/courses/{id}` | Удаление дисциплины | Authenticated |

## Напоминания

| Метод | Endpoint | Назначение | Доступ |
|---|---|---|---|
| `GET` | `/api/assignments/{assignmentId}/reminders` | Напоминания задания | Authenticated |
| `POST` | `/api/assignments/{assignmentId}/reminders` | Создание напоминания | Authenticated |
| `DELETE` | `/api/reminders/{id}` | Удаление напоминания | Authenticated |

## Администрирование

| Метод | Endpoint | Назначение | Доступ |
|---|---|---|---|
| `GET` | `/api/admin/users` | Просмотр пользователей системы | ADMIN |

## Документация API

Swagger UI доступен после запуска сервера:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Безопасность

Защищенные endpoint'ы требуют заголовок:

```http
Authorization: Bearer <accessToken>
```

Токен формируется `JwtProvider` и содержит id пользователя, email, роль, время выпуска и время истечения. Endpoint'ы `/api/admin/**` доступны только пользователям с ролью `ADMIN`; остальные бизнес-endpoint'ы требуют валидный JWT.
