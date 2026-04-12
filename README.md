# Fitness Tracker API

# Андрей Котов 450502

## Основные эндпоинты

1. GET /workouts – Получение списка всех тренировок пользователя. Поддержка фильтров, сортировки, пагинации.
2. GET /workouts/{id} – Получение конкретной тренировки по ID.
3. POST /workouts – Добавление новой тренировки.
4. PUT /workouts/{id} – Обновление данных о тренировке.
5. DELETE /workouts/{id} – Удаление тренировки.
6. POST /media – Загрузка фото прогресса (с хранением в файловой системе, облаке или хранить бинарные данные прямо в PostgreSQL (тип BYTEA)).

## Дополнительный функционал

1. JWT-аутентификация
   Авторизация через Bearer Token.
   Refresh Token для продления сессии.

2. Валидация данных
   Использовать javax.validation или Spring Validation.
   Примеры:
   Название тренировки: не пустое, длина ≤ 100 символов.
   Дата: прошедшая дата.
   Длительность: > 0 минут.
   Калории: > 0.

3. Расширенный поиск
   1. Фильтры:
      Тип тренировки (cardio, strength, yoga и т.д.)
      Дата (диапазон дат)
      Длительность (от/до)
   2. Сортировка:
      По дате (ASC/DESC)
      По калориям (ASC/DESC)
   3. Пагинация:
      page, size параметры.
      Ответ с totalElements, totalPages.

## Стек технологий

1. Java 17+ – современная версия языка.
2. Spring Boot – быстрый старт и конфигурация.
3. Spring MVC – реализация REST API.
4. Hibernate + Spring Data JPA – ORM для работы с БД.
5. PostgreSQL – реляционная база данных.
6. JUnit 5 – модульное тестирование.
7. Swagger (OpenAPI) – генерация документации API.
8. Docker – контейнеризация приложения и зависимостей. Позволяет запускать приложение в изолированной среде.

## Запуск

### Через Docker Compose
1. Собрать и поднять сервисы: 
   <code>docker compose up --build -d</code>
2. Фронтенд доступен по адресу:
   <code>http://localhost:3000</code>
3. API доступен:
   - через frontend proxy: <code>http://localhost:3000/api</code>
   - напрямую backend: <code>http://localhost:8080</code>
4. Проверить логи backend: 
   <code>docker compose logs -f backend</code>
5. Остановить и удалить контейнеры: 
   <code>docker compose down -v</code>

### Локально (без контейнера)
1. Поднять PostgreSQL (можно через compose только для БД): `
   <code>docker compose up -d db</code>
2. Собрать и запустить приложение:
   <code>./mvnw spring-boot:run</code>
Windows:
   <code>mvnw.cmd spring-boot:run</code>
3. По умолчанию приложение слушает порт `8080`, 
БД — <code>jdbc:postgresql://localhost:5432/fitness_tracker</code>
