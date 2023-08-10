package com.alpha.silentme.firebase_chat;

public class ChatListBeanWithKey {
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

    public String getuUId() {
        return uUId;
    }

    public void setuUId(String uUId) {
        this.uUId = uUId;
    }

    private String name ="";
    private String email = "";
    private String college = "";
    private String uUId = "";

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public ChatListBeanWithKey(String name, String email, String college,String Uuid) {
        this.name = name;
        this.uUId = Uuid;
        this.email = email;
        this.college = college;
    }

    public ChatListBeanWithKey() {
    }

}
