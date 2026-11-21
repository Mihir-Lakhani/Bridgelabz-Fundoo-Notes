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
| 10 | Send reminder emails when due | Planned |
| 11 | Redis caching | Planned |
| 12 | RabbitMQ reminder events | Planned |
| 13 | AOP logging and timing | Planned |
| 14 | CSV note import | Planned |
| 15 | Scheduled Trash cleanup | Planned |
| 16 | CSV note export | Planned |
| 17 | API Gateway | Planned |
| 18 | Final integration checks | Planned |

The two supplied PDFs are study guides, so this UC sequence is the project's implementation plan. Planned items are not claimed as working code.

## Requirements and run

- Java 21 or newer and Maven (or IntelliJ IDEA's Maven tool window).
- No external database is needed for UC01–UC09. H2 stores data in the local `data/` folder.

```bash
git switch develop
mvn spring-boot:run
```

The API starts on port `8080` by default. Run `mvn test` separately to execute the tests.

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

Every note and label operation checks the authenticated owner. UC09 stores reminder times but does not send reminder emails yet.

## Code structure

The Java packages follow the same layer-based layout as the Hospital Management System project: `config`, `controller`, `dto`, `entity`, `exception`, `repository`, and `service`. Controllers accept HTTP requests, services contain the logic, repositories talk to H2, and DTOs control the request and response fields. No Lombok or mapper framework is required.
