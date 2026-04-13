# Fitness Tracker API

# [SonarCloud](https://sonarcloud.io/project/overview?id=k0lm1r_fitness_tracker)

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

## CI/CD (GitHub Actions)

Добавлены workflow:

1. `CI` - `.github/workflows/ci.yml`
   Выполняется на каждый `push` и `pull_request`:
   - установка Java 25
   - `./mvnw verify` (тесты, checkstyle, jacoco rules)
   - сохранение test/jacoco артефактов

2. `CD` - `.github/workflows/cd.yml`
   Выполняется на `push` в ветку `main` и вручную (`workflow_dispatch`):
   - сборка Docker-образов backend/frontend
   - публикация в `ghcr.io`
   - опциональный деплой по SSH на сервер

### Что нужно настроить в GitHub Secrets

Для публикации образов в GHCR дополнительных секретов не нужно (используется `GITHUB_TOKEN`).

Для деплоя на сервер нужны secrets:

- `DEPLOY_HOST` - адрес сервера
- `DEPLOY_PORT` - SSH порт (обычно `22`)
- `DEPLOY_USER` - SSH пользователь
- `DEPLOY_SSH_KEY` - приватный SSH ключ
- `DEPLOY_PATH` - путь на сервере, где лежит `docker-compose.prod.yml`
- `GHCR_USERNAME` - пользователь для чтения GHCR на сервере
- `GHCR_READ_TOKEN` - токен с правом `read:packages`

Если secrets деплоя не заданы, job `deploy` автоматически пропускается.

### Деплой на сервер

На сервере в `DEPLOY_PATH` должны быть:

- `docker-compose.prod.yml`
- `.env` (POSTGRES/MINIO переменные)
- `init.sql` (если нужен первичный init БД)

Workflow выставляет `IMAGE_NAMESPACE` автоматически из owner репозитория и запускает:

- `docker compose -f docker-compose.prod.yml pull`
- `docker compose -f docker-compose.prod.yml up -d --remove-orphans`

### Переменные в `.env`

Все переменные из `docker-compose.yml` вынесены в `.env`:

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `SPRING_DATASOURCE_URL`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `MINIO_URL`
- `MINIO_BUCKET`
- `MINIO_ROOT_USER`
- `MINIO_ROOT_PASSWORD`
- `MINIO_ACCESS_KEY`
- `MINIO_SECRET_KEY`
- `JWT_SECRET`
- `JWT_ACCESS_TOKEN_EXPIRATION`
- `JWT_REFRESH_TOKEN_EXPIRATION`
- `BACKEND_HOST`
- `BACKEND_PORT`

### Где взять значения для переменных деплоя (не из `.env`)

Эти значения задаются в `GitHub -> Settings -> Secrets and variables -> Actions -> Repository secrets`:

- `DEPLOY_HOST`: IP/домен сервера (из панели хостинга/VPS или команды `hostname -I` на сервере)
- `DEPLOY_USER`: SSH-пользователь на сервере (`ubuntu`, `root`, `deploy` и т.п.)
- `DEPLOY_SSH_KEY`: приватный ключ от пары, чья публичная часть добавлена на сервер в `~/.ssh/authorized_keys`
- `DEPLOY_PATH`: абсолютный путь на сервере с compose-файлами, например `/opt/fitness-tracker`
- `GHCR_USERNAME`: GitHub username или имя организации-владельца образов в `ghcr.io`
- `GHCR_READ_TOKEN`: GitHub Personal Access Token с правом `read:packages`
