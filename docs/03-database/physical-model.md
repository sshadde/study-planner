# Физическая модель данных

## Общие соглашения

| Соглашение | Описание |
|---|---|
| СУБД | PostgreSQL |
| Идентификаторы | `bigint generated always as identity` |
| Дата и время | `timestamp with time zone` для пользовательских событий и аудита |
| Имена таблиц | `snake_case`, множественное число |
| Имена ограничений | Префиксы `pk_`, `fk_`, `uk_`, `chk_` |
| Удаление владельца | Каскадное удаление зависимых данных пользователя |
| Удаление дисциплины | Запрещено при наличии связанных заданий |

## Таблица `users`

| Поле | Тип | Ограничения | Назначение |
|---|---|---|---|
| `id` | `bigint` | PK | Идентификатор учетной записи |
| `email` | `varchar(255)` | NOT NULL, UNIQUE | Логин пользователя |
| `password_hash` | `varchar(255)` | NOT NULL | BCrypt-хеш пароля |
| `role` | `varchar(20)` | NOT NULL, CHECK | Роль `STUDENT` или `ADMIN` |
| `enabled` | `boolean` | NOT NULL | Признак активной учетной записи |
| `created_at` | `timestamptz` | NOT NULL | Дата создания |
| `updated_at` | `timestamptz` | NOT NULL | Дата последнего изменения |

## Таблица `student_profiles`

| Поле | Тип | Ограничения | Назначение |
|---|---|---|---|
| `id` | `bigint` | PK | Идентификатор профиля |
| `user_id` | `bigint` | NOT NULL, FK, UNIQUE | Связь с учетной записью |
| `full_name` | `varchar(160)` | NOT NULL | ФИО студента |
| `group_name` | `varchar(80)` | NOT NULL | Учебная группа |

## Таблица `courses`

| Поле | Тип | Ограничения | Назначение |
|---|---|---|---|
| `id` | `bigint` | PK | Идентификатор дисциплины |
| `user_id` | `bigint` | NOT NULL, FK | Владелец дисциплины |
| `title` | `varchar(160)` | NOT NULL | Название дисциплины |
| `teacher_name` | `varchar(160)` | NULL | Преподаватель |
| `semester` | `smallint` | CHECK | Номер семестра |
| `color` | `varchar(16)` | NULL | Цветовая метка в UI |
| `created_at` | `timestamptz` | NOT NULL | Дата создания |
| `updated_at` | `timestamptz` | NOT NULL | Дата изменения |

Уникальность пары `user_id`, `title` не дает одному студенту создать две дисциплины с одинаковым названием.

## Таблица `assignments`

| Поле | Тип | Ограничения | Назначение |
|---|---|---|---|
| `id` | `bigint` | PK | Идентификатор задания |
| `user_id` | `bigint` | NOT NULL, FK | Владелец задания |
| `course_id` | `bigint` | NOT NULL, FK | Дисциплина |
| `title` | `varchar(200)` | NOT NULL | Название задания |
| `description` | `text` | NULL | Подробное описание |
| `due_at` | `timestamptz` | NOT NULL | Дедлайн |
| `priority` | `varchar(20)` | NOT NULL, CHECK | Приоритет `LOW`, `MEDIUM`, `HIGH` |
| `status` | `varchar(20)` | NOT NULL, CHECK | Статус `NEW`, `IN_PROGRESS`, `DONE`, `OVERDUE`, `ARCHIVED` |
| `created_at` | `timestamptz` | NOT NULL | Дата создания |
| `updated_at` | `timestamptz` | NOT NULL | Дата изменения |
| `completed_at` | `timestamptz` | NULL | Дата выполнения |

Для сценариев списка, фильтрации и поиска добавлены индексы по владельцу, дисциплине, сроку, статусу и приоритету.

## Таблица `reminders`

| Поле | Тип | Ограничения | Назначение |
|---|---|---|---|
| `id` | `bigint` | PK | Идентификатор напоминания |
| `assignment_id` | `bigint` | NOT NULL, FK | Задание |
| `remind_at` | `timestamptz` | NOT NULL | Время напоминания |
| `message` | `varchar(500)` | NULL | Текст сообщения |
| `enabled` | `boolean` | NOT NULL | Активность напоминания |
| `sent_at` | `timestamptz` | NULL | Фактическое время отправки |
| `created_at` | `timestamptz` | NOT NULL | Дата создания |

Бизнес-правило `remind_at <= assignments.due_at` проверяется в Mediator-слое, потому что PostgreSQL `CHECK` не может ссылаться на другую таблицу.

## Индексы

| Индекс | Назначение |
|---|---|
| `idx_assignments_user_due` | Быстрый список ближайших заданий пользователя |
| `idx_assignments_user_status` | Фильтрация по статусу |
| `idx_assignments_user_priority` | Фильтрация по приоритету |
| `idx_assignments_course` | Получение заданий дисциплины |
| `idx_reminders_assignment` | Получение напоминаний задания |
