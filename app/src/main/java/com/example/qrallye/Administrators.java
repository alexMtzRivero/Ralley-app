package com.example.qrallye;

public class Administrators {
    private String password;

    private String username;
    public Administrators(){

    }

    public Administrators(String username , String password) {
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
