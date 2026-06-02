# Спецификация интерфейсов

## Control -> Mediator

Контроллеры не реализуют бизнес-логику самостоятельно. Они получают запрос, выполняют первичную валидацию DTO, извлекают текущего пользователя из security context и вызывают сервисный интерфейс.

### AuthService

```java
public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    CurrentUserResponse getCurrentUser(Long userId);
}
```

### AssignmentService

```java
public interface AssignmentService {
    List<AssignmentResponse> getAssignments(Long userId, AssignmentFilter filter);

    AssignmentResponse getAssignmentById(Long userId, Long assignmentId);

    AssignmentResponse createAssignment(Long userId, AssignmentCreateRequest request);

    AssignmentResponse updateAssignment(Long userId, Long assignmentId, AssignmentUpdateRequest request);

    void deleteAssignment(Long userId, Long assignmentId);

    AssignmentResponse changeStatus(Long userId, Long assignmentId, AssignmentStatus status);
}
```

### CourseService

```java
public interface CourseService {
    List<CourseResponse> getCourses(Long userId);

    CourseResponse createCourse(Long userId, CourseCreateRequest request);

    CourseResponse updateCourse(Long userId, Long courseId, CourseUpdateRequest request);

    void deleteCourse(Long userId, Long courseId);
}
```

### ReminderService

```java
public interface ReminderService {
    ReminderResponse createReminder(Long userId, Long assignmentId, ReminderCreateRequest request);

    List<ReminderResponse> getAssignmentReminders(Long userId, Long assignmentId);

    void deleteReminder(Long userId, Long reminderId);
}
```

## Mediator -> Foundation

Сервисы используют репозитории Spring Data JPA. Бизнес-правила остаются в сервисах, а репозитории отвечают за получение и сохранение данных.

### UserRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
```

### AssignmentRepository

```java
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, JpaSpecificationExecutor<Assignment> {
    Optional<Assignment> findByIdAndUserId(Long id, Long userId);

    List<Assignment> findAllByUserIdOrderByDueAtAsc(Long userId);

    boolean existsByCourseId(Long courseId);
}
```

Фильтрация заданий по статусу, приоритету, дисциплине и строке поиска выполняется через унаследованный метод `findAll(Specification)` и `AssignmentSpecification`.

### CourseRepository

```java
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdAndUserId(Long id, Long userId);

    List<Course> findAllByUserIdOrderByTitleAsc(Long userId);

    boolean existsByUserIdAndTitleIgnoreCase(Long userId, String title);

    boolean existsByIdAndUserId(Long id, Long userId);
}
```

### ReminderRepository

```java
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Optional<Reminder> findByIdAndAssignmentUserId(Long id, Long userId);

    List<Reminder> findAllByAssignmentIdAndAssignmentUserIdOrderByRemindAtAsc(Long assignmentId, Long userId);
}
```

## Mobile -> Backend REST contracts

Мобильный клиент обращается к серверу через Retrofit-интерфейсы. Основной контракт заданий выглядит так:

```kotlin
interface AssignmentApi {
    @GET("api/assignments")
    suspend fun getAssignments(
        @Query("status") status: String?,
        @Query("priority") priority: String?,
        @Query("courseId") courseId: Long?,
        @Query("query") query: String?
    ): List<AssignmentDto>

    @GET("api/assignments/{id}")
    suspend fun getAssignment(@Path("id") id: Long): AssignmentDto

    @POST("api/assignments")
    suspend fun createAssignment(@Body request: AssignmentCreateDto): AssignmentDto

    @PUT("api/assignments/{id}")
    suspend fun updateAssignment(
        @Path("id") id: Long,
        @Body request: AssignmentUpdateDto
    ): AssignmentDto

    @PATCH("api/assignments/{id}/status")
    suspend fun changeStatus(
        @Path("id") id: Long,
        @Body request: StatusChangeDto
    ): AssignmentDto

    @DELETE("api/assignments/{id}")
    suspend fun deleteAssignment(@Path("id") id: Long)
}
```

## DTO-границы

| Направление | Объекты | Назначение |
|---|---|---|
| Mobile -> Control | Request DTO | Передача данных форм и фильтров |
| Control -> Mobile | Response DTO | Возврат данных без раскрытия внутренней структуры Entity |
| Mediator -> Entity | Domain objects | Выполнение бизнес-операций |
| Mediator -> Foundation | Entity / фильтры | Сохранение и поиск данных |
