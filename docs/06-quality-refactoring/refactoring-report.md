# Отчет о рефакторинге

## Data Mapper

Data Mapper используется для отделения доменных Entity от объектов передачи данных REST API.

| Mapper | Направление | Назначение |
|---|---|---|
| `AssignmentMapper` | `Assignment` -> `AssignmentResponse` | Формирует DTO задания для мобильного клиента |
| `CourseMapper` | `Course` -> `CourseResponse` | Формирует DTO дисциплины |
| `ReminderMapper` | `Reminder` -> `ReminderResponse` | Формирует DTO напоминания |

До применения Data Mapper контроллеры или сервисы могли бы возвращать JPA Entity напрямую. Это нарушило бы границы PCMEF: наружу вышли бы persistence-поля, lazy-связи и внутренняя структура доменной модели.

После рефакторинга:

- Control-слой работает только с request/response DTO;
- Entity не зависит от DTO;
- Mediator управляет преобразованием через mapper-классы;
- мобильный клиент получает стабильный API-контракт.

## Identity Map

Identity Map добавлен как инфраструктурный паттерн `ru.studyplanner.foundation.IdentityMap`.

Назначение:

- хранить один объект на один идентификатор в рамках операции;
- не создавать повторные response-объекты для одной и той же сущности;
- явно показать применение паттерна, требуемого методическими указаниями.

Использование:

```java
IdentityMap<Long, AssignmentResponse> responseIdentityMap = new IdentityMap<>();
assignmentRepository.findAll(specification)
        .forEach(assignment -> responseIdentityMap.getOrPut(
                assignment.getId(),
                id -> assignmentMapper.toResponse(assignment)
        ));
```

Дополнительно Identity Map реализуется Hibernate Persistence Context на уровне JPA-транзакции: одна строка БД с одним id представлена одним Entity-экземпляром внутри persistence context.

## Улучшения после рефакторинга

| Изменение | Эффект |
|---|---|
| Добавлена Checkstyle-конфигурация | Появился воспроизводимый статический анализ |
| Добавлен `IdentityMap` | Формально и кодово закрыто требование Identity Map |
| Добавлен `IdentityMapTest` | Паттерн проверяется unit-тестом |
| Обновлен `AssignmentServiceImpl` | Список заданий использует Identity Map при DTO-маппинге |
| Обновлена документация этапа 5 | Зафиксированы актуальные тесты и покрытие |
| Добавлены controller/security integration-тесты | REST-слой и авторизация проверяются через MockMvc |
| CurrentUser перенесен из Foundation в Entity | Устранена спорная зависимость Control -> Foundation |

## Соответствие PCMEF

Рефакторинг не нарушает направление зависимостей:

```text
Control -> Mediator -> Entity -> Foundation
```

`IdentityMap` находится в Foundation, mapper-классы находятся в Mediator, Entity остается независимым от REST DTO и инфраструктуры. Контроллеры больше не импортируют `foundation.CurrentUser`; security principal размещен как простой доменный record `entity.CurrentUser`.
