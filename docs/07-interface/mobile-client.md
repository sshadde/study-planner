# Описание мобильного клиента

## Структура проекта

```text
mobile/
  settings.gradle.kts
  build.gradle.kts
  app/
    build.gradle.kts
    src/main/
      AndroidManifest.xml
      java/ru/studyplanner/mobile/
        di/
        local/
        model/
        remote/
        repository/
        state/
        sync/
        ui/
```

## Архитектурные модули

| Модуль | Назначение |
|---|---|
| `ui` | Compose-экраны, тема, навигация |
| `state` | ViewModel и StateFlow-состояния |
| `repository` | Координация Retrofit, Room и offline fallback |
| `remote` | Retrofit API и DTO |
| `local` | Room database, DAO и локальные Entity |
| `sync` | Синхронизация pending-операций |
| `di` | Простая сборка зависимостей приложения |

## Обработка ошибок

Мобильный клиент обрабатывает типовые сетевые и HTTP-ошибки:

- недоступность backend;
- истечение или отсутствие JWT-токена;
- ошибки валидации `400 Bad Request`;
- конфликты данных `409 Conflict`;
- серверные ошибки `5xx`.

Если backend возвращает `ErrorResponse` с деталями валидации, приложение преобразует технические сообщения в пользовательские подсказки, например `semester: must be greater than or equal to 1` отображается как `Семестр: должен быть от 1 до 10`.

## Связь с PCMEF

Мобильное приложение реализует Presentation-слой адаптированной PCMEF-архитектуры.

```text
Compose screens -> ViewModel -> Repository -> Retrofit API -> Backend Control
                                Repository -> Room cache
```

Серверная бизнес-логика остается на backend в слоях Control, Mediator, Entity и Foundation.

## Технологии

| Задача | Технология |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Навигация | Navigation Compose |
| Состояние | ViewModel + StateFlow |
| REST API | Retrofit + OkHttp |
| JSON | Moshi |
| Локальное хранение | Room |
| Токен | Android Keystore + encrypted DataStore value |

## Backend URL

Android Emulator в обычной debug-сборке обращается к Docker-backend по адресу:

```text
http://10.0.2.2:8080/
```

Это стандартный адрес для доступа из эмулятора Android к `localhost` хостовой машины при запуске backend на `8080` через Docker Compose.

## Хранение токена

JWT не сохраняется в открытом виде. `TokenStore` шифрует токен через Android Keystore и AES/GCM, а в DataStore хранится только зашифрованная строка.
