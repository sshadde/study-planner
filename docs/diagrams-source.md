# Исходники диаграмм

В этом файле собраны исходные PlantUML-коды диаграмм. В основных документах отображаются готовые изображения из `docs/images`.

## 00-initiation/business-classes.md

### Модель бизнес-классов

```plantuml
@startuml
title Бизнес-классы системы планирования учебных заданий

class Student {
  id
  fullName
  groupName
  email
}

class UserAccount {
  id
  email
  passwordHash
  role
}

class Course {
  id
  title
  teacherName
  semester
}

class Assignment {
  id
  title
  description
  dueDate
  priority
  status
}

class Reminder {
  id
  remindAt
  message
  isSent
}

Student "1" -- "1" UserAccount
Student "1" -- "*" Assignment
Course "1" -- "*" Assignment
Assignment "1" -- "*" Reminder

@enduml
```

## 00-initiation/business-use-cases.md

### BUC-диаграмма

```plantuml
@startuml
title BUC-диаграмма: бизнес-прецеденты

left to right direction

actor "Студент" as Student

rectangle "Система планирования учебных заданий" {
  usecase "Планировать учебные задания" as UC1
  usecase "Контролировать выполнение" as UC2
  usecase "Получать напоминания" as UC3
  usecase "Искать и фильтровать задания" as UC4
  usecase "Работать с заданиями без сети" as UC5
  usecase "Управлять дисциплинами" as UC6
}

Student --> UC1
Student --> UC2
Student --> UC3
Student --> UC4
Student --> UC5
Student --> UC6

@enduml
```

## 00-initiation/idef0-a0.md

### IDEF0 A-0

```plantuml
@startuml
title IDEF0 A-0: Планирование учебных заданий студента

rectangle "Планировать и контролировать\nучебные задания студента" as A0

rectangle "Информация о заданиях\nи дисциплинах" as Input
rectangle "Учебный план,\nдедлайны,\nправила дисциплин" as Control
rectangle "Студент,\nмобильное приложение,\nсервер, БД" as Mechanism
rectangle "План выполнения,\nстатусы,\nнапоминания" as Output

Input --> A0 : вход
Control --> A0 : управление
Mechanism --> A0 : механизм
A0 --> Output : выход

@enduml
```

## 01-requirements/domain-model.md

### Domain Model

```plantuml
@startuml
title Domain Model: планирование учебных заданий студентов

class User {
  id
  email
  passwordHash
  role
  createdAt
}

class StudentProfile {
  id
  fullName
  groupName
}

class Course {
  id
  title
  teacherName
  semester
  color
}

class Assignment {
  id
  title
  description
  dueDate
  priority
  status
  createdAt
  updatedAt
}

class Reminder {
  id
  remindAt
  message
  enabled
}

enum UserRole {
  STUDENT
  ADMIN
}

enum AssignmentStatus {
  NEW
  IN_PROGRESS
  DONE
  OVERDUE
  ARCHIVED
}

enum AssignmentPriority {
  LOW
  MEDIUM
  HIGH
}

User "1" -- "1" StudentProfile
User "1" -- "*" Course
User "1" -- "*" Assignment
Course "1" -- "*" Assignment
Assignment "1" -- "*" Reminder

User --> UserRole
Assignment --> AssignmentStatus
Assignment --> AssignmentPriority

@enduml
```

## 01-requirements/use-case-diagram.md

### Use Case диаграмма

