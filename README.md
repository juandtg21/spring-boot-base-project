# 🧰 Spring Boot Backend Template

A production-ready backend **starter template** built with Java 21 and Spring Boot 3.4.5.

Includes everything most modern web or mobile apps need:
- User authentication (JWT + OAuth2)
- Local login & registration
- REST API with Swagger docs
- MySQL for production / H2 for testing
- Behavior-driven tests with Cucumber

---

## 🛠️ Tech Stack

- Java **21**
- Spring Boot **3.4.5**
- Spring Security
- OAuth2 (Google, Facebook)
- JWT (JSON Web Token)
- JPA / Hibernate
- MySQL / H2
- Swagger / OpenAPI 3
- Cucumber (JUnit)

---

## 🔧 Requirements

- Java 21
- Maven 3.9+
- MySQL 8.x (for production)

---

## 🚀 Features

- ✅ User registration and local login
- 🔐 OAuth2 login with Google and Facebook
- 🔑 JWT token issuance and validation
- ⚙️ Role-based access control (RBAC) *(optional enhancement)*
- 🧪 Behavior-driven development with Cucumber
- 🧰 REST API with Swagger documentation
- 🗄️ MySQL for production, H2 for dev/test
---

## 📄 Swagger API Documentation

After starting the application, you can access the Swagger UI and OpenAPI docs at:

- Swagger UI (interactive API docs):  
  `http://localhost:8080/swagger-ui/index.html`

- OpenAPI JSON spec:  
  `http://localhost:8080/v3/api-docs`

---

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/spring-boot-backend-template.git
cd spring-boot-backend-template
```

### 2. Configure Environment Variables

Create a `.env` file in the root directory (same location as `docker-compose.yml`):

```bash
# On Linux/macOS
touch .env
```
## Update the .env file with the following variable
### Database Configuration

```bash
MYSQL_ROOT_PASSWORD=your-root-password
MYSQL_DATABASE=your-database-name
MYSQL_USER=your-username
MYSQL_PASSWORD=your-password
```

### OAuth2 credentials
```bash
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

FACEBOOK_CLIENT_ID=your-facebook-client-id
FACEBOOK_CLIENT_SECRET=your-facebook-client-secret
```

### App-specific secrets for token generation and validation.

```bash
APP_TOKEN_SECRET=your-token-secret
```

## 🧙 Automatic Data Loader

This project includes a `SetupDataLoader` that runs at application startup to pre-populate the database with:

- Default roles:
    - `ROLE_USER`
    - `ROLE_ADMIN`
    - `ROLE_MODERATOR`
- Default users:
    - `admin@test.com` — roles: admin, moderator, user
    - `johndoe@test.com`
    - `janedoe@test.com`
    - `jimdoe@test.com`
    - `joecitizen@test.com`

> All users have the default password: **`admin123`**

---

## 📦 Postman Collection

You can import the provided Postman collection to quickly test and explore the API endpoints.

1. Download the collection JSON from this repository or copy the following JSON content into a file named `BaseTemplate.postman_collection.json`.

2. Open Postman, click **Import**, then select the JSON file.

3. The collection will be imported with predefined requests for user management, authentication, and profile.

4. Use the **login** request to get your JWT token, which will be saved as the `access_token` environment variable in Postman.

5. Later requests requiring authorization will use this `access_token` automatically.

---



