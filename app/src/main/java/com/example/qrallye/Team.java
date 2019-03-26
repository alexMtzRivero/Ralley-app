package com.example.qrallye;

import android.graphics.Color;
import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Team {
    private String name;
    private long password;
    private Location position;
    private Color colorB;
    private String color;
    private Date startTimer;
    private Date endTimer;

    //<editor-fold desc="Getter and Setter">
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

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
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

    public Team(String name, long password, Location position, Color colorB, String color, Date startTimer, Date endTimer) {
        this.name = name;
        this.password = password;
        this.position = position;
        this.colorB = colorB;
        this.color = color;
        this.startTimer = startTimer;
        this.endTimer = endTimer;
    }
}
