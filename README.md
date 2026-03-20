# Comic Book Rental System

A simple console-based CLI application written in Java SE using JDBC and MySQL to manage
comic books, members, and rentals.

## Project Structure

- `schema.sql` – database schema for `comics`, `members`, and `rentals` tables.
- `src/main/java/com/aiegoo/comicrental` – Java source files.
  - `DomainEntities.java` – POJO definitions for `Comic`, `Member`, and `Rental`.
  - `dao` – interfaces defining data-access operations.
  - `Main.java` – entry point with a command loop.

## System Architecture

The system is designed as a layered Java CLI application with clear separation of concerns:

- **CLI / TUI Layer** (`Main.java`, `tui` mode)
  - Reads console commands and delegates to service layer.
  - Supports commands such as `comic-add`, `member-add`, `rent`, `return`, `rental-list`.
- **Service Layer** (`RentalService`, `MemberService`, etc.)
  - Contains business rules (e.g., block renting when a comic is already rented).
  - Handles validation and transaction flow.
- **DAO Layer** (`dao/*Dao.java`, `dao/*DaoImpl.java`)
  - Implements JDBC interactions using `PreparedStatement` and resource management.
  - CRUD operations on `comics`, `members`, `rentals`.
- **Database Layer** (MySQL)
  - Schema managed via `schema.sql`, and Docker initialization in `init.sql`.
  - Runs in `docker-compose` as service `mysql` with optional phpMyAdmin/adminer.

Deployment architecture for local development (all in same submodule):

- `comic-rental-tui` repository root contains the app code and Docker orchestration.
- `aibe5-comic` submodule is the reusable project component with code and docs.
- Data persistence : `mysql` container + `comic_db_data` volume.
- Optional dashboard / logs integration using `tmux` (only for developer convenience).

## Branching Strategy
See [BRANCHING_STRATEGY.md](BRANCHING_STRATEGY.md) for guidelines on creating
feature branches for the core system and future SimpleDB threading features.

## Usage
1. Make sure you have **JDK 11 or newer** installed and `java`/`javac` are on your `PATH`.
2. Start the database. You can either use the supplied Docker Compose stack or
   create the schema manually:
   ```sh
   # start MySQL + phpMyAdmin (see `Makefile`/`.env` for credentials)
   make up
   # or, if you already have a MySQL server running:
   mysql -u root -p < schema.sql
   ```
3. Download the MySQL JDBC driver (``mysql-connector-java-8.0.xx.jar``) from
   https://dev.mysql.com/downloads/connector/j/ and place the jar in the project
   root (or any folder you like).

> **MySQL client vs application CLI:** you will see a prompt like `mysql>` when
> you connect to the database itself.  That is **not** where you type
> `comic-list`, `rent`, etc.; those commands are understood only by the Java
> application which prints a `>` prompt when running.  Typing application
> commands at the MySQL prompt will simply hang until you cancel (`Ctrl-C`).
4. Compile the source files:
   ```sh
   javac -d out \
       src/main/java/com/aiegoo/comicrental/*.java \
       src/main/java/com/aiegoo/comicrental/dao/*.java \
       src/main/java/com/aiegoo/comicrental/util/*.java
   ```
5. Run the CLI, making sure to include the JDBC jar on the classpath:
   ```sh
   java -cp out:mysql-connector-java-8.0.xx.jar \
       com.aiegoo.comicrental.Main
   # or, if you downloaded the full connector distribution and are
   # using the bundled jar under the `mysql-connector-j-*` folder:
   # java -cp out:mysql-connector-j-9.6.0/mysql-connector-j-9.6.0.jar \
   #     com.aiegoo.comicrental.Main
   ```
   (adjust the jar file name/path if you saved it elsewhere.)

   **TUI mode:** the same command accepts an optional `dashboard` argument
   that launches a simple text‑based dashboard. You can also use the helper
   make target from the project root:
   ```sh
   make dashboard
   ```

   > In tmux you may want to run the CLI in one pane and follow the database
   > logs in another; the section below shows a suggested layout.
6. At the `>` prompt type commands like `comic-add`, `comic-list`,
   `member-add`, `rent 1 1`, `rental-list`, etc. Use `exit` to quit.

