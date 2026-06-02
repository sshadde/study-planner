# Техническое задание

## 1. Общие сведения

**Название:** мобильное приложение для информационной системы планирования учебных заданий студентов.

**Траектория:** мобильная разработка.

**Назначение:** предоставить студенту инструмент для централизованного учета учебных заданий, дедлайнов, дисциплин, статусов и напоминаний.

## 2. Требования к функциональным характеристикам

| ID | Требование | Реализация |
|---|---|---|
| FR-01 | Регистрация пользователя | `POST /api/auth/register`, экран Login |
| FR-02 | Аутентификация через JWT | `POST /api/auth/login`, `JwtProvider` |
| FR-03 | Просмотр списка заданий | `GET /api/assignments`, `AssignmentListScreen` |
| FR-04 | Создание задания | `POST /api/assignments`, `AssignmentEditScreen` |
| FR-05 | Редактирование задания | `PUT /api/assignments/{id}` |
| FR-06 | Удаление задания | `DELETE /api/assignments/{id}` |
| FR-07 | Изменение статуса | `PATCH /api/assignments/{id}/status` |
| FR-08 | Поиск и фильтрация | `GET /api/assignments` с query-параметрами |
| FR-09 | Управление дисциплинами | `GET/POST/PUT/DELETE /api/courses`, экран `Дисциплины` |
| FR-10 | Напоминания | `/api/assignments/{id}/reminders`, экран деталей задания |
| FR-11 | OpenAPI | Swagger UI |
| FR-12 | Оффлайн-кэш | Room |

## 3. Требования к архитектуре

Архитектура соответствует PCMEF:

| Слой | Реализация |
|---|---|
| Presentation | Android Jetpack Compose |
| Control | Spring REST Controllers |
| Mediator | Spring Services |
| Entity | JPA Entity и доменные методы |
| Foundation | Repositories, Security, JWT, PostgreSQL |

## 4. Требования к надежности

- Сервер возвращает корректные HTTP-статусы.
- Ошибки API приводятся к единому `ErrorResponse`.
- Мобильное приложение показывает loading/error/empty/offline states.
- Данные пользователя фильтруются по текущему JWT-пользователю.

## 5. Требования к безопасности

- BCrypt-хеширование паролей.
- JWT Bearer token для защищенных запросов.
- Пользовательские роли `STUDENT` и `ADMIN`.
- Endpoint'ы `/api/admin/**` доступны только роли `ADMIN`.
- Stateless Spring Security.

## 6. Требования к развертыванию

- Backend запускается как JAR или Docker-контейнер.
- PostgreSQL запускается локально или через Docker Compose.
- Android APK собирается командой `.\gradlew.bat :app:assembleDebug`.