```plantuml
@startuml
title Use Case диаграмма системы планирования учебных заданий

left to right direction

actor "Гость" as Guest
actor "Студент" as Student

rectangle "Мобильное приложение и сервер планирования заданий" {
  usecase "Зарегистрироваться" as UC_REGISTER
  usecase "Войти в систему" as UC_LOGIN
  usecase "Просмотреть список заданий" as UC_LIST
  usecase "Просмотреть детали задания" as UC_DETAILS
  usecase "Создать задание" as UC_CREATE
  usecase "Редактировать задание" as UC_UPDATE
  usecase "Удалить задание" as UC_DELETE
  usecase "Изменить статус задания" as UC_STATUS
  usecase "Искать и фильтровать задания" as UC_SEARCH
  usecase "Управлять дисциплинами" as UC_COURSES
  usecase "Создать напоминание" as UC_REMINDER
  usecase "Просмотреть профиль" as UC_PROFILE
  usecase "Работать с кэшем без сети" as UC_OFFLINE
  usecase "Синхронизировать данные" as UC_SYNC
}

Guest --> UC_REGISTER
Guest --> UC_LOGIN

Student --> UC_LIST
Student --> UC_DETAILS
Student --> UC_CREATE
Student --> UC_UPDATE
Student --> UC_DELETE
Student --> UC_STATUS
Student --> UC_SEARCH
Student --> UC_COURSES
Student --> UC_REMINDER
Student --> UC_PROFILE
Student --> UC_OFFLINE
Student --> UC_SYNC

UC_UPDATE ..> UC_DETAILS : <<include>>
UC_SEARCH ..> UC_LIST : <<extend>>
UC_OFFLINE ..> UC_LIST : <<extend>>
UC_SYNC ..> UC_OFFLINE : <<include>>

@enduml
```

## 02-architecture/package-diagram.md

### Диаграмма пакетов

```plantuml
@startuml
allowmixing
title Диаграмма пакетов PCMEF

package "Mobile Client" {
  package "presentation" {
    rectangle "AuthScreen"
    rectangle "AssignmentListScreen"
    rectangle "AssignmentDetailsScreen"
    rectangle "AssignmentEditScreen"
    rectangle "CoursesScreen"
    rectangle "ProfileScreen"
  }

  package "state" {
    rectangle "AuthViewModel"
    rectangle "AssignmentViewModel"
    rectangle "CourseViewModel"
    rectangle "ReminderViewModel"
  }

  package "remote" {
    interface "AuthApi"
    interface "AssignmentApi"
    interface "CourseApi"
    interface "ReminderApi"
  }

  package "local" {
    rectangle "RoomDatabase"
    interface "AssignmentDao"
    interface "CourseDao"
  }

  package "sync" {
    rectangle "AssignmentSyncManager"
  }
}

package "Backend: Presentation/Control boundary" {
  package "control" {
    rectangle "AuthController"
    rectangle "AssignmentController"
    rectangle "CourseController"
    rectangle "ReminderController"
  }
}

package "Backend: Mediator" {
  package "mediator" {
    interface "AuthService"
    interface "AssignmentService"
    interface "CourseService"
    interface "ReminderService"
    rectangle "AuthServiceImpl"
    rectangle "AssignmentServiceImpl"
    rectangle "CourseServiceImpl"
    rectangle "ReminderServiceImpl"
  }
}

package "Backend: Entity" {
  package "entity" {
    rectangle "User"
    rectangle "StudentProfile"
    rectangle "Assignment"
    rectangle "Course"
    rectangle "Reminder"
  }
}

package "Backend: Foundation" {
  package "foundation" {
    interface "UserRepository"
    interface "AssignmentRepository"
    interface "CourseRepository"
    interface "ReminderRepository"
    rectangle "JwtProvider"
    rectangle "PasswordEncoderConfig"
  }
}

database "PostgreSQL" as DB

"presentation" --> "state"
"state" --> "remote"
"state" --> "local"
"sync" --> "remote"
"sync" --> "local"
"remote" --> "control" : REST/JSON

"control" --> "mediator"
"mediator" --> "entity"
"mediator" --> "foundation"
"foundation" --> "entity"
"foundation" --> DB

@enduml
```

## 02-architecture/dependency-diagram.md

### Диаграмма зависимостей