> **Tip:** the DAOs use the URL
> ``jdbc:mysql://localhost:3306/comic_rental?useSSL=false&allowPublicKeyRetrieval=true``
> with user `root` and the password defined in `.env` (defaults to `example`).
> Edit the strings in `src/main/java/com/aiegoo/comicrental/dao/*DaoImpl.java`
> if your MySQL setup is different.

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

> **Note:** if you previously mounted or imported a large SQL dump (e.g. the
> `2026-03-01.sql` file with hundreds of thousands of covers), it was executed
> only once during container creation. Subsequent `docker-compose up` invocations
> only use `init.sql` from the repository; the original dump is **not retained**
> by Docker. To persist or re‑apply that dataset, keep a copy of the dump in
> the repo or mount it explicitly (see example below).

### Quick start
1. Adjust passwords in `.env` if necessary
data in the workspace or edit `init.sql` for your own samples.
2. Run `make up` to start all services; `make down` to stop and remove them.

   If you have a separate dump file such as `2026-03-01.sql` that you want to
   load into the database, you can either replace `init.sql` with it or add a
   second bind mount in `docker-compose.yml` (see example below), or import it
   manually after the container has started:
   ```sh
   # mount alongside the existing init script
   # (edit docker-compose.yml accordingly and then restart the stack)
   services:
     mysql:
       volumes:
         - db_data:/var/lib/mysql
         - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
         - ./2026-03-01.sql:/docker-entrypoint-initdb.d/2026-03-01.sql:ro

   # – or – import into a running container
   docker exec -i comic-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" \
       comic_rental < ./2026-03-01.sql
   ```
3. Browse phpMyAdmin (`http://localhost:8080`) or Adminer (`http://localhost:8081`).

   **Verifying inserted data:**
   When you add members/comics via the Java CLI or dashboard you can
   inspect the same database from within the container.  For example:
   ```sh
   docker-compose exec mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" comic_rental \
       -e "SELECT id,name,phone_number FROM members;"
   docker-compose exec mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" comic_rental \
       -e "SELECT id,title,volume_count,author,is_rented FROM comics;"
   ```
   (the phone column may be called `phone` or `phone_number` depending on
   which schema you’re using; the application handles both.)

If you prefer a shell inside the container you can drop into bash and invoke the
`mysql` client directly.  A `make exec` rule has been added to the Makefile for
this purpose:
```sh
# start an interactive shell in the running mysql service
make exec   # runs "docker-compose exec mysql bash"

# once inside the container, connect to the database
mysql -uroot -p"$MYSQL_ROOT_PASSWORD" comic_rental

# or simply inspect the raw dump file if needed
ls -lh /docker-entrypoint-initdb.d

# (optional) Use tmux for live logs and dual-pane interaction
If you're building a TUI and want to view the MySQL log output alongside a shell, start a tmux session with two panes:
```sh
# split horizontally, left pane for bash, right pane for logs
tmux new-session -s comicview \;
  send-keys 'docker-compose exec mysql bash' C-m \;
  split-window -h \;
  send-keys 'docker-compose logs -f mysql' C-m
```
You can then navigate panes with `Ctrl-b` + arrow keys.  This setup lets you type commands in the container while watching log messages update in real time, which is helpful during TUI development.```


### Example test queries
```sql
SELECT * FROM comics;               -- legacy demo table
SELECT * FROM gcd_series LIMIT 5;    -- first few rows of the imported Grand Comics Database (column `name` holds the title)
SELECT * FROM members;
SELECT * FROM rentals WHERE status='RENTED';
SELECT * FROM rental_overview LIMIT 10; -- convenient join of rentals, members, and series
```

The `rental_overview` view makes it easy to see which member has checked out
which series without having to join tables manually.

> **Note:** some older databases created before the view was corrected may not
> contain `rental_overview` or may reference a non‑existent `title` column. You
> can rebuild it with:
> ```sql
> DROP VIEW IF EXISTS rental_overview;
> CREATE VIEW rental_overview AS
> SELECT r.id AS rental_id,
>        m.name AS member_name,
>        g.name AS comic_title,
>        r.rented_at,
>        r.due_date,
>        r.returned_at,
>        r.status
> FROM rentals r
> JOIN members m ON r.member_id = m.id
> JOIN gcd_series g ON r.comic_id = g.id;
> ```

