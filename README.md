# 🔗 URL Shortener Application

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql)
![REST API](https://img.shields.io/badge/REST-API-FF6C37?style=for-the-badge)

A production-grade URL Shortener built with Spring Boot. Converts long URLs into short, shareable links using custom **Base62 encoding** for collision-resistant short-code generation with sub-100ms redirection.

---

## ✨ Features

- 🔗 Shorten any valid URL instantly
- ⚡ Sub-100ms redirection from short URL to original
- 📊 Click tracking & analytics per short URL
- ⏰ Optional expiry dates for short URLs
- 🔁 Duplicate detection — same URL always returns same short code
- ❌ Handles invalid inputs, expired URLs, and duplicates gracefully
- 🛡️ Input validation with meaningful error messages

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.2, Spring Data JPA |
| Database | MySQL 8.0 with indexed lookups |
| ORM | Hibernate |
| Encoding | Custom Base62 algorithm |
| Build | Maven |

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/shorten` | Shorten a URL |
| `GET` | `/{shortCode}` | Redirect to original URL |
| `GET` | `/api/stats/{shortCode}` | Get click stats for a URL |
| `GET` | `/api/urls` | Get all shortened URLs |
| `DELETE` | `/api/delete/{shortCode}` | Delete a short URL |
| `GET` | `/api/health` | Health check |

---

## 📥 Request & Response Examples

### Shorten a URL
```http
POST /api/shorten
Content-Type: application/json

{
  "originalUrl": "https://www.example.com/very/long/url/that/needs/shortening",
  "expiryDays": 30
}
```

```json
{
  "originalUrl": "https://www.example.com/very/long/url/that/needs/shortening",
  "shortUrl": "http://localhost:8080/aB3xYz",
  "shortCode": "aB3xYz",
  "clickCount": 0,
  "createdAt": "2026-04-24 10:30:00",
  "expiresAt": "2026-05-24 10:30:00"
}
```

### Get Stats
```http
GET /api/stats/aB3xYz
```
```json
{
  "originalUrl": "https://www.example.com/...",
  "shortUrl": "http://localhost:8080/aB3xYz",
  "clickCount": 42,
  "createdAt": "2026-04-24 10:30:00",
  "expiresAt": "2026-05-24 10:30:00",
  "active": true
}
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/imprateekv/url-shortener.git
cd url-shortener

# 2. Create MySQL database
mysql -u root -p
CREATE DATABASE url_shortener_db;

# 3. Update application.properties
# Set your DB password in src/main/resources/application.properties

# 4. Run the application
mvn spring-boot:run
```

App runs at: `http://localhost:8080`

---

## 🗄️ Database Schema

```sql
CREATE TABLE urls (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    short_code  VARCHAR(10) NOT NULL UNIQUE,
    created_at  DATETIME NOT NULL,
    expires_at  DATETIME,
    click_count BIGINT DEFAULT 0,
    active      BOOLEAN DEFAULT TRUE,
    INDEX idx_short_code (short_code)
);
```

---

## 👨‍💻 Author

**Prateek Verma** — Java Full Stack Developer
- GitHub: [@imprateekv](https://github.com/imprateekv)
- Email: vermaprateek@gmail.com
