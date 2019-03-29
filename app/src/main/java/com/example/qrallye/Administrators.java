package com.example.qrallye;

public class Administrators {
    private String username;
    private String number;


    public Administrators(){

    }
    public Administrators(String username , String number) {
        this.username = username;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getUsername() {
        return username;
    }

}