All Docker‑related files (`docker-compose.yml`, `.env`, `Makefile`, etc.) reside in
the repository root. See the repository root README for full details.

## project scope and specification
✅ 만화책 대여점 (팀 과제 / MySQL)

### 🎯 개요

이 과제는 Java 콘솔 프로그램으로 **만화책 대여점 시스템**을 구현하는 팀 프로젝트입니다.

Java 기본 문법, 클래스 및 객체지향 설계, 사용자 입력 처리, 그리고 **MySQL \+ JDBC 기반 데이터 저장/조회**를 연습합니다.\~\!

본 과제는 완성된 결과물보다, 팀 단위 협업과 구현 과정을 경험하는 데 목적이 있습니다.

(DB 수업에서 다루는 MySQL 환경을 그대로 팀 활동 과제에 적용합니다.)

---

### 🧩 전체 기능 예시

| 기능 | 설명 |
| :---- | :---- |
| 만화책 등록 | `comic-add` 명령어 입력 시 제목/권수/작가를 받아 새 만화책 등록 |
| 만화책 목록 | `comic-list` 명령어 입력 시 모든 만화책을 번호순으로 출력 |
| 만화책 상세보기 | `comic-detail [id]` 명령어로 특정 만화책 상세 정보 확인 |
| 만화책 수정 | `comic-update [id]` 명령어로 제목/권수/작가 수정 |
| 만화책 삭제 | `comic-delete [id]` 명령어로 해당 만화책 삭제 |
| 회원 등록 | `member-add` 명령어로 회원 등록 |
| 회원 목록 | `member-list` 명령어로 회원 목록 출력 |
| 회원별 대여 | `member-rentals [memberId]` 명령어로 특정 회원의 대여 내역 확인 |
| 만화 검색 | `comic-search [keyword]` 명령어로 제목/작가 검색 |
| 대여 | `rent [comicId] [memberId]` 명령어로 대여 처리(대여중이면 불가) |
| 반납 | `return [rentalId]` 명령어로 반납 처리(이미 반납이면 불가) |
| 대여 목록 | `rental-list` 명령어로 대여 내역 출력(전체/미반납 구분 가능; `rental-list open` to show only open rentals) |
| 종료 | `exit` 명령어로 프로그램 종료 |

---

### ✅ 데이터 구조 예시

#### **Comic(만화책)**

class Comic {

    int id;

    String title;

    int volume;

    String author;

    boolean isRented;

    String regDate;  // yyyy-MM-dd

}

#### Member(회원)

classMember {

intid;

Stringname;

Stringphone;// 선택

StringregDate;// yyyy-MM-dd

}

---

### ⚙️ 주요 클래스 및 파일 구조 예시

src/

├─ Main.java                 ← 진입점

├─ App.java                  ← 프로그램 실행 로직(명령어 처리)

├─ Rq.java                   ← 커맨드 파싱 유틸

├─ DBUtil.java               ← JDBC 연결/자원반납 유틸

├─ Comic.java                ← 만화책 데이터 클래스

├─ Member.java               ← 회원 데이터 클래스

├─ Rental.java               ← 대여 기록 데이터 클래스

├─ ComicRepository.java      ← 만화책 DB 처리(JDBC)

├─ MemberRepository.java     ← 회원 DB 처리(JDBC)

└─ RentalRepository.java     ← 대여 기록 DB 처리(JDBC)

---

### 🧠 메서드 설계 예시

메서드 구성 역시 하나의 예시입니다. 기능 단위로 팀이 합의한 방식으로 설계해 주세요.

