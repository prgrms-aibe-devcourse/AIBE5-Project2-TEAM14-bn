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
| 대여 | `rent [comicId] [memberId]` 명령어로 대여 처리(대여중이면 불가) |
| 반납 | `return [rentalId]` 명령어로 반납 처리(이미 반납이면 불가) |
| 대여 목록 | `rental-list` 명령어로 대여 내역 출력(전체/미반납 구분 가능) |
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

### 명령어: comic-list 번호 | 제목       | 권수 | 작가               | 상태 | 등록일

1    | 슬램덩크   | 1    | 이노우에 다케히코   | 대여가능 | 2026-03-03

명령어: rent 1 1 \=\> 대여 완료: \[대여id=1\] 만화(1) → 회원(1)

### 명령어: rental-list 대여id | 만화id | 회원id | 대여일     | 반납일

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

추가 기능은 **필수가 아니며** 팀의 학습 단계와 여유에 맞게 선택적으로 진행해주세요.

- 만화책 검색 기능 (`comic-search [keyword]`) — 제목/작가 검색  
- 미반납 대여만 보기 (`rental-list open`)  
- 회원별 대여 내역 (`member-rentals [memberId]`)  
- 연체 기능(대여일 \+ 7일) 및 연체 목록 출력

---

## 📋 Implementation Task Checklist

### Foundation Layer (Tasks 1-5)

- [ ] **Task 1**: Update domain entities to match spec
  - Verify and update `Comic.java`, `Member.java`, `Rental.java` with all required fields
  - Comic: `id`, `title`, `volume`, `author`, `isRented`, `regDate`
  - Member: `id`, `name`, `phone`, `regDate`
  - Rental: `id`, `comicId`, `memberId`, `rentedAt`, `returnedAt`, `status`

- [ ] **Task 2**: Implement ComicRepository/DAO
  - Create `ComicRepository.java` with methods: `addComic()`, `listComics()`, `showComicDetail(id)`, `updateComic(id)`, `deleteComic(id)`
  - Use `PreparedStatement` and `try-with-resources`

- [ ] **Task 3**: Implement MemberRepository/DAO
  - Create `MemberRepository.java` with methods: `addMember()`, `listMembers()`
  - Use `PreparedStatement` and `try-with-resources`

- [ ] **Task 4**: Implement RentalRepository/DAO
  - Create `RentalRepository.java` with methods: `rentComic(comicId, memberId)`, `returnComic(rentalId)`, `listRentals()`
  - Include validation for rental status and transaction support

- [ ] **Task 5**: Create Rq command parser utility
  - Implement `Rq.java` to parse command-line input
  - Extract command name and parameters (e.g., `'comic-add'`, `'rent 1 2'`)

### Application Layer (Tasks 6-17)

- [ ] **Task 6**: Implement App.java with command dispatch
  - Create `App.java` to route commands: `comic-add`, `comic-list`, `comic-detail`, `comic-update`, `comic-delete`, `member-add`, `member-list`, `rent`, `return`, `rental-list`, `exit`

- [ ] **Task 7**: Implement comic-add command
  - Get title, volume, author from user input
  - Call repository to insert into database
  - Display success message with generated ID

- [ ] **Task 8**: Implement comic-list command
  - Display all comics in table format: 번호 | 제목 | 권수 | 작가 | 상태 | 등록일

- [ ] **Task 9**: Implement comic-detail command
  - Show detailed information for a specific comic by ID

- [ ] **Task 10**: Implement comic-update command
  - Allow updating title, volume, author for a specific comic by ID

- [ ] **Task 11**: Implement comic-delete command
  - Delete a comic by ID from database

- [ ] **Task 12**: Implement member-add command
  - Get name and phone from user
  - Insert into database
  - Display success with ID

- [ ] **Task 13**: Implement member-list command
  - Display all members in table format

- [ ] **Task 14**: Implement rent command
  - Process rental with validation: check if comic is already rented
  - Create rental record, update `comic.isRented = true`
  - Use transaction

- [ ] **Task 15**: Implement return command
  - Process return with validation: check if rental exists and not returned
  - Update `rental.returnedAt`, update `comic.isRented = false`
  - Use transaction

- [ ] **Task 16**: Implement rental-list command
  - Display rental history: 대여id | 만화id | 회원id | 대여일 | 반납일
  - Show '-' for unreturned rentals

- [ ] **Task 17**: Update Main.java with Scanner loop
  - Replace stub Main with proper Scanner-based command loop
  - Integrate `App.java`
  - Call `DBConnectionUtil.registerShutdownHook()`
  - Handle exit command

### Testing (Tasks 18-20)

- [ ] **Task 18**: Manual testing - comic operations
  - Test `comic-add`, `comic-list`, `comic-detail`, `comic-update`, `comic-delete` against Docker MySQL

- [ ] **Task 19**: Manual testing - member operations
  - Test `member-add`, `member-list` against Docker MySQL

- [ ] **Task 20**: Manual testing - rental operations
  - Test `rent`, `return`, `rental-list` including edge cases (double-rent, invalid return) against Docker MySQL
