# Course Project: Student Study Planner

## Название проекта

Разработка мобильного приложения для информационной системы планирования учебных заданий студентов.

## Траектория

Траектория В: мобильная разработка.

## Краткое описание

Проект представляет собой клиент-серверную информационную систему для планирования учебных заданий студентов. Мобильное приложение помогает студенту вести список учебных задач, отслеживать сроки, приоритеты, статусы выполнения, дисциплины и напоминания. Серверная часть предоставляет REST API, хранит данные в PostgreSQL и обеспечивает аутентификацию через JWT.

## Планируемый стек

- Mobile: Kotlin, Jetpack Compose, ViewModel, StateFlow, Retrofit, Room.
- Backend: Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA.
- Database: PostgreSQL.
- API docs: OpenAPI / Swagger UI.
- Tests: JUnit, Mockito, JaCoCo.
- Deployment: JAR, Docker Compose для backend + PostgreSQL.

## Структура репозитория

```text
docs/
  00-initiation/          Этап 0: инициация и бизнес-анализ
  01-requirements/        Этап 1: требования
  02-architecture/        Этап 2: архитектура PCMEF
  03-database/            Этап 3: проектирование БД
  04-detailed-design/     Этап 4: детальное проектирование
  05-core-implementation/ Этап 5: реализация ядра
  06-quality-refactoring/ Этап 6: качество и рефакторинг
  07-interface/           Этап 7: мобильный интерфейс
  08-finalization/        Этап 8: завершение и защита
  images/                 Диаграммы, скриншоты, Git-статистика
mobile/                   Мобильное приложение
server/                   Серверная часть
```