```plantuml
@startuml
title Зависимости PCMEF

skinparam componentStyle rectangle

component "Мобильное представление\nCompose-экраны" as P
component "Состояние клиента\nViewModel, StateFlow" as MS
component "Control\nREST-контроллеры" as C
component "Mediator\nсервисы, транзакции" as M
component "Entity\nдоменная модель, JPA" as E
component "Foundation\nрепозитории, security" as F
database "PostgreSQL" as DB
component "Room-кэш" as ROOM

P --> MS : события UI / состояние
MS --> C : REST через Retrofit
MS --> ROOM : чтение кэша
MS --> ROOM : запись кэша
C --> M : сервисные интерфейсы
M --> E : доменные методы
M --> F : интерфейсы репозиториев
F --> E : JPA-отображение
F --> DB : SQL/JPA

note right of C
Control проверяет запросы
и передает сценарии сервисам.
end note

note right of M
Mediator содержит бизнес-правила,
транзакции и проверки владельца.
end note

note right of F
Foundation не принимает
бизнес-решения о доступе.
end note

@enduml
```

## 03-database/er-diagram.md

### ER-диаграмма

```plantuml
@startuml
title ER-диаграмма: Student Study Planner

hide circle
skinparam linetype ortho

entity "users" as users {
  * id : bigint <<PK>>
  --
  * email : varchar(255) <<UK>>
  * password_hash : varchar(255)
  * role : varchar(20)
  * enabled : boolean
  * created_at : timestamp
  * updated_at : timestamp
}

entity "student_profiles" as profiles {
  * id : bigint <<PK>>
  --
  * user_id : bigint <<FK, UK>>
  * full_name : varchar(160)
  * group_name : varchar(80)
}

entity "courses" as courses {
  * id : bigint <<PK>>
  --
  * user_id : bigint <<FK>>
  * title : varchar(160)
  teacher_name : varchar(160)
  semester : smallint
  color : varchar(16)
  * created_at : timestamp
  * updated_at : timestamp
}

entity "assignments" as assignments {
  * id : bigint <<PK>>
  --
  * user_id : bigint <<FK>>
  * course_id : bigint <<FK>>
  * title : varchar(200)
  description : text
  * due_at : timestamp
  * priority : varchar(20)
  * status : varchar(20)
  * created_at : timestamp
  * updated_at : timestamp
  completed_at : timestamp
}

entity "reminders" as reminders {
  * id : bigint <<PK>>
  --
  * assignment_id : bigint <<FK>>
  * remind_at : timestamp
  message : varchar(500)
  * enabled : boolean
  sent_at : timestamp
  * created_at : timestamp
}

users ||--|| profiles : has
users ||--o{ courses : owns
users ||--o{ assignments : owns
courses ||--o{ assignments : groups
assignments ||--o{ reminders : schedules

@enduml
```

## 04-detailed-design/sequence-diagrams.md

### SD-01. Вход пользователя

```plantuml
@startuml
title SD-01. Вход пользователя

actor "Гость" as Guest
participant "LoginScreen" as Screen
participant "AuthViewModel" as ViewModel
participant "AuthRepository" as MobileRepo
participant "TokenStore" as TokenStore
participant "AuthApi" as Api
participant "AuthController" as Controller
participant "AuthService" as Service
participant "UserRepository" as UserRepo
participant "PasswordEncoder" as Encoder
participant "JwtProvider" as Jwt

Guest -> Screen : вводит email и пароль
Screen -> ViewModel : login(email, password)
ViewModel -> MobileRepo : login(email, password)
MobileRepo -> Api : POST /api/auth/login
Api -> Controller : LoginRequest
Controller -> Service : login(request)
Service -> UserRepo : findByEmail(email)
UserRepo --> Service : User
Service -> Encoder : matches(rawPassword, passwordHash)
Encoder --> Service : true
Service -> Jwt : generateToken(user)
Jwt --> Service : accessToken
Service --> Controller : AuthResponse
Controller --> Api : 200 OK
Api --> MobileRepo : AuthResponseDto
MobileRepo -> TokenStore : save(accessToken)
MobileRepo -> Api : GET /api/auth/me
Api --> MobileRepo : CurrentUserDto
MobileRepo -> TokenStore : saveUser(currentUser)
MobileRepo --> ViewModel : ResultState.Success(CurrentUserDto)
ViewModel --> Screen : AuthUiState(user = currentUser)
Screen --> Guest : открывает список заданий

alt Неверные учетные данные
    Service --> Controller : AuthenticationException
    Controller --> Api : 401 Unauthorized
    Api --> MobileRepo : error
    MobileRepo --> ViewModel : ResultState.Error(message)
    ViewModel --> Screen : AuthUiState(error = message)
end

@enduml
```

