# Fundoo Notes

A Spring Boot backend for personal notes, rebuilt one small use case at a time. The running application is on [develop](https://github.com/Mihir-Lakhani/Bridgelabz-Fundoo-Notes/tree/develop). `main` contains this README only.

## Current progress

Each completed UC was built on its own `feature/uc-*` branch from `develop` and merged with `--no-ff`. The [first draft](https://github.com/Mihir-Lakhani/Bridgelabz-Fundoo-Notes/tree/archive/first-fundoo-draft) is archived for reference; the new branches are the rebuilt version.

| UC | Feature | Status |
| --- | --- | --- |
| 01 | User registration with validation and BCrypt password hashing | Complete |
| 02 | Login and signed JWT access token | Complete |
| 03 | Read and update the authenticated user's profile | Complete |
| 04 | Email verification and one-time password reset tokens | Complete for local use; SMTP optional |
| 05 | Create, read, update, and delete owned notes | Complete |
| 06 | Pin, archive, trash, restore, and permanently delete notes | Complete |
| 07 | Search active notes by title or description | Complete |
| 08 | Create/delete labels, attach them to notes, and filter notes by label | Complete |
| 09 | Set, view, and remove a note reminder | Complete |
| 10 | Send reminder emails when due | Complete; SMTP optional |
| 11 | Redis caching for labels | Complete; Redis profile optional |
| 12 | RabbitMQ reminder events | Complete; RabbitMQ profile optional |
| 13 | AOP activity logging and execution timing | Complete |
| 14 | Spring Batch CSV note import | Complete |
| 15 | Scheduled Trash cleanup after 30 days | Complete |
| 16 | Spring Batch CSV export | Complete |
| 17 | Separate Spring Cloud API Gateway | Complete |
| 18 | Integration checks and project finalization | Complete |

The two supplied PDFs are study guides, so this UC sequence is the project's implementation plan. Redis, RabbitMQ, and SMTP require their respective services to be configured for those integrations to work. Their core logic is tested, but no live Redis, RabbitMQ, or SMTP service is part of the automated test suite.

## Requirements and run

- Java 21 or newer and Maven (or IntelliJ IDEA's Maven tool window).
- No external database is needed. H2 stores data in the local `data/` folder, which Git ignores.

```bash
git switch develop
mvn spring-boot:run
```

The API starts on port `8080` by default. Run `mvn test` separately to execute the tests.

To use the separate gateway, start the backend first, then run the gateway in another terminal:

```bash
cd fundoo-api-gateway
mvn spring-boot:run
```

Call the same `/api/**` paths through port `8081`. The gateway forwards requests and bearer tokens to the backend; the backend still checks authentication and ownership. Set `FUNDOO_BACKEND_URL` to change the target; by default the gateway targets `http://localhost:8080`. The gateway has its own `pom.xml`, so run its tests with `mvn -f fundoo-api-gateway/pom.xml test` from the repository root.

For a stable JWT signing key across restarts, set `FUNDOO_JWT_SECRET` to a random value of at least 32 characters. If omitted, the app creates a temporary random key at startup and existing tokens stop working after a restart.

## Authentication and account APIs

1. `POST /api/auth/register` with `{"name":"Mihir","email":"mihir@example.com","password":"password123"}`. The response contains the new user ID.
2. `POST /api/auth/login` with `{"email":"mihir@example.com","password":"password123"}`. The response contains a JWT.
3. Send `Authorization: Bearer <token>` for all user, label, and note endpoints.
4. `GET /api/users/me` reads the profile; `PUT /api/users/me` with `{"name":"New name"}` updates the name.

Account recovery endpoints:

- `POST /api/auth/verify` with `{"token":"..."}` marks the email verified.
- `POST /api/auth/forgot-password` with `{"email":"mihir@example.com"}` requests a reset token. It returns the same response for known and unknown emails.
- `POST /api/auth/reset-password` with `{"token":"...","password":"newpassword123"}` changes the password.

Verification and reset tokens expire after 15 minutes and can be used once. Email verification is recorded in the profile but is **not currently required for login**. Without SMTP settings, development emails and tokens are written to the application log. To send real mail, configure Spring Mail (`spring.mail.host`, port, username, and password) and `fundoo.mail.from`. Do not use the log-mail fallback for a deployed application.

## Notes and labels APIs

- `POST /api/notes` with `{"title":"Plan","description":"Call team"}` creates a note.
- `GET /api/notes` lists active notes. Add `?search=team` or `?label=Work` to filter.
- `GET /api/notes/{id}` and `PUT /api/notes/{id}` read and update an owned note.
- `DELETE /api/notes/{id}` moves a note to Trash. `GET /api/notes/trash` lists trashed notes.
- `PATCH /api/notes/{id}/pin`, `/archive`, or `/trash` with `{"enabled":true}` changes a state. Use `false` on `/trash` to restore.
- `DELETE /api/notes/{id}/permanent` permanently deletes a note already in Trash.
- `POST /api/labels` with `{"name":"Work"}` creates a label; `GET /api/labels` lists labels; `DELETE /api/labels/{id}` removes one.
- `PUT /api/notes/{id}/labels/{labelId}` attaches a label; `DELETE` on the same path detaches it.
- `PUT /api/notes/{id}/reminder` with `{"reminderAt":"2030-01-01T09:00:00Z"}` sets a reminder; `DELETE` on the same path removes it.

Every note and label operation checks the authenticated owner.

## Reminders, CSV, and optional services

The default app checks due reminders every minute and sends an email. Without SMTP configuration, it writes development emails to the log. Set `spring.mail.host`, port, username, password, and `fundoo.mail.from` to send real email. A reminder is marked sent after processing, so it is not repeatedly sent. Email delivery is not verified by the app.

`POST /api/notes/import` accepts a multipart form field named `file`, with a CSV header `title,description`. `GET /api/notes/export` downloads the signed-in user's active notes as CSV. Both operations run simple Spring Batch jobs. The import accepts quoted commas and newlines; the export preserves them.

Trash cleanup runs once per day and permanently removes notes that have been in Trash for more than 30 days.

Redis and RabbitMQ are optional local profiles:

- `mvn spring-boot:run -Dspring-boot.run.profiles=redis` caches each user's label list in Redis at `localhost:6379` and clears that cache when labels change. Start Redis before enabling this profile.
- `mvn spring-boot:run -Dspring-boot.run.profiles=rabbit` publishes due reminder IDs to RabbitMQ at `localhost:5672`; a consumer processes the email. Start RabbitMQ before enabling this profile. Without it, the default reminder scheduler sends directly.

The AOP aspect logs note-service activity and execution time. It does not change the note API.

## Code structure

The Java packages follow the same layer-based layout as the Hospital Management System project: `config`, `controller`, `dto`, `entity`, `exception`, `repository`, and `service`, with small `batch` and `aop` packages for those features. Controllers accept HTTP requests, services contain the logic, repositories talk to H2, and DTOs control the request and response fields. `fundoo-api-gateway/` is a separate application. No Lombok or mapper framework is required.
