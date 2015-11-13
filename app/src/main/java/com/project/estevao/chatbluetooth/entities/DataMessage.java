package com.project.estevao.chatbluetooth.entities;

/**
 * Created by c1284520 on 13/11/2015.
 */
public class DataMessage {

    String txt;
    String type;
    private String nameUser;

    public DataMessage() {
    }

    public boolean isType() {
        if (type.equals("WRITE")) {
            return false;  //se for escrita -- enviando mensagem
        }
        else
            return true;    //se estiver recebendo

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
}