### SD-02. Создание учебного задания

```plantuml
@startuml
title SD-02. Создание учебного задания

actor "Студент" as Student
participant "AssignmentEditScreen" as Screen
participant "AssignmentViewModel" as ViewModel
participant "AssignmentRepository" as MobileRepo
participant "PendingOperationDao" as PendingDao
participant "Room AssignmentDao" as Dao
participant "AssignmentApi" as Api
participant "AssignmentController" as Controller
participant "AssignmentService" as Service
participant "CourseRepository" as CourseRepo
participant "AssignmentRepository" as AssignmentRepo
participant "Assignment" as Assignment

Student -> Screen : заполняет форму задания
Screen -> ViewModel : createAssignment(form, onDone)
ViewModel -> MobileRepo : createAssignment(form)

alt Сеть доступна
    MobileRepo -> Api : POST /api/assignments
    Api -> Controller : AssignmentCreateRequest
    Controller -> Service : createAssignment(userId, request)
    Service -> CourseRepo : findByIdAndUserId(courseId, userId)
    CourseRepo --> Service : Course
    Service -> Assignment : new Assignment(...)
    Assignment --> Service : Assignment
    Service -> AssignmentRepo : save(assignment)
    AssignmentRepo --> Service : saved Assignment
    Service --> Controller : AssignmentResponse
    Controller --> Api : 201 Created
    Api --> MobileRepo : AssignmentDto
    MobileRepo -> Dao : upsert(dto.toLocal())
    MobileRepo --> ViewModel : ResultState.Success(Assignment)
    ViewModel -> ViewModel : loadAssignments()
    ViewModel --> Screen : onDone()
else Нет сети или сервер недоступен
    MobileRepo -> PendingDao : insert(CREATE_ASSIGNMENT, payload)
    PendingDao --> MobileRepo : saved
    MobileRepo --> ViewModel : ResultState.Error("Сохранено локально...")
    ViewModel --> Screen : error = message
end

@enduml
```

### SD-03. Поиск и фильтрация заданий

```plantuml
@startuml
title SD-03. Поиск и фильтрация заданий

actor "Студент" as Student
participant "AssignmentListScreen" as Screen
participant "AssignmentViewModel" as ViewModel
participant "AssignmentRepository" as MobileRepo
participant "AssignmentApi" as Api
participant "AssignmentController" as Controller
participant "AssignmentService" as Service
participant "AssignmentRepository" as AssignmentRepo
participant "AssignmentSpecification" as Spec
participant "Room AssignmentDao" as Dao

Student -> Screen : вводит запрос и выбирает фильтры
Screen -> ViewModel : loadAssignments(filter)
ViewModel -> MobileRepo : getAssignments(filter)

alt Сеть доступна
    MobileRepo -> Api : GET /api/assignments?status&priority&courseId&query
    Api -> Controller : AssignmentFilter
    Controller -> Service : getAssignments(userId, filter)
    Service -> Spec : byUserAndFilter(userId, filter)
    Spec --> Service : Specification
    Service -> AssignmentRepo : findAll(specification)
    AssignmentRepo --> Service : List<Assignment>
    Service --> Controller : List<AssignmentResponse>
    Controller --> Api : 200 OK
    Api --> MobileRepo : List<AssignmentDto>
    MobileRepo -> Dao : upsertAll(dto.toLocal())
    MobileRepo --> ViewModel : ResultState.Success(assignments, fromCache = false)
else Сеть недоступна
    MobileRepo -> Dao : search(query, status, priority, courseId)
    Dao --> MobileRepo : List<LocalAssignment>
    MobileRepo --> ViewModel : ResultState.Success(assignments, fromCache = true)
end

ViewModel --> Screen : AssignmentListUiState(items, offline)
Screen --> Student : показывает найденные задания

@enduml
```

