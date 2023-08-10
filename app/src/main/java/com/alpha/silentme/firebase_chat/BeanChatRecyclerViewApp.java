package com.alpha.silentme.firebase_chat;

public class BeanChatRecyclerViewApp {
    private String msg;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BeanChatRecyclerViewApp(String msg, String email,String time) {
        this.msg = msg;
        this.email = email;
        this.time = time;
    }

    public BeanChatRecyclerViewApp() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
