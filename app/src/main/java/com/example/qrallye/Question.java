package com.example.qrallye;

import java.util.List;

public class Question {
    private int goodAnswer;
    private String question;
    private List<String> choices;

    public Question() {
    }

    public void setGoodAnswer(int goodAnswer) {
        this.goodAnswer = goodAnswer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public int getGoodAnswer() {
        return goodAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getChoices() {
        return choices;
    }
}
