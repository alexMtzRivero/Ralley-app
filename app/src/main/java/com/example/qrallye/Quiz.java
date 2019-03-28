package com.example.qrallye;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.LatLng;

public class Quiz {
    private String nomQuiz;
    private GeoPoint position;

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

    public Quiz(String nomQuiz, GeoPoint position, String id) {
        this.nomQuiz = nomQuiz;
        this.position = position;
        this.id = id;
    }

    public Quiz() {
    }
}
