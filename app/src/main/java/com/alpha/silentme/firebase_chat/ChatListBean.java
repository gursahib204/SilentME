package com.alpha.silentme.firebase_chat;

public class ChatListBean {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    private String name ="";
    private String email = "";
    private String college = "";

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public ChatListBean(String name, String email, String college) {
        this.name = name;
        this.email = email;
        this.college = college;
    }

    public ChatListBean() {
    }
}