### SD-04. Оффлайн-синхронизация

```plantuml
@startuml
title SD-04. Оффлайн-синхронизация

actor "Студент" as Student
participant "AssignmentListScreen" as Screen
participant "AssignmentViewModel" as ViewModel
participant "AssignmentRepository" as MobileRepo
participant "PendingOperationDao" as PendingDao
participant "Room AssignmentDao" as Dao
participant "AssignmentApi" as Api
participant "AssignmentController" as Controller
participant "AssignmentService" as Service

Student -> Screen : нажимает обновление списка
Screen -> ViewModel : synchronizeAndLoad()
ViewModel -> MobileRepo : syncPendingOperations()
MobileRepo -> PendingDao : findAll()
PendingDao --> MobileRepo : pending operations

loop для каждой pending-операции
    MobileRepo -> Api : POST/PUT/PATCH/DELETE /api/assignments
    Api -> Controller : REST request
    Controller -> Service : execute scenario
    Service --> Controller : response
    Controller --> Api : 2xx
    Api --> MobileRepo : success
    MobileRepo -> PendingDao : delete(operation)
end

ViewModel -> MobileRepo : getAssignments(lastFilter)

alt Сервер доступен
    MobileRepo -> Api : GET /api/assignments
    Api --> MobileRepo : List<AssignmentDto>
    MobileRepo -> Dao : upsertAll(dto.toLocal())
    MobileRepo --> ViewModel : ResultState.Success(assignments, fromCache = false)
    ViewModel --> Screen : AssignmentListUiState(items, offline = false)
else Сервер недоступен
    MobileRepo -> Dao : search(lastFilter)
    Dao --> MobileRepo : cached assignments
    MobileRepo --> ViewModel : ResultState.Success(assignments, fromCache = true)
    ViewModel --> Screen : AssignmentListUiState(items, offline = true)
end

@enduml
```

## 04-detailed-design/design-class-diagram.md

### Диаграмма классов: общий обзор

```plantuml
@startuml
title Диаграмма классов проектирования: общий обзор

left to right direction
skinparam linetype ortho
skinparam shadowing false
hide members

package "Mobile" {
  class LoginScreen
  class AssignmentListScreen
  class AssignmentDetailsScreen
  class AssignmentEditScreen
  class CoursesScreen
  class AuthViewModel
  class AssignmentViewModel
  class CourseViewModel
  class ReminderViewModel
  class "AuthRepository" as MAuthRepo
  class "AssignmentRepository" as MAssignmentRepo
  class "CourseRepository" as MCourseRepo
  class "ReminderRepository" as MReminderRepo
}

package "Backend" {
  class AuthController
  class AssignmentController
  class CourseController
  class ReminderController
  interface AuthService
  interface AssignmentService
  interface CourseService
  interface ReminderService
  interface UserRepository
  interface "AssignmentRepository" as BAssignmentRepo
  interface CourseRepository
  interface ReminderRepository
}

LoginScreen --> AuthViewModel
AssignmentListScreen --> AssignmentViewModel
AssignmentDetailsScreen --> AssignmentViewModel
AssignmentDetailsScreen --> ReminderViewModel
AssignmentEditScreen --> AssignmentViewModel
AssignmentEditScreen --> CourseViewModel
CoursesScreen --> CourseViewModel

AuthViewModel --> MAuthRepo
AssignmentViewModel --> MAssignmentRepo
CourseViewModel --> MCourseRepo
ReminderViewModel --> MReminderRepo

MAuthRepo --> AuthController : REST
MAssignmentRepo --> AssignmentController : REST
MCourseRepo --> CourseController : REST
MReminderRepo --> ReminderController : REST

AuthController --> AuthService
AssignmentController --> AssignmentService
CourseController --> CourseService
ReminderController --> ReminderService

AuthService --> UserRepository
AssignmentService --> BAssignmentRepo
AssignmentService --> CourseRepository
CourseService --> CourseRepository
ReminderService --> ReminderRepository
ReminderService --> BAssignmentRepo

@enduml
```

