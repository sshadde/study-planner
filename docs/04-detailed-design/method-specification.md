# Спецификация методов

## Назначение

Документ фиксирует ключевые методы, которые реализуют основные сценарии системы. В спецификацию включены не все getter/setter и технические методы, а только методы, важные для требований, бизнес-логики, PCMEF и оффлайн-режима.

## Backend: Control

| Компонент | Ключевые методы | Назначение |
|---|---|---|
| `AuthController` | `register`, `login`, `me` | Принимает запросы регистрации, входа и получения текущего пользователя |
| `AssignmentController` | `findAll`, `search`, `findById`, `create`, `update`, `changeStatus`, `delete` | Предоставляет REST API для работы с учебными заданиями |
| `CourseController` | `findAll`, `create`, `update`, `delete` | Предоставляет REST API для управления дисциплинами |
| `ReminderController` | `findAll`, `create`, `delete` | Предоставляет REST API для напоминаний |
| `AdminController` | `getUsers` | Возвращает список пользователей для администратора |
| `RestExceptionHandler` | `notFound`, `conflict`, `businessRule`, `badCredentials`, `validation` | Приводит ошибки API к единому формату ответа |

## Backend: Mediator

| Компонент | Ключевые методы | Назначение |
|---|---|---|
| `AuthService` | `register`, `login`, `getCurrentUser` | Проверяет учетные данные, хеширует пароль BCrypt, формирует JWT, возвращает профиль пользователя |
| `AssignmentService` | `getAssignments`, `getAssignmentById`, `createAssignment`, `updateAssignment`, `changeStatus`, `deleteAssignment` | Реализует основные сценарии заданий, проверяет владельца, дисциплину, фильтры и статус |
| `CourseService` | `getCourses`, `createCourse`, `updateCourse`, `deleteCourse` | Управляет дисциплинами пользователя, запрещает дубли и удаление дисциплины со связанными заданиями |
| `ReminderService` | `createReminder`, `getAssignmentReminders`, `deleteReminder` | Управляет напоминаниями и проверяет, что напоминание не позже дедлайна |
| `AdminService` | `getUsers` | Возвращает список пользователей для административного сценария |

## Backend: Entity

| Компонент | Ключевые методы | Назначение |
|---|---|---|
| `Assignment` | `update`, `changeStatus`, `markDone`, `isOverdue`, `rename`, `reschedule` | Хранит доменные правила задания: статус, дедлайн, название, время выполнения |
| `Course` | `update`, `rename` | Обновляет данные дисциплины и не допускает пустое название |
| `User` | `attachProfile`, `disable` | Связывает учетную запись с профилем и поддерживает блокировку пользователя |
| `Reminder` | `disable`, `markSent` | Управляет активностью и фактом отправки напоминания |

## Backend: Foundation

| Компонент | Ключевые методы | Назначение |
|---|---|---|
| `UserRepository` | `findByEmail`, `existsByEmail`, `findAllByOrderByCreatedAtDesc` | Поиск пользователей, проверка уникальности email, список для администратора |
| `AssignmentRepository` | `findByIdAndUserId`, `findAllByUserIdOrderByDueAtAsc`, `existsByCourseId`, `findAll(Specification)` | Доступ к заданиям с учетом владельца, сортировки и фильтрации |
| `CourseRepository` | `findByIdAndUserId`, `findAllByUserIdOrderByTitleAsc`, `existsByUserIdAndTitleIgnoreCase`, `existsByIdAndUserId` | Доступ к дисциплинам пользователя |
| `ReminderRepository` | `findByIdAndAssignmentUserId`, `findAllByAssignmentIdAndAssignmentUserIdOrderByRemindAtAsc` | Доступ к напоминаниям через проверку владельца задания |
| `JwtProvider` | `generateToken`, `parse` | Создает и проверяет JWT |
| `IdentityMap` | `find`, `getOrPut`, `values` | Поддерживает уникальность объектов по ключу внутри операции |

## Mobile: ViewModel

| Компонент | Ключевые методы | Назначение |
|---|---|---|
| `AuthViewModel` | `restoreSession`, `login`, `register`, `logout` | Управляет состоянием авторизации и восстановлением сохраненной сессии |
| `AssignmentViewModel` | `loadAssignments`, `synchronizeAndLoad`, `loadAssignment`, `createAssignment`, `updateAssignment`, `changeStatus`, `deleteAssignment` | Управляет списком, деталями, формами и синхронизацией заданий |
| `CourseViewModel` | `loadCourses`, `createCourse`, `updateCourse`, `deleteCourse` | Управляет экраном дисциплин |
| `ReminderViewModel` | `loadReminders`, `createReminder`, `deleteReminder` | Управляет напоминаниями на экране деталей задания |

## Mobile: Repository

| Компонент | Ключевые методы | Назначение |
|---|---|---|
| `AuthRepository` | `restoreSession`, `login`, `register`, `logout` | Работает с API авторизации и сохраненной сессией |
| `AssignmentRepository` | `getAssignments`, `getAssignment`, `createAssignment`, `updateAssignment`, `changeStatus`, `deleteAssignment`, `syncPendingOperations` | Загружает задания с сервера, читает Room-кэш при ошибке сети, сохраняет pending-операции заданий |
| `CourseRepository` | `getCourses`, `createCourse`, `updateCourse`, `deleteCourse` | Загружает дисциплины с сервера и читает Room-кэш при ошибке сети |
| `ReminderRepository` | `getReminders`, `createReminder`, `deleteReminder` | Работает с напоминаниями через REST API |
| `TokenStore` | `saveSession`, `saveUser`, `clear`, `hasToken` | Хранит JWT и данные пользователя для восстановления сессии |

## Вывод

Ключевые методы распределены по слоям PCMEF. Control принимает запросы, Mediator реализует бизнес-сценарии, Entity содержит доменные правила, Foundation отвечает за хранение и инфраструктуру. Мобильный клиент использует ViewModel и Repository для работы с REST API, Room-кэшем и сохраненной сессией.
