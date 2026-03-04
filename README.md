# Comic Book Rental System

A simple console-based CLI application written in Java SE using JDBC and MySQL to manage
comic books, members, and rentals.

## Project Structure

- `schema.sql` – database schema for `comics`, `members`, and `rentals` tables.
- `src/main/java/com/aiegoo/comicrental` – Java source files.
  - `DomainEntities.java` – POJO definitions for `Comic`, `Member`, and `Rental`.
  - `dao` – interfaces defining data-access operations.
  - `Main.java` – entry point with a command loop.

## Branching Strategy
See [BRANCHING_STRATEGY.md](BRANCHING_STRATEGY.md) for guidelines on creating
feature branches for the core system and future SimpleDB threading features.

## Usage
1. Create the `comic_rental` database by running `schema.sql` against MySQL.
2. Configure JDBC connection parameters in the DAO implementations (not yet provided).
3. Run `Main` to start the CLI and execute commands such as `comic-add`,
   `member-list`, `rent`, `return`, etc.

## Next Steps
- Implement DAO JDBC classes using `PreparedStatement` and `try-with-resources`.
- Add service layer and command parsing in `Main`.
- Follow branching strategy when developing new features.

---

## Database Docker Compose Setup

A production‑ready Compose configuration is included for the MySQL back end. Key
components:

* **MySQL 8** with UTF8MB4 charset, strict SQL mode, and healthcheck.
* **phpMyAdmin** on port 8080 (user/pass from `.env`).
* **Optional Adminer** service on port 8081.
* Named volume `comic_db_data` for persistent storage.
* Initialization script `init.sql` builds schema, indexes, foreign keys and seeds
  sample Marvel/DC data.

### Quick start
1. Adjust passwords in `.env` if necessary
data in the workspace or edit `init.sql` for your own samples.
2. Run `make up` to start all services; `make down` to stop and remove them.
3. Browse phpMyAdmin (`http://localhost:8080`) or Adminer (`http://localhost:8081`).

### Example test queries
```sql
SELECT * FROM comics;
SELECT * FROM members;
SELECT * FROM rentals WHERE status='RENTED';
```

All Docker‑related files (`docker-compose.yml`, `.env`, `Makefile`, etc.) reside in
the repository root. See the repository root README for full details.
