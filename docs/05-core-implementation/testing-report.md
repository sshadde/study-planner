# Отчет о тестировании серверного ядра

## Инструменты

| Инструмент | Назначение |
|---|---|
| JUnit 5 | Модульные и integration-тесты |
| Mockito | Изоляция сервисов от репозиториев в unit-тестах |
| Spring Boot Test | Поднятие тестового application context |
| MockMvc | Проверка REST controller и security-сценариев без внешнего сервера |
| AssertJ | Читаемые проверки |
| JaCoCo | Отчет о покрытии |
| Maven Surefire | Запуск тестов |

## Проверенные сценарии

| Тестовый класс | Проверки |
|---|---|
| `AssignmentControllerIntegrationTest` | Validation error, создание задания через REST, получение списка заданий |
| `SecurityControllerIntegrationTest` | Обязательный JWT, запрет student-доступа к admin endpoint, `401` при неверном пароле |
| `AssignmentTest` | Доменные правила статусов, просрочка, запрет пустого названия |
| `JwtProviderTest` | Генерация, разбор и отклонение измененного JWT |
| `AssignmentServiceImplTest` | Поиск, создание, обновление и смена статуса задания |
| `AuthServiceImplTest` | Отклонение неверного пароля |
| `CourseServiceImplTest` | Запрет удаления дисциплины со связанными заданиями |
| `MapperTest` | Маппинг Entity в response DTO |
| `ReminderServiceImplTest` | Запрет напоминания позже дедлайна |
| `IdentityMapTest` | Уникальность объекта по ключу |

## Результат последнего запуска

```text
Tests run: 22
Failures: 0
Errors: 0
Skipped: 0
Checkstyle violations: 0
Build: SUCCESS
```

## Покрытие JaCoCo

| Метрика | Значение |
|---|---:|
| Покрытые инструкции | 1683 |
| Всего инструкций | 2519 |
| Instruction coverage | 66.81% |
| Branch coverage | 49.14% |

Порог методических указаний: более 40%. Требование выполнено с запасом.

## Покрытие по пакетам

| Пакет | Instruction coverage |
|---|---:|
| `ru.studyplanner` | 37.50% |
| `ru.studyplanner.control` | 43.17% |
| `ru.studyplanner.entity` | 79.21% |
| `ru.studyplanner.foundation` | 76.18% |
| `ru.studyplanner.mediator` | 54.25% |
| `ru.studyplanner.mediator.dto` | 69.07% |
