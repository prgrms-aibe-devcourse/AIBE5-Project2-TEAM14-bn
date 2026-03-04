package com.aiegoo.comicrental;

import java.time.LocalDate;

public class Comic {
    private int id;
    private String title;
    private int volume;
    private String author;
    private boolean isRented;
    private LocalDate regDate;

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public boolean isRented() { return isRented; }
    public void setRented(boolean rented) { isRented = rented; }
    public LocalDate getRegDate() { return regDate; }
    public void setRegDate(LocalDate regDate) { this.regDate = regDate; }
}
