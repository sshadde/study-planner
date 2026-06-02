# Руководство администратора

## Системные требования

| Компонент | Требование |
|---|---|
| Java | JDK 17 |
| Backend build | Maven 3.9+ |
| БД | PostgreSQL 16 или Docker |
| Mobile build | Android SDK, Gradle Wrapper |
| Порты | `8080` для backend, `5432` для PostgreSQL |

## Запуск через Docker Compose

```powershell
docker compose up --build
```

Состав контейнеров:

| Контейнер | Назначение |
|---|---|
| `study-planner-postgres` | PostgreSQL |
| `study-planner-server` | Spring Boot backend |

## Остановка Docker Compose

Остановить все контейнеры проекта:

```powershell
docker compose down
```

Проверить, что контейнеры не запущены:

```powershell
docker ps
```

Команда `docker compose down` не удаляет volume с базой данных. Это сделано намеренно, чтобы данные PostgreSQL сохранились между запусками. Полностью удалить контейнеры и данные БД можно командой:

```powershell
docker compose down -v
```

## Переменные окружения backend

| Переменная | Назначение | Значение по умолчанию |
|---|---|---|
| `DB_URL` | JDBC URL PostgreSQL | `jdbc:postgresql://localhost:5432/study_planner` |
| `DB_USERNAME` | Пользователь БД | `study_planner` |
| `DB_PASSWORD` | Пароль БД | `study_planner` |
| `JWT_SECRET` | Секрет подписи JWT | задается в окружении |
| `JWT_TTL_MINUTES` | Время жизни токена | `120` |
| `ADMIN_EMAIL` | Email администратора для bootstrap | пусто |
| `ADMIN_PASSWORD` | Пароль администратора для bootstrap | пусто |

Если `ADMIN_EMAIL` и `ADMIN_PASSWORD` заданы, backend при запуске создает администратора с ролью `ADMIN`, если такого email еще нет. Административные endpoint'ы `/api/admin/**` доступны только этой роли.

## Swagger UI

После запуска backend:

```text
http://localhost:8080/swagger-ui.html
```

## Безопасность

- Пароли сохраняются только в виде BCrypt-хеша.
- Защищенные endpoint'ы требуют JWT Bearer token.
- Endpoint'ы `/api/admin/**` требуют роль `ADMIN`.
- Сервер работает stateless, HTTP-сессии не используются.

## Резервное копирование

При Docker Compose данные PostgreSQL хранятся в volume `postgres-data`. Для резервного копирования можно использовать `pg_dump`.
