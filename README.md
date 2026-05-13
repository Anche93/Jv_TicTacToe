# Java_TicTacToe (Крестики-Нолики)

Резюме: проект выполнен в рамках учебного задания по основам web-разработки на языке **Java**, добавление базы данных,
JWT-авторизации, истории игр и таблицы лидеров с использованием фреймворка **Spring**.

## 1. Общая информация

Проект - **"Крестики-Нолики"**

| Технология             | Описание                     |
|------------------------|------------------------------|
| Java 21                | язык программирования        |
| Spring Boot 3.2.12     | фреймворк для веб-приложения |
| Spring Data JPA        | работа с базой данных        |
| Spring Security        | безопасность и авторизация   |
| JWT (JSON Web Token)   | токены доступа и обновления  |
| PostgreSQL 15 (Docker) | хранение базы данных         |
| Gradle (Kotlin DSL)    | система сборки               |
| JaCoCo                 | анализ покрытия кода тестами |

### 1.1. Описание приложения

- Создание игр с компьютером (алгоритм Минимакс) или с другим игроком.
- Регистрация, JWT-авторизация и идентификация пользователей.
- Поддержка нескольких игр одновременно.
- История завершенных игр для каждого пользователя.
- Таблица лидеров (процент побед).
- Хранение данных в PostgreSQL.

## 2. Запуск проекта

### 2.1. Запустить PostgreSQL в Docker

```
bash

docker run --name ticTac-postgres \
-e POSTGRES_PASSWORD=mysecretpassword \
-p 5432:5432 \
-d postgres:15
```

### 2.2. Скачать релиз

Перейти в раздел Releases и скачать `TicTacToe-1.2.0.jar`.

### 2.3. Настроить подключение к базе данных

Создай файл `application.properties` рядом с JAR-файлом:

```
spring.application.name=game-tic-tac-toe

spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=секретный_ключ_для_jwt_который_задаешь_самостоятельно
```

`jwt.secret` - секретный ключ для подписи токенов. Необходимо заменить на свой надежный ключ. \
Если нужно изменить порт, хост или пароль - отредактируй данный файл.

### 2.4. Запустить приложение

Запустить приложение двойным щелчком или через терминал выполнить команду:

```
bash

java -jar TicTacToe-1.2.0.jar`
```

Приложение запустится на `http://localhost:8080`

## 3. API

**Авторизация**: JWT токен (кроме эндпоинтов регистрации и логина). \
После успешного логина получаешь `accessToken` и `refreshToken`. \
Access Token передается в заголовке:

```
Authorization: Bearer <accessToken>
```

### 3.1. Пользователи

#### 3.1.1. Регистрация

```
http

POST /user/reg
Content-Type: application/json

{
    "login": "test1",
    "password": "t1"
}
```

#### 3.1.2. Логин (получение токенов)

```
http

POST /auth/login
Content-Type: application/json

{
    "login": "test1",
    "password": "t1"
}
```

Ответ:

```
json
{
    "type": "Bearer",
    "accessToken": "eyJhGc...",
    "refreshToken": "eyJhGc..."
}
```

#### 3.1.3. Обновление accessToken

```
http

POST /auth/refresh-access
Content-Type: application/json

{
    "refreshToken": "<refreshToken>"
}
```

#### 3.1.4. Обновление refreshToken

```
http

POST /auth/refresh-refresh
Content-Type: application/json

{
    "refreshToken": "<refreshToken>"
}
```

#### 3.1.5. Получение информации о пользователе по токену

```
http

GET /user/me
Authorization: Bearer <accessToken>
```

### 3.2. Игры

#### 3.2.1. Создать игру с компьютером

```
http

POST /game/create/comp
Authorization: Bearer <accessToken>
```

#### 3.2.2. Создать игру с другим игроком (ожидание второго игрока)

```
http

POST /game/create/friend
Authorization: Bearer <accessToken>
```

#### 3.2.3. Получить список доступных игр (ожидающих второго игрока)

```
http

GET /game/available
Authorization: Bearer <accessToken>
```

#### 3.2.4. Присоединиться к игре (в качестве игрока "0")

```
http

POST /game/{gameId}/join
Authorization: Bearer <accessToken>
```

#### 3.2.5. Получить информацию об игре

```
http

GET /game/{gameId}
Authorization: Bearer <accessToken>
```

#### 3.2.6. Сделать ход

```
http

POST /game/{gameId}
Authorization: Bearer <accessToken>

Content-Type: application/json

{
    "row": 1,
    "col": 1
}
```

#### 3.2.7. Получить историю завершенных игр пользователя

```
http

GET /game/history
Authorization: Bearer <accessToken>
```

Ответ: список игр со статусами `PLAYER_X_WIN`, `PLAYER_O_WIN`, `ZERO_WIN`.

### 3.3. Таблица лидеров

#### 3.3.1. Получить топ N игроков по проценту побед

```
http

GET /user/leader?limit=10
Authorization: Bearer <accessToken>
```

Ответ:

```
json
{
    "userId": "uuid",
    "userLogin": "test1",
    "winPercent": "75.5"
}
```

## 4. Структура проекта

```
src/main/org/example/
|- domain/          # бизнес-логика (чистый Java)
|- datasource/      # работа с БД (JPA, репозитории)
|- web/             # контроллеры, DTO, мапперы
|- security/        # JWT, фильры, аутентификация
|- di/              # Spring конфигарации
|- Main.java        # точка входа
```

## 5. Тестирование и покрытие кода

Проект покрыт unit-тестами и интеграционными тестами:

- `GameServiceImpTest`, `UserServiceImpTest` - бизнес-логика
- `WinCheckerTest` - проверка победы
- `AuthFilterIntegrationTest`, `AuthServiceTest`, `JwtProviderTest`, `JwtUtilTest` - безопасность

### 5.1. Запуск тестов

```
bash
./gradlew test
```

### 5.2. Отчет о покрытии кода (JaCoCo)

В проекте настроен плагин **JaCoCo** для анализа покрытия кода тестами.

```
bash
./gradlew jacocoTestReport
```

Отчет: `build/reports/jacoco/test/html/index.html` \
Открыть `index.html` в браузере и можно посмотреть покрытие по классам и методам.

## 6. Версии

`v1.0` - in-memory хранилище, игра только с компьютером (без авторизации). \
`v1.1.0` - PostgreSQL, Basic Auth, игра с другом. \
`v1.2.0` - JWT авторизация, история игр, таблица лидеров, покрытие тестами.
