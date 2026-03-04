package com.aiegoo.comicrental;

import java.util.List;

public class App {
    private final ComicRepository comicRepo = new ComicRepository();
    private final MemberRepository memberRepo = new MemberRepository();
    private final RentalRepository rentalRepo = new RentalRepository();

    public void handle(String line, Scanner scanner) {
        Rq rq = new Rq(line);
        String cmd = rq.getCommand();
        try {
            switch (cmd) {
                case "comic-add" -> cmdComicAdd(scanner);
                case "comic-list" -> cmdComicList();
                case "comic-detail" -> cmdComicDetail(rq.getArg(0));
                case "comic-update" -> cmdComicUpdate(rq.getArg(0));
                case "comic-delete" -> cmdComicDelete(rq.getArg(0));
                case "member-add" -> cmdMemberAdd(scanner);
                case "member-list" -> cmdMemberList();
                case "rent" -> cmdRent(rq.getArg(0), rq.getArg(1));
                case "return" -> cmdReturn(rq.getArg(0));
                case "rental-list" -> cmdRentalList();
                case "" -> System.out.println();
                default -> System.out.println("Unknown command: " + cmd);
            }
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // stubs for command implementations; actual input handling will be added later (in Main)
    private void cmdComicAdd(Scanner scanner) throws Exception {
        System.out.print("제목: ");
        String title = scanner.nextLine().trim();
        System.out.print("권수: ");
        int volume = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("작가: ");
        String author = scanner.nextLine().trim();
        Comic c = new Comic();
        c.setTitle(title);
        c.setVolume(volume);
        c.setAuthor(author);
        c.setRented(false);
        c.setRegDate(java.time.LocalDate.now());
        comicRepo.addComic(c);
        System.out.println("만화책이 등록되었습니다. (id=" + c.getId() + ")");
    }

    private void cmdComicList() throws Exception {
        List<Comic> list = comicRepo.listComics();
        System.out.printf("번호 | 제목 | 권수 | 작가 | 상태 | 등록일\n");
        for (Comic c : list) {
            String status = c.isRented() ? "대여중" : "대여가능";
            System.out.printf("%d | %s | %d | %s | %s | %s\n",
                    c.getId(), c.getTitle(), c.getVolume(), c.getAuthor(), status,
                    c.getRegDate() == null ? "" : c.getRegDate().toString());
        }
    }

    private void cmdComicDetail(String idStr) throws Exception {
        if (idStr == null) {
            System.out.println("Usage: comic-detail [id]");
            return;
        }
        int id = Integer.parseInt(idStr);
        Comic c = comicRepo.showComicDetail(id);
        if (c == null) {
            System.out.println("Comic not found.");
        } else {
            System.out.println("ID: " + c.getId());
            System.out.println("Title: " + c.getTitle());
            System.out.println("Volume: " + c.getVolume());
            System.out.println("Author: " + c.getAuthor());
            System.out.println("Rented: " + c.isRented());
            System.out.println("Registered: " + c.getRegDate());
        }
    }

    private void cmdComicUpdate(String idStr) throws Exception {
        if (idStr == null) {
            System.out.println("Usage: comic-update [id]");
            return;
        }
        int id = Integer.parseInt(idStr);
        Comic c = comicRepo.showComicDetail(id);
        if (c == null) {
            System.out.println("Comic not found.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("제목 ("+c.getTitle()+"): ");
        String title = scanner.nextLine().trim();
        if (!title.isEmpty()) c.setTitle(title);
        System.out.print("권수 ("+c.getVolume()+"): ");
        String volInput = scanner.nextLine().trim();
        if (!volInput.isEmpty()) c.setVolume(Integer.parseInt(volInput));
        System.out.print("작가 ("+c.getAuthor()+"): ");
        String author = scanner.nextLine().trim();
        if (!author.isEmpty()) c.setAuthor(author);
        comicRepo.updateComic(c);
        System.out.println("Comic updated.");
    }

    private void cmdComicDelete(String idStr) throws Exception {
        if (idStr == null) {
            System.out.println("Usage: comic-delete [id]");
            return;
        }
        comicRepo.deleteComic(Integer.parseInt(idStr));
        System.out.println("Comic deleted.");
    }

    private void cmdMemberAdd(Scanner scanner) throws Exception {
        System.out.print("이름: ");
        String name = scanner.nextLine().trim();
        System.out.print("전화번호: ");
        String phone = scanner.nextLine().trim();
        Member m = new Member();
        m.setName(name);
        m.setPhone(phone);
        m.setRegDate(java.time.LocalDate.now());
        memberRepo.addMember(m);
        System.out.println("회원이 등록되었습니다. (id=" + m.getId() + ")");
    }

    private void cmdMemberList() throws Exception {
        List<Member> list = memberRepo.listMembers();
        System.out.printf("ID | 이름 | 전화번호 | 가입일\n");
        for (Member m : list) {
            System.out.printf("%d | %s | %s | %s\n", m.getId(), m.getName(), m.getPhone(),
                    m.getRegDate() == null ? "" : m.getRegDate().toString());
        }
    }

    private void cmdRent(String comicIdStr, String memberIdStr) throws Exception {
        if (comicIdStr == null || memberIdStr == null) {
            System.out.println("Usage: rent [comicId] [memberId]");
            return;
        }
        int comicId = Integer.parseInt(comicIdStr);
        int memberId = Integer.parseInt(memberIdStr);
        Comic c = comicRepo.showComicDetail(comicId);
        if (c == null) {
            System.out.println("Comic not found.");
            return;
        }
        if (c.isRented()) {
            System.out.println("Comic is already rented.");
            return;
        }
        // perform rental in transaction
        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Rental r = new Rental();
                r.setComicId(comicId);
                r.setMemberId(memberId);
                r.setStatus("RENTED");
                r.setRentedAt(java.time.LocalDateTime.now());
                rentalRepo.add(r);
                c.setRented(true);
                comicRepo.updateComic(c);
                conn.commit();
                System.out.println("대여 완료: [대여id=" + r.getId() + "] 만화("+comicId+") → 회원("+memberId+")");
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    private void cmdReturn(String rentalIdStr) throws Exception {
        if (rentalIdStr == null) {
            System.out.println("Usage: return [rentalId]");
            return;
        }
        int rid = Integer.parseInt(rentalIdStr);
        Rental r = rentalRepo.findById(rid);
        if (r == null) {
            System.out.println("Rental not found.");
            return;
        }
        if ("RETURNED".equals(r.getStatus())) {
            System.out.println("Already returned.");
            return;
        }
        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                r.setStatus("RETURNED");
                r.setReturnedAt(java.time.LocalDateTime.now());
                rentalRepo.update(r);
                Comic c = comicRepo.showComicDetail(r.getComicId());
                c.setRented(false);
                comicRepo.updateComic(c);
                conn.commit();
                System.out.println("반납 완료: 대여id=" + rid);
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    private void cmdRentalList() throws Exception {
        List<Rental> list = rentalRepo.listAll();
        System.out.printf("대여id | 만화id | 회원id | 대여일 | 반납일\n");
        for (Rental r : list) {
            String returned = r.getReturnedAt() == null ? "-" : r.getReturnedAt().toLocalDate().toString();
            System.out.printf("%d | %d | %d | %s | %s\n",
                    r.getId(), r.getComicId(), r.getMemberId(),
                    r.getRentedAt().toLocalDate().toString(), returned);
        }
    }
}
