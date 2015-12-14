package com.project.estevao.chatbluetooth.entities;

import java.io.Serializable;

/**
 * Created by c1284520 on 13/11/2015.
 */
public class DataMessage implements Serializable{

    private String txt;
    private String type;
    private String nameUser;
    private Long time;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DataMessage() {
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getType() {
        return type;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}

