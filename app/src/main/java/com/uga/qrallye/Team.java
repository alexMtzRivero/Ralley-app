package com.uga.qrallye;

import android.graphics.Color;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Team {
    private String name;
    private long password;
    private GeoPoint position;
    private Color colorB;
    private String color;
    private Date startTimer;
    private Date endTimer;
    private String currentQuiz;
    private String token;

    //<editor-fold desc="Getter and Setter">

    public String getCurrentQuiz() {
        return currentQuiz;
    }

    public void setCurrentQuiz(String currentQuiz) {
        this.currentQuiz = currentQuiz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPassword() {
        return password;
    }

    public void setPassword(long password) {
        this.password = password;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    public Color getColorB() {
        return colorB;
    }

    public void setColorB(Color colorB) {
        this.colorB = colorB;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getStartTimer() {
        return startTimer;
    }

    public void setStartTimer(Date startTimer) {
        this.startTimer = startTimer;
    }

    public Date getEndTimer() {
        return endTimer;
    }

    public void setEndTimer(Date endTimer) {
        this.endTimer = endTimer;
    }
    //</editor-fold>

    public Team(){
    }

    public Team(String name, long password, GeoPoint position, Color colorB, String color, Date startTimer, Date endTimer, String currentQuiz, String token) {
        this.name = name;
        this.password = password;
        this.position = position;
        this.colorB = colorB;
        this.color = color;
        this.startTimer = startTimer;
        this.endTimer = endTimer;
        this.currentQuiz = currentQuiz;
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
