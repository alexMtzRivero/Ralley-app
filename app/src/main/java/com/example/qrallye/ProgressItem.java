package com.example.qrallye;

import java.util.Date;

class ProgressItem {

    private String team;
    private Long chrono;
    private int quizzesCount;

    public ProgressItem(String team, Long chrono, int quizzesCount) {
        this.team = team;
        this.chrono = chrono;
        this.quizzesCount = quizzesCount;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Long getChrono() {
        return chrono;
    }

    public void setChrono(Long chrono) {
        this.chrono = chrono;
    }

    public int getQuizzesCount() {
        return quizzesCount;
    }

    public void setQuizzesCount(int quizzesCount) {
        this.quizzesCount = quizzesCount;
    }
}