| 메서드명 | 설명 |
| :---- | :---- |
| `addComic()` | 만화책 등록(INSERT) |
| `listComics()` | 만화책 목록(SELECT) |
| `showComicDetail(int id)` | 만화책 상세(SELECT) |
| `updateComic(int id)` | 만화책 수정(UPDATE) |
| `deleteComic(int id)` | 만화책 삭제(DELETE) |
| `addMember()` | 회원 등록(INSERT) |
| `listMembers()` | 회원 목록(SELECT) |
| `rentComic(int comicId, int memberId)` | 대여 처리(대여중 체크 포함, 트랜잭션 권장) |
| `returnComic(int rentalId)` | 반납 처리(이미 반납 체크 포함, 트랜잭션 권장) |
| `listRentals()` | 대여 내역 출력(SELECT) |
| `getCurrentDate()` | 현재 날짜 리턴(yyyy-MM-dd 형식) |

---

## 💬 실행 예시

명령어: member-add

이름: 에밀리

전화번호: 010-1111-2222

\=\> 회원이 등록되었습니다. (id=1)

명령어: comic-add

제목: 슬램덩크

권수: 1

작가: 이노우에 다케히코

\=\> 만화책이 등록되었습니다. (id=1)  
명령어: member-add 이름: 에밀리 전화번호: 010-1111-2222 \=\> 회원이 등록되었습니다. (id=1)

명령어: comic-add 제목: 슬램덩크 권수: 1 작가: 이노우에 다케히코 \=\> 만화책이 등록되었습니다. (id=1)

명령어: comic-search 슬램 \=\> (검색 결과)
번호 | 제목       | 권수 | 작가               | 상태 | 등록일

### 명령어: comic-list 번호 | 제목       | 권수 | 작가               | 상태 | 등록일

1    | 슬램덩크   | 1    | 이노우에 다케히코   | 대여가능 | 2026-03-03

명령어: rent 1 1 \=\> 대여 완료: \[대여id=1\] 만화(1) → 회원(1)

명령어: member-rentals 1 \=\> (해당 회원의 대여 기록)

### 명령어: rental-list 대여id | 만화id | 회원id | 대여일     | 반납일

명령어: rental-list open \=\> (미반납만 표시)

1     | 1      | 1      | 2026-03-03 | \-

명령어: return 1 \=\> 반납 완료: 대여id=1

명령어: exit 프로그램을 종료합니다.

---

### 🎯 개발 포인트 요약

본 과제는 GitHub 저장소를 기준으로 커밋, Pull Request(PR), 코드 리뷰를 통해 협업하며 진행 하는 것을 전제로 합니다.

| 기술 요소 | 적용 |
| :---- | :---- |
| 입력 처리 | `Scanner` 활용하여 명령어/데이터 입력 받기 |
| DB 연동 | **MySQL \+ JDBC** (`Connection`, `PreparedStatement`, `ResultSet`) |
| 자원 관리 | `try-with-resources` 또는 `close()`로 자원 반납 |
| 날짜 처리 | `LocalDate.now()` 또는 MySQL `CURRENT_DATE` 활용 |
| 상태 관리 | 대여중 여부(`isRented`, `returnDate == null`) 처리 |

---

### ✅ 추가 기능 구현 예시 (선택)

아래 기능들은 예시로 제시되었으나 이번 구현에서는 모두 포함되어 있습니다:

- 만화책 검색 기능 (`comic-search [keyword]`) — 제목/작가 검색  
- 미반납 대여만 보기 (`rental-list open`)  
- 회원별 대여 내역 (`member-rentals [memberId]`)  
- 연체 기능(대여일 + 7일) 및 연체 목록 출력 (due_date 필드, 상태 `OVERDUE` 사용 가능)

---

## 📋 Implementation Task Checklist

### Foundation Layer (Tasks 1-5)

- [x] **Task 1**: Update domain entities to match spec
  - Verify and update `Comic.java`, `Member.java`, `Rental.java` with all required fields
  - Comic: `id`, `title`, `volume`, `author`, `isRented`, `regDate`
  - Member: `id`, `name`, `phone`, `regDate`
  - Rental: `id`, `comicId`, `memberId`, `rentedAt`, `returnedAt`, `status`

- [x] **Task 2**: Implement ComicRepository/DAO
  - Create `ComicRepository.java` with methods: `addComic()`, `listComics()`, `showComicDetail(id)`, `updateComic(id)`, `deleteComic(id)`
  - Use `PreparedStatement` and `try-with-resources`

