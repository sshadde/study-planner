# Стратегия ORM

## Назначение

Стратегия ORM описывает, как серверные JPA Entity отображаются на физическую модель PostgreSQL и как это поддерживает архитектуру PCMEF. Entity-слой содержит состояние и доменные методы, Foundation-слой содержит Spring Data JPA репозитории, а Mediator-слой управляет транзакциями и бизнес-правилами.

## Соответствие Entity и таблиц

| JPA Entity | Таблица | Основные связи в коде |
|---|---|---|
| `User` | `users` | `@OneToOne(mappedBy = "user") StudentProfile` |
| `StudentProfile` | `student_profiles` | `@OneToOne User` |
| `Course` | `courses` | `@ManyToOne User` |
| `Assignment` | `assignments` | `@ManyToOne User`, `@ManyToOne Course` |
| `Reminder` | `reminders` | `@ManyToOne Assignment` |

Связи между дисциплинами, заданиями и напоминаниями в JPA-модели описаны преимущественно со стороны дочерних сущностей. Обратные коллекции вроде `User.courses`, `Course.assignments` и `Assignment.reminders` в Entity-классах не объявлены, чтобы не усложнять модель лишним состоянием.

## Enum-поля

Enum-поля хранятся как строки через `@Enumerated(EnumType.STRING)`.

| Enum | Значения | Таблица и поле |
|---|---|---|
| `UserRole` | `STUDENT`, `ADMIN` | `users.role` |
| `AssignmentStatus` | `NEW`, `IN_PROGRESS`, `DONE`, `OVERDUE`, `ARCHIVED` | `assignments.status` |
| `AssignmentPriority` | `LOW`, `MEDIUM`, `HIGH` | `assignments.priority` |

Такой подход сохраняет читаемость данных в БД и не ломает смысл значений при изменении порядка enum в Java-коде.

## Правила загрузки связей

| Связь | Fetch strategy | Обоснование |
|---|---|---|
| `User -> StudentProfile` | `LAZY` | Профиль нужен только в сценариях пользователя |
| `StudentProfile -> User` | `LAZY` | Профиль обращается к пользователю только при необходимости |
| `Course -> User` | `LAZY` | Дисциплина всегда принадлежит пользователю, но данные пользователя не нужны в каждом ответе |
| `Assignment -> User` | `LAZY` | Задания фильтруются по владельцу, но объект пользователя не отдается наружу |
| `Assignment -> Course` | `LAZY` | Дисциплина используется при формировании DTO и проверке владения |
| `Reminder -> Assignment` | `LAZY` | Напоминания загружаются в сценариях конкретного задания |

Для API-ответов используются DTO, поэтому Entity не отдаются наружу напрямую.

## Транзакции

| Сценарий | Транзакция | Особенности |
|---|---|---|
| Регистрация | `@Transactional` | Создаются `User` и `StudentProfile`, пароль хешируется до сохранения |
| Создание задания | `@Transactional` | Проверяется владение дисциплиной, создается `Assignment`, опционально `Reminder` |
| Изменение статуса | `@Transactional` | Entity-метод переводит статус и обновляет `completedAt` |
| Получение списка | `@Transactional(readOnly = true)` | Данные фильтруются по текущему пользователю |
| Удаление задания | `@Transactional` | Задание удаляется через репозиторий; связанные напоминания удаляются на уровне ограничения БД |
| Удаление дисциплины | `@Transactional` | Перед удалением проверяется, что дисциплина не используется заданиями |

## Доменные методы Entity

| Entity | Метод | Назначение |
|---|---|---|
| `Assignment` | `changeStatus(AssignmentStatus status)` | Централизованное изменение статуса |
| `Assignment` | `markDone(Instant completedAt)` | Завершение задания и установка даты выполнения |
| `Assignment` | `isOverdue(Instant now)` | Проверка просрочки |
| `Assignment` | `rename(String title)` | Изменение названия с базовой валидацией |
| `Reminder` | `disable()` | Отключение напоминания |
| `User` | `disable()` | Блокировка учетной записи |

## Разделение ответственности PCMEF

| Слой | Ответственность в работе с данными |
|---|---|
| Control | Получает DTO, вызывает сервисы, возвращает HTTP-ответы |
| Mediator | Проверяет права пользователя, выполняет бизнес-правила и транзакции |
| Entity | Хранит состояние и доменные методы без SQL и HTTP |
| Foundation | Реализует Spring Data JPA репозитории и запросы |

## Репозитории

Foundation-слой предоставляет репозитории с методами, ориентированными на реальные сценарии приложения:

```java
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {
    Optional<Assignment> findByIdAndUserId(Long id, Long userId);

    List<Assignment> findAllByUserIdOrderByDueAtAsc(Long userId);

    boolean existsByCourseId(Long courseId);
}
```

```java
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdAndUserId(Long id, Long userId);

    List<Course> findAllByUserIdOrderByTitleAsc(Long userId);

    boolean existsByUserIdAndTitleIgnoreCase(Long userId, String title);

    boolean existsByIdAndUserId(Long id, Long userId);
}
```

```java
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Optional<Reminder> findByIdAndAssignmentUserId(Long id, Long userId);

    List<Reminder> findAllByAssignmentIdAndAssignmentUserIdOrderByRemindAtAsc(Long assignmentId, Long userId);
}
```

Поиск заданий по нескольким параметрам реализуется через `JpaSpecificationExecutor<Assignment>` и класс `AssignmentSpecification`, чтобы не переносить SQL-логику в Mediator-слой.