### Диаграмма классов: мобильный клиент

```plantuml
@startuml
title Диаграмма классов: мобильный клиент

left to right direction
skinparam linetype ortho
skinparam shadowing false

package "UI" {
  class LoginScreen
  class AssignmentListScreen
  class AssignmentDetailsScreen
  class AssignmentEditScreen
  class CoursesScreen
  class ProfileScreen
}

package "State" {
  class AuthViewModel {
    +restoreSession()
    +login(email: String, password: String)
    +register(email: String, password: String, fullName: String, groupName: String)
    +logout()
  }

  class AssignmentViewModel {
    +loadAssignments(filter: AssignmentFilter)
    +synchronizeAndLoad()
    +createAssignment(form: AssignmentForm, onDone: () -> Unit)
    +updateAssignment(id: Long, form: AssignmentForm, onDone: () -> Unit)
    +changeStatus(id: Long, status: AssignmentStatus)
  }

  class CourseViewModel {
    +loadCourses()
    +createCourse(form: CourseForm)
    +updateCourse(id: Long, form: CourseForm)
  }

  class ReminderViewModel {
    +loadReminders(assignmentId: Long)
    +createReminder(assignmentId: Long, form: ReminderForm)
  }
}

package "Repository" {
  class "AuthRepository" as AuthRepo
  class "AssignmentRepository" as AssignmentRepo
  class "CourseRepository" as CourseRepo
  class "ReminderRepository" as ReminderRepo
  class TokenStore
}

package "Remote / Local" {
  interface AuthApi
  interface AssignmentApi
  interface CourseApi
  interface ReminderApi
  interface AssignmentDao
  interface CourseDao
  interface PendingOperationDao
}

LoginScreen --> AuthViewModel
AssignmentListScreen --> AssignmentViewModel
AssignmentDetailsScreen --> AssignmentViewModel
AssignmentDetailsScreen --> ReminderViewModel
AssignmentEditScreen --> AssignmentViewModel
AssignmentEditScreen --> CourseViewModel
CoursesScreen --> CourseViewModel

AuthViewModel --> AuthRepo
AssignmentViewModel --> AssignmentRepo
CourseViewModel --> CourseRepo
ReminderViewModel --> ReminderRepo

AuthRepo --> AuthApi
AuthRepo --> TokenStore
AssignmentRepo --> AssignmentApi
AssignmentRepo --> AssignmentDao
AssignmentRepo --> PendingOperationDao
CourseRepo --> CourseApi
CourseRepo --> CourseDao
ReminderRepo --> ReminderApi

@enduml
```

### Диаграмма классов: backend PCMEF

```plantuml
@startuml
title Диаграмма классов: backend PCMEF

left to right direction
skinparam linetype ortho
skinparam shadowing false

package "Control" {
  class AuthController {
    +register(request: RegisterRequest): AuthResponse
    +login(request: LoginRequest): AuthResponse
    +me(principal: CurrentUser): CurrentUserResponse
  }

  class AssignmentController {
    +findAll(filter: AssignmentFilter): List<AssignmentResponse>
    +findById(id: Long): AssignmentResponse
    +create(request: AssignmentCreateRequest): AssignmentResponse
    +update(id: Long, request: AssignmentUpdateRequest): AssignmentResponse
    +changeStatus(id: Long, request: StatusChangeRequest): AssignmentResponse
  }

  class CourseController
  class ReminderController
  class AdminController
}

package "Mediator" {
  interface AuthService
  interface AssignmentService
  interface CourseService
  interface ReminderService
  interface AdminService
  class AuthServiceImpl
  class AssignmentServiceImpl
  class CourseServiceImpl
  class ReminderServiceImpl
  class AdminServiceImpl
  class AssignmentMapper
  class CourseMapper
  class ReminderMapper
}

package "Foundation" {
  interface UserRepository
  interface "AssignmentRepository" as AssignmentRepo
  interface CourseRepository
  interface ReminderRepository
  class JwtProvider
  class SecurityConfig
  class AssignmentSpecification
}

AuthController --> AuthService
AssignmentController --> AssignmentService
CourseController --> CourseService
ReminderController --> ReminderService
AdminController --> AdminService

AuthServiceImpl ..|> AuthService
AssignmentServiceImpl ..|> AssignmentService
CourseServiceImpl ..|> CourseService
ReminderServiceImpl ..|> ReminderService
AdminServiceImpl ..|> AdminService

AuthServiceImpl --> UserRepository
AuthServiceImpl --> JwtProvider
AssignmentServiceImpl --> AssignmentRepo
AssignmentServiceImpl --> CourseRepository
AssignmentServiceImpl --> AssignmentMapper
CourseServiceImpl --> CourseRepository
CourseServiceImpl --> CourseMapper
ReminderServiceImpl --> ReminderRepository
ReminderServiceImpl --> AssignmentRepo
ReminderServiceImpl --> ReminderMapper
AdminServiceImpl --> UserRepository
AssignmentRepo --> AssignmentSpecification

@enduml
```