- [x] **Task 3**: Implement MemberRepository/DAO
  - Create `MemberRepository.java` with methods: `addMember()`, `listMembers()`
  - Use `PreparedStatement` and `try-with-resources`

- [x] **Task 4**: Implement RentalRepository/DAO
  - Create `RentalRepository.java` with methods: `rentComic(comicId, memberId)`, `returnComic(rentalId)`, `listRentals()`
  - Include validation for rental status and transaction support

- [x] **Task 5**: Create Rq command parser utility
  - Implement `Rq.java` to parse command-line input
  - Extract command name and parameters (e.g., `'comic-add'`, `'rent 1 2'`)

### Application Layer (Tasks 6-17)

- [x] **Task 6**: Implement App.java with command dispatch
  - Create `App.java` to route commands: `comic-add`, `comic-list`, `comic-detail`, `comic-update`, `comic-delete`, `member-add`, `member-list`, `rent`, `return`, `rental-list`, `exit`

- [x] **Task 7**: Implement comic-add command
  - Get title, volume, author from user input
  - Call repository to insert into database
  - Display success message with generated ID

- [x] **Task 8**: Implement comic-list command
  - Display all comics in table format: 번호 | 제목 | 권수 | 작가 | 상태 | 등록일

- [x] **Task 9**: Implement comic-detail command
  - Show detailed information for a specific comic by ID

- [x] **Task 10**: Implement comic-update command
  - Allow updating title, volume, author for a specific comic by ID

- [x] **Task 11**: Implement comic-delete command
  - Delete a comic by ID from database

- [x] **Task 12**: Implement member-add command
  - Get name and phone from user
  - Insert into database
  - Display success with ID

- [x] **Task 13**: Implement member-list command
  - Display all members in table format

- [x] **Task 14**: Implement rent command
  - Process rental with validation: check if comic is already rented
  - Create rental record, update `comic.isRented = true`
  - Use transaction

- [x] **Task 15**: Implement return command
  - Process return with validation: check if rental exists and not returned
  - Update `rental.returnedAt`, update `comic.isRented = false`
  - Use transaction

- [x] **Task 16**: Implement rental-list command
  - Display rental history: 대여id | 만화id | 회원id | 대여일 | 반납일
  - Show '-' for unreturned rentals

- [x] **Task 17**: Update Main.java with Scanner loop
  - Replace stub Main with proper Scanner-based command loop
  - Integrate `App.java`
  - Call `DBConnectionUtil.registerShutdownHook()`
  - Handle exit command

### Testing (Tasks 18-20)

- [x] **Task 18**: Manual testing - comic operations
  - Test `comic-add`, `comic-list`, `comic-detail`, `comic-update`, `comic-delete` against Docker MySQL (completed)

- [x] **Task 19**: Manual testing - member operations
  - Test `member-add`, `member-list` against Docker MySQL (completed)

- [x] **Task 20**: Manual testing - rental operations
  - Test `rent`, `return`, `rental-list` including edge cases (double-rent, invalid return) against Docker MySQL (completed)

---

## 🚀 Getting Started with the CLI App

1. Ensure the MySQL Docker stack is running:
   ```sh
   cd project2
   make up
   ```
2. Compile and run the Java application (requires JDK 11+):
   ```sh
   # build all source files, including utilities
   javac -d out \
       src/main/java/com/aiegoo/comicrental/*.java \
       src/main/java/com/aiegoo/comicrental/dao/*.java \
       src/main/java/com/aiegoo/comicrental/util/*.java

   # start the program (replace the jar name/path if necessary)
   java -cp out:mysql-connector-java-8.0.xx.jar \
       com.aiegoo.comicrental.Main
   ```
3. At the prompt, type commands such as:
   * `comic-add` – register a new comic
   * `comic-list` – view all comics
   * `member-add` – register a member
   * `rent 1 1` – rent comic id 1 to member id 1
   * `rental-list` – see rental history
   * `exit` – terminate the program

