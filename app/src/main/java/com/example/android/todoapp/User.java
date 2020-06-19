package com.example.android.todoapp;

public class User {
    private String userid;
    private String firstname;
    private String secondname;
    private String username;
    private String phoneno;
    private String email;
    private String country;

    public String getUserid() {
        return userid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public User(String userid, String firstname, String secondname, String username, String phoneno, String email, String country) {
        this.userid = userid;
        this.firstname = firstname;
        this.secondname = secondname;
        this.username = username;
        this.phoneno = phoneno;
        this.email = email;
        this.country = country;
    }

    public User() {

    }
}
