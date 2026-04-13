# Java_TicTacToe (Крестики-Нолики)

Резюме: проект выполнен в рамках учебного задания по основам web-разработки на языке **Java**, добавление базы данных и авторизации пользователей с использованием фреймворка **Spring**. 
 
## 1. Общая информация
**Проект** - "Крестики-Нолики". \
**Java 21** - язык программирования. \
**Spring Boot 3.2.12** - фреймворк для веб-приложения. \
**Spring Data JPA** - работа с базой данных. \
**Spring Security** - безопасность и авторизация. \
**PostgreSQL 15 (Docker)** - хранение базы данных в Docker-контейнере. \
**Basic auth** - авторизация по паре логина и пароля. \
**Gradle (Kotlin DSL)** - система сборки.

### 1.1. Описание приложения
 - Создание игр.
 - Регистрация, авторизация и идентификация пользователей.
 - Поддерживает множество игр одновременно.
 - Поддержка игры с компьютером (алгоритм Минимакс).
 - Поддержка игры с другим игроком.
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
Перейти в раздел Releases и скачать `TicTacToe-1.1.0.jar`.

### 2.3. Настроить подключение к базе данных
Создай файл `application.properties` рядом с JAR-файлом:
```
spring.application.name=game-tic-tac-toe

spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
Если нужно изменить порт, хост или пароль - отредактируй данный файл.

### 2.4. Запустить приложение
Запустить приложение двойным щелчком или через терминал выполни команду:
```
bash

java -jar TicTacToe-1.1.0.jar`
```
Приложение запустится на `http://localhost:8080`

## 3. API
Почти все эндпоинты (кроме регистрации и логина) требует Basic Auth. \
Логин и пароль кодируются в **Base64**: \
`login:password` → `base64(login:password)`

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
#### 3.1.2. Авторизация
```
http

POST /user/login
Authorization: Basic ...
```
#### 3.1.3. Получение информации о пользователе
```
http

GET /user/{userId}
Authorization: Basic ...
```

### 3.2. Игры 
#### 3.2.1. Создать игру с компьютером
```
http

POST /game/create/comp
Authorization: Basic ...
```

#### 3.2.2. Создать игру с другим игроком (ожидание второго игрока)
```
http

POST /game/create/friend
Authorization: Basic ...
```
#### 3.2.3. Получить список доступных игр (ожидающих второго игрока)
```
http

GET /game/available
Authorization: Basic ...
```
#### 3.2.4. Присоединиться к игре (в качестве игрока "0")
```
http

POST /game/{gameId}/join
Authorization: Basic ...
```
#### 3.2.5. Получить информацию об игре
```
http

GET /game/{gameId}
Authorization: Basic ...
```
#### 3.2.6. Сделать ход
```
http

POST /game/{gameId}
Authorization: Basic ...

Content-Type: application/json

{
"row": 1,
"col": 1
}
```
## 4. Версии
`v1.0` - in-memory хранилище, игра только с компьютером (без авторизации). \
`v1.1.0` - PostgreSQL, авторизация, добавляется вариант играть с другом.