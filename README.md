# Fundoo Notes

A Spring Boot notes backend built step by step for learning.

## Use cases

- UC01: Register users with a BCrypt password and log in to receive a JWT.
- UC02: Create, read, update, and trash owned notes; pin and archive them.
- UC03: Add labels and reminder timestamps; search and filter notes.

## Run locally

Use Java 21 or newer and run `mvn spring-boot:run` from the `develop` branch.
The H2 database is stored in `data/`. For a shared or deployed copy, set the
`FUNDOO_JWT_SECRET` environment variable to a long random secret.

Register with `POST /api/auth/register` using `name`, `email`, and `password`.
Log in with `POST /api/auth/login` using `email` and `password`.
Send the returned token as `Authorization: Bearer <token>` on note requests.

Notes use `POST/GET /api/notes`, `GET/PUT/DELETE /api/notes/{id}`, and
`PATCH /api/notes/{id}/pin`, `/archive`, or `/trash` with `{"enabled":true}`.
Use `GET /api/notes?search=...&label=...&archived=true` to filter notes.
Reminders are stored timestamps; this learning version does not send notifications.
