package com.alpha.silentme.bean;

public class User {
    public String email;
    public String name;
    public String college;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String email, String name, String college) {
        this.email = email;
        this.name = name;
        this.college = college;
    }
}
