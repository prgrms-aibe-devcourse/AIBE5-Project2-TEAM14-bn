package com.aiegoo.comicrental;

import java.time.LocalDateTime;

public class Rental {
    private int id;
    private int comicId;
    private int memberId;
    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
    private LocalDateTime dueDate;
    private String status; // RENTED, RETURNED or OVERDUE

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getComicId() { return comicId; }
    public void setComicId(int comicId) { this.comicId = comicId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public LocalDateTime getRentedAt() { return rentedAt; }
    public void setRentedAt(LocalDateTime rentedAt) { this.rentedAt = rentedAt; }
    public LocalDateTime getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDateTime returnedAt) { this.returnedAt = returnedAt; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
