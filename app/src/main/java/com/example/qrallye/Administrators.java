package com.example.qrallye;

public class Administrators {
    private String password;
    private String username;

    private String number;

    public Administrators(){

    }
    public Administrators(String username , String password , String number) {
        this.username = username;
        this.password = password;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