> **Dependency:** the MySQL JDBC driver must be on the classpath when running the
> application. Download [mysql-connector-java](https://dev.mysql.com/downloads/connector/j/)
> (e.g. `mysql-connector-java-8.0.xx.jar`) and start the program with:
> ```sh
> java -cp out:mysql-connector-java-8.0.xx.jar com.aiegoo.comicrental.Main
> ```
> or add the jar to your project build (Maven/Gradle) if you use one.

The application will talk to the `comic_rental` database running in the container.

---

## 🛠 Fixes & Notes

During setup the following issues were encountered and corrected:

* **Schema syntax errors** in `init.sql` (index declarations and `CURRENT_DATE` defaults) were fixed so the script executes successfully. Indexes now use `INDEX idx_name (col)` and date defaults use parentheses.
* **JDBC URL options** needed `allowPublicKeyRetrieval=true` to permit the connector to authenticate with the Docker MySQL instance.
* Added a static initializer in `DBConnectionUtil` to explicitly load the MySQL driver class.
* Documentation updated to show how to download and supply the platform‑independent JDBC jar.

These changes are all committed on the `ssot` branch; the application now runs correctly after importing the schema.

---

## 🚧 Upcoming Text‑based User Interface (TUI)

Work on a richer TUI will take place on the `tui` branch and will eventually
replace the simple menu dashboard described earlier.

### Goals

* Provide full‑screen, menu‑driven screens with keyboard (arrow/enter) navigation.
* Use a Java console library such as Lanterna or JLine to manage windows, layouts
  and input.
* Offer the same operations currently available via CLI commands, with forms
  and paginated result lists.
* Preserve the existing command‑line interface for scripting/testing – CLI and
  the older dashboard will continue to work side‑by‑side.

### Local setup

1. **Install tmux & tmuxinator** (macOS/brew example):
   ```sh
   brew install tmux        # or your platform's package manager
   gem install tmuxinator   # requires Ruby gem system
   ```
2. **Obtain a TUI library**.  Download the Lanterna jar (e.g.
   `lanterna-3.2.1.jar`) from https://github.com/mabe02/lanterna/releases or
   add the dependency in your Maven/Gradle build.  Set the path via the
   `TUI_JAR` environment variable when running `make tui`.
3. **Build the project** using the provided Makefile; it now compiles the TUI
   package as well:
   ```sh
   make build JDBC_JAR=mysql-connector-java-8.0.xx.jar TUI_JAR=lanterna-3.2.1.jar
   ```
4. **Run modes** – choose one of the following:
   * `make run`   → the original CLI (verbose DB messages enabled)
   * `make dashboard` → the simple text‑menu dashboard (DB logs are
     suppressed to avoid scrolling the menu)
   * `make tui TUI_JAR=lanterna-3.2.1.jar` → the rich Lanterna‑based GUI
     (connection logging disabled and both stdout/stderr captured so command
     output and exceptions are shown in dialogs rather than corrupting the
     screen)
5. **tmux support** – the sample tmuxinator project (`tmux/comic.yml`) now
   launches the `Tui` class in its second window.  Copy the file to
   `~/.tmuxinator/comic.yml` and run:
   ```sh
   tmuxinator start comic
   ```
   Pane 1 will be a shell (for docker commands, SQL client, etc.) and pane 2 will
   start the Java TUI (once you’ve compiled with `TUI_JAR` on the classpath).

### Branch workflow

1. Create and switch to branch: `git checkout -b tui` (done).
2. Implement screen layouts and input handling (see `src/main/java/com/aiegoo/comicrental/tui/Tui.java`).
3. Keep documentation in README up to date, including sample screenshots.
4. Merge back to `master` when the TUI reaches feature parity.

### Schema compatibility

The application can run against either the simple `comics`/`members` schema
used by `schema.sql` or the more elaborate dataset produced by
`init.sql` (which includes a `gcd_series` table and a `phone` column on
`members`). On startup the DAOs detect which column names are available and
`RentalService` detects whether `gcd_series` exists, automatically adding an
`is_rented` column if necessary.  This means you can migrate an existing
database by simply running the application once; it will alter tables as
needed.  However, for new setups using Docker Compose it’s easiest to rely on
`init.sql` as described above.



