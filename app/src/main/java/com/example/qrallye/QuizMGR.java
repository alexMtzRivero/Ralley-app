package com.example.qrallye;

import java.util.ArrayList;
import android.util.Log;

public class QuizMGR {
    private static final QuizMGR ourInstance = new QuizMGR();

    private ArrayList<Question> questionList;
    public boolean complete = false;

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

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }


    public void addQuestion(Question question){
        questionList.add(question);
    }

    public void onQuestionListRetrieved(){
        complete = true;
    }

    public String getCurrentQuiz() {
        return currentQuiz;
    }

    public void setCurrentQuiz(String currentQuiz) {
        this.currentQuiz = currentQuiz;
    }
}
