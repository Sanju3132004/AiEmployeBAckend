# AI Employee Support Assistant

AI-powered employee support application built with Java 17, Spring Boot, Spring Security,
Spring AI, Hibernate/JPA, MySQL, REST APIs and JWT authentication.

## Features
- AI chatbot for HR policy, leave, attendance and payroll queries (Spring AI)
- JWT-based authentication and role-based authorization (ADMIN, HR, EMPLOYEE)
- Leave application and approval workflow with email notifications
- Attendance check-in / check-out tracking
- Payroll / payslip generation
- MySQL database with Hibernate/JPA, CRUD operations
- Swagger / OpenAPI documentation for API testing

## Tech Stack
- Java 17
- Spring Boot 3.2.5
- Spring Security 6 + JWT (jjwt)
- Spring AI (OpenAI-compatible chat client)
- Hibernate / Spring Data JPA
- MySQL 8
- Swagger / springdoc-openapi
- Lombok
- Maven

## Project Structure
```
src/main/java/com/aiassistant/
  config/         -> Security & Swagger configuration
  security/       -> JWT util, filter, user details service
  entity/         -> JPA entities (Employee, LeaveRequest, Attendance, Payroll, HRPolicy)
  repository/     -> Spring Data JPA repositories
  service/        -> Business logic, including AiChatService (Spring AI)
  controller/     -> REST controllers
  dto/            -> Request/response DTOs
  exception/      -> Global exception handling
src/main/resources/
  application.properties
```

## Setup Instructions

### 1. Prerequisites
- Java 17 (JDK)
- Maven 3.8+
- MySQL 8 running locally (or update the datasource URL)
- An OpenAI-compatible API key (or point Spring AI at any compatible provider)

### 2. Clone / unzip the project
```bash
cd ai-employee-support-assistant
```

### 3. Configure `src/main/resources/application.properties`
Update the following before running:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ai_employee_support?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password

jwt.secret=CHANGE_THIS_TO_A_LONG_RANDOM_SECRET_KEY_MIN_32_CHARS

spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

Set your AI provider key as an environment variable instead of hardcoding it:
```bash
export OPENAI_API_KEY=sk-xxxxxxxx
```

MySQL will auto-create the `ai_employee_support` database on first run
(`createDatabaseIfNotExist=true`), and Hibernate will auto-create the tables
(`spring.jpa.hibernate.ddl-auto=update`).

### 4. Build
```bash
mvn clean install
```

### 5. Run
```bash
mvn spring-boot:run
```
The app starts on **http://localhost:8080**

### 6. API Documentation (Swagger)
Once running, open:
```
http://localhost:8080/swagger-ui.html
```

## API Overview

| Endpoint                              | Method | Auth        | Description                     |
|----------------------------------------|--------|-------------|----------------------------------|
| /api/auth/register                     | POST   | Public      | Register a new employee          |
| /api/auth/login                        | POST   | Public      | Login, returns JWT                |
| /api/employees/me                      | GET    | JWT         | Get current employee profile     |
| /api/leaves                            | POST   | JWT         | Apply for leave                  |
| /api/leaves/me                         | GET    | JWT         | View my leave history            |
| /api/leaves/admin/all                  | GET    | ADMIN/HR    | View all leave requests          |
| /api/leaves/admin/{id}/status          | PATCH  | ADMIN/HR    | Approve/reject a leave request   |
| /api/attendance/check-in               | POST   | JWT         | Check in for the day             |
| /api/attendance/check-out              | POST   | JWT         | Check out for the day            |
| /api/attendance/me                     | GET    | JWT         | View attendance history          |
| /api/payroll/me                        | GET    | JWT         | View my payslips                 |
| /api/payroll/admin/generate            | POST   | ADMIN/HR    | Generate a payslip               |
| /api/chatbot/ask                       | POST   | JWT         | Ask the AI HR assistant a question |

### Example: Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Akila","email":"akila@example.com","password":"Passw0rd!","department":"IT","designation":"Developer"}'
```

### Example: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"akila@example.com","password":"Passw0rd!"}'
```
Response includes a `token` — use it as `Authorization: Bearer <token>` on subsequent requests.

### Example: Ask the AI chatbot
```bash
curl -X POST http://localhost:8080/api/chatbot/ask \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"message":"How many casual leaves am I entitled to per year?"}'
```

## Roles & Account Creation (updated)

Public self-registration has been removed. Accounts are now created like this:

1. **First-time setup — create the owner (ADMIN) account:**
   Set `app.owner-setup-key` in `application.properties` (or `OWNER_SETUP_KEY` env var)
   to a secret of your choice, then call:
   ```
   POST /api/auth/register-owner
   { "fullName": "...", "email": "...", "password": "...", "setupKey": "your-secret" }
   ```
   This only works once — after the first ADMIN account exists, it's disabled automatically.

2. **Owner/PROJECT_MANAGER adds employees:**
   Once logged in as ADMIN (or PROJECT_MANAGER), call the normal register endpoint with a
   valid JWT:
   ```
   POST /api/auth/register
   { "fullName": "...", "email": "...", "password": "...", "department": "...",
     "designation": "...", "role": "EMPLOYEE" }
   ```
   Only an ADMIN can set `role` to `PROJECT_MANAGER` or `HR`; anyone with permission to
   call this endpoint can create plain `EMPLOYEE` accounts.

3. **Roles:** `ADMIN` (owner), `PROJECT_MANAGER`, `HR`, `EMPLOYEE`. Admin-only endpoints
   (`/api/admin/**`, `/api/leaves/admin/**`, `/api/payroll/admin/**`) require
   ADMIN, HR, or PROJECT_MANAGER.

## Forgot / Reset Password

```
POST /api/auth/forgot-password   { "email": "..." }   -> emails an 8-character reset code
POST /api/auth/reset-password    { "token": "...", "newPassword": "..." }
```
The reset code expires after 15 minutes. This uses the same `spring.mail.*` SMTP settings
as leave notifications.

## Multi-language AI Chatbot
The chatbot's system prompt instructs it to reply in whichever language the employee
writes in (English, Tamil, Hindi, or a mix) — no extra configuration needed.

## Notes
- The AI chatbot grounds its answers using `HRPolicy` records stored in MySQL — insert
  your company's actual policy text into that table so the AI has real context to draw from.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for development; use proper
  migrations (Flyway/Liquibase) for production.
- Replace the JWT secret, owner setup key, and mail credentials with real values (ideally
  via environment variables) before deploying.
