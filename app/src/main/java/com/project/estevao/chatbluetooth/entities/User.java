package com.project.estevao.chatbluetooth.entities;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by C1284520 on 01/12/2015.
 */
public class User implements Serializable {

    private Long id;
    private Boolean login;
    private byte[] photo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean login) {
        this.login = login;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

}
