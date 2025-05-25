
<h1 align="center">🏢 UdvCorpSocialBackend</h1>

<p align="center">
  <img src="https://img.shields.io/badge/build-passing-brightgreen" alt="Build Status"/>
  <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="License"/>
  <img src="https://img.shields.io/badge/Java-21-blue" alt="Java Version"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-success" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/docker--compose-enabled-blue" alt="Docker Compose"/>
</p>

<p align="center">
  <strong>Backend корпоративного портала: социальная сеть</strong>
</p>

---

## 📌 Основной функционал

<ul>
  <li>🧑‍💼 Профили сотрудников и организационная структура</li>
  <li>🏢 Подразделения, иерархия, привязка к проектам</li>
  <li>📝 Посты и комментарии, лайки, медиа</li>
  <li>📁 Интеграция с MinIO для хранения файлов</li>
  <li>🔐 JWT-аутентификация и авторизация</li>
  <li>📊 Управление навыками, компетенциями, участием в проектах</li>
  <li>⚙️ REST API для frontend-интеграции</li>
</ul>

---

## 🛠️ Архитектура

```text
+------------------+       +------------------+       +-----------------+
|  Spring Boot API | <---> |   PostgreSQL DB  | <---> |     pgAdmin     |
+------------------+       +------------------+       +-----------------+
        |                          ↑
        ↓                          |
+------------------+       +------------------+
|   MinIO (S3 API) | <---> |     Frontend     |
+------------------+       +------------------+
````

* 📘 **Spring Boot** — основная логика API
* 🐘 **PostgreSQL** — база данных
* 📂 **MinIO** — файловое хранилище
* 🧑‍💻 **pgAdmin** — управление БД через UI
* 🐳 **Docker Compose** — запуск всех сервисов в контейнерах

---

## 🚀 Быстрый старт

```bash
git clone https://github.com/WebStormUdv/UdvCorpSocialBackend.git
cd UdvCorpSocialBackend

cp .env.example .env
# Отредактируйте .env с вашими настройками

docker-compose up -d
```

Чтобы остановить:

```bash
docker-compose down
```

---

## ⚙️ Переменные окружения

Указываются в `.env`:

```env
UDV_DB_URL=jdbc:postgresql://postgres:5432/udv_corp_social
UDV_DB_USER=postgres
UDV_DB_PASSWORD=PostgresPassword

JWT_SECRET=SuperSecretJwtKey
JWT_EXPIRATION=86400000

```

---

## 🌐 API документация

Документация Swagger автоматически разворачивается вместе с API:

<p>
  🔗 <a href="http://localhost:8080/swagger-ui/index.html" target="_blank">
    http://localhost:8080/swagger-ui/index.html
  </a>
</p>

* Используется JWT Bearer Token (введите access\_token после логина)
* Полное описание доступных эндпоинтов и моделей

---

## 🔌 Сервисы и доступ

<table>
  <thead>
    <tr>
      <th>Сервис</th>
      <th>URL</th>
      <th>Доступ / Учётные данные</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>API Server</td>
      <td><a href="http://localhost:8080/">http://localhost:8080</a></td>
      <td>—</td>
    </tr>
    <tr>
      <td>Swagger UI</td>
      <td><a href="http://localhost:8080/swagger-ui/index.html">Swagger</a></td>
      <td>—</td>
    </tr>
    <tr>
      <td>pgAdmin</td>
      <td><a href="http://localhost:5050/">http://localhost:5050</a></td>
      <td><code>admin@udvcorp.com / admin123</code></td>
    </tr>
    <tr>
      <td>MinIO</td>
      <td><a href="http://localhost:9000/">http://localhost:9000</a></td>
      <td><code>minioadmin / minioadmin123</code></td>
    </tr>
    <tr>
      <td>MinIO Console</td>
      <td><a href="http://localhost:9001/">http://localhost:9001</a></td>
      <td>Для администрирования бакетов</td>
    </tr>
  </tbody>
</table>

---

## 🧼 Очистка среды и сброс данных

Для полного сброса состояния, включая тома и кэш Docker:

```bash
docker-compose down -v
```




