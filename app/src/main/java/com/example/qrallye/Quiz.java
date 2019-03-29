package com.example.qrallye;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

import java.util.Date;

public class Quiz {
    private String nomQuiz;
    private GeoPoint position;
    private Date startQuiz;
    private Date endQuiz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getNomQuiz() {
        return nomQuiz;
    }

    public void setNomQuiz(String nomQuiz) {
        this.nomQuiz = nomQuiz;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }

    public Date getStartQuiz() {
        return startQuiz;
    }

    public void setStartQuiz(Date startQuiz) {
        this.startQuiz = startQuiz;
    }

    public Date getEndQuiz() {
        return endQuiz;
    }

    public void setEndQuiz(Date endQuiz) {
        this.endQuiz = endQuiz;
    }

    public Quiz(String nomQuiz, GeoPoint position, String id, Date startQuiz, Date endQuiz) {
        this.nomQuiz = nomQuiz;
        this.position = position;
        this.id = id;
        this.startQuiz = startQuiz;
        this.endQuiz = endQuiz;
    }

    public Quiz() {
    }
}
