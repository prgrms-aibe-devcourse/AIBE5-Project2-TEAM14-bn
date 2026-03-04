package com.aiegoo.comicrental;

import java.time.LocalDate;

public class Member {
    private int id;
    private String name;
    private String phone;
    private LocalDate regDate;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getRegDate() { return regDate; }
    public void setRegDate(LocalDate regDate) { this.regDate = regDate; }
}
