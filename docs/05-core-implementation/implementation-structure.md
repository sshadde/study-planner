# Структура реализации

## Backend-модуль

Серверная часть расположена в папке `server/` и реализована как Maven-проект Spring Boot.

```text
server/
  pom.xml
  src/main/java/ru/studyplanner/
    StudyPlannerServerApplication.java
    control/
    mediator/
      dto/
    entity/
    foundation/
  src/main/resources/
    application.yml
  src/test/java/ru/studyplanner/
```

## Соответствие PCMEF

| Слой PCMEF | Пакет | Ответственность |
|---|---|---|
| Presentation | Android-клиент Jetpack Compose | UI, ввод и отображение данных |
| Control | `ru.studyplanner.control` | REST-контроллеры, DTO validation, HTTP-статусы |
| Mediator | `ru.studyplanner.mediator` | Use Case-сервисы, транзакции, проверки прав и бизнес-правил |
| Entity | `ru.studyplanner.entity` | JPA Entity, enum'ы, доменные методы |
| Foundation | `ru.studyplanner.foundation` | Spring Data JPA, Specification, JWT, Spring Security, admin bootstrap |

## Entity-слой

| Класс | Назначение |
|---|---|
| `User` | Учетная запись, роль, состояние активности |
| `StudentProfile` | Профиль студента |
| `Course` | Учебная дисциплина |
| `Assignment` | Учебное задание с доменными методами статуса и дедлайна |
| `Reminder` | Напоминание о задании |

## Foundation-слой

| Компонент | Назначение |
|---|---|
| `UserRepository` | Поиск пользователя по id и email |
| `CourseRepository` | Управление дисциплинами пользователя |
| `AssignmentRepository` | CRUD и поиск заданий |
| `ReminderRepository` | Управление напоминаниями |
| `AssignmentSpecification` | Фильтрация заданий по нескольким критериям |
| `JwtProvider` | Генерация и проверка JWT HS256 |
| `SecurityConfig` | Stateless security и защита endpoint'ов |
| `JwtAuthenticationFilter` | Извлечение пользователя из Bearer token |

## Mediator-слой

| Сервис | Основные сценарии |
|---|---|
| `AuthServiceImpl` | Регистрация, вход, текущий пользователь |
| `CourseServiceImpl` | Список, создание, изменение, удаление дисциплин |
| `AssignmentServiceImpl` | Список, поиск, создание, изменение, удаление, смена статуса заданий |
| `ReminderServiceImpl` | Создание, просмотр и удаление напоминаний |
| `AdminServiceImpl` | Просмотр пользователей для административного сценария |

## Доменные правила

- Email пользователя уникален.
- Пароль хранится только как BCrypt-хеш.
- Пользователь получает только собственные дисциплины, задания и напоминания.
- Дисциплину нельзя удалить, если к ней привязаны задания.
- Напоминание не может быть позже дедлайна задания.
- При переводе задания в `DONE` заполняется `completedAt`.
- При выходе задания из `DONE` поле `completedAt` очищается.
