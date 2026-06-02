# Обновленные тесты

## Набор тестов после рефакторинга

| Тестовый класс | Количество тестов | Назначение |
|---|---:|---|
| `AssignmentControllerIntegrationTest` | 2 | REST validation, создание и получение заданий через controller |
| `SecurityControllerIntegrationTest` | 3 | JWT-защита, роли, ошибка входа |
| `AssignmentTest` | 4 | Доменные методы задания |
| `IdentityMapTest` | 1 | Проверка уникальности объекта по ключу |
| `JwtProviderTest` | 2 | Генерация и проверка JWT |
| `AssignmentServiceImplTest` | 4 | Сервисные сценарии заданий |
| `AuthServiceImplTest` | 1 | Негативный сценарий входа |
| `CourseServiceImplTest` | 1 | Бизнес-правило удаления дисциплины |
| `MapperTest` | 3 | Data Mapper-классы |
| `ReminderServiceImplTest` | 1 | Бизнес-правило напоминания |

## Результат запуска

```text
Tests run: 22
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## Покрытие JaCoCo

| Пакет | Instruction coverage |
|---|---:|
| `ru.studyplanner` | 37.50% |
| `ru.studyplanner.control` | 43.17% |
| `ru.studyplanner.entity` | 79.21% |
| `ru.studyplanner.foundation` | 76.18% |
| `ru.studyplanner.mediator` | 54.25% |
| `ru.studyplanner.mediator.dto` | 69.07% |
| Общий показатель | 66.81% |

Требование методических указаний: покрытие более 40%. Требование выполнено.