### Диаграмма классов: Entity

```plantuml
@startuml
title Диаграмма классов: Entity

left to right direction
skinparam linetype ortho
skinparam shadowing false

class User {
  -id: Long
  -email: String
  -passwordHash: String
  -role: UserRole
  +disable()
}

class StudentProfile {
  -id: Long
  -fullName: String
  -groupName: String
}

class Course {
  -id: Long
  -title: String
  -semester: Short
  +rename(title: String)
}

class Assignment {
  -id: Long
  -title: String
  -dueAt: Instant
  -priority: AssignmentPriority
  -status: AssignmentStatus
  +changeStatus(status: AssignmentStatus)
  +markDone(completedAt: Instant)
  +isOverdue(now: Instant): boolean
}

class Reminder {
  -id: Long
  -remindAt: Instant
  -enabled: boolean
  +disable()
}

StudentProfile --> User
Course --> User
Assignment --> User
Assignment --> Course
Reminder --> Assignment

@enduml
```

## 08-finalization/gantt.md

### Диаграмма Ганта

```plantuml
@startgantt
title План выполнения курсового проекта
Project starts 2026-02-03

[Этап 0. Инициация и бизнес-анализ] lasts 14 days
[Этап 0. Инициация и бизнес-анализ] is 100% completed

[Этап 1. Определение требований] starts at [Этап 0. Инициация и бизнес-анализ]'s end
[Этап 1. Определение требований] lasts 14 days
[Этап 1. Определение требований] is 100% completed

[Этап 2. Архитектура PCMEF] starts at [Этап 1. Определение требований]'s end
[Этап 2. Архитектура PCMEF] lasts 14 days
[Этап 2. Архитектура PCMEF] is 100% completed

[Этап 3. Проектирование БД] starts at [Этап 2. Архитектура PCMEF]'s end
[Этап 3. Проектирование БД] lasts 14 days
[Этап 3. Проектирование БД] is 100% completed

[Этап 4. Детальное проектирование] starts at [Этап 3. Проектирование БД]'s end
[Этап 4. Детальное проектирование] lasts 14 days
[Этап 4. Детальное проектирование] is 100% completed

[Этап 5. Реализация ядра] starts at [Этап 4. Детальное проектирование]'s end
[Этап 5. Реализация ядра] lasts 14 days
[Этап 5. Реализация ядра] is 100% completed

[Этап 6. Рефакторинг и качество] starts at [Этап 5. Реализация ядра]'s end
[Этап 6. Рефакторинг и качество] lasts 14 days
[Этап 6. Рефакторинг и качество] is 100% completed

[Этап 7. Мобильный интерфейс] starts at [Этап 6. Рефакторинг и качество]'s end
[Этап 7. Мобильный интерфейс] lasts 14 days
[Этап 7. Мобильный интерфейс] is 100% completed

[Этап 8. Завершение и защита] starts at [Этап 7. Мобильный интерфейс]'s end
[Этап 8. Завершение и защита] lasts 14 days
[Этап 8. Завершение и защита] is 100% completed
@endgantt
```
