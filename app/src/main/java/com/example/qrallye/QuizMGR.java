package com.example.qrallye;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuizMGR {
    private static final QuizMGR ourInstance = new QuizMGR();

    private ArrayList<Question> questionList;

    private String currentQuiz = "";

    public static QuizMGR getInstance() {
        return ourInstance;
    }

    private QuizMGR() {
        questionList = new ArrayList<>();
    }
    public boolean isAtGoodPostion(){

        return  true;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }


    public void addQuestion(Question question){
        questionList.add(question);
    }

    public String getCurrentQuiz() {
        return currentQuiz;
    }

    public void setCurrentQuiz(String currentQuiz) {
        this.currentQuiz = currentQuiz;
    }
}
