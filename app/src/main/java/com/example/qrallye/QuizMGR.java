package com.example.qrallye;

import android.util.Log;

import java.util.ArrayList;


public class QuizMGR {
    private static final QuizMGR ourInstance = new QuizMGR();
    private ArrayList<Question> questionList;
    private ArrayList<Quiz> quizList;
    private ArrayList<Quiz> quizDoneList;
    public boolean complete = false;
    private boolean isWaitingForListOfQuiz = true;
    private String currentQuiz = "";
    public static QuizMGR getInstance() {
        return ourInstance;
    }

    public ArrayList<Quiz> getQuizList() {
        return quizList;
    }

    public void setQuizList(ArrayList<Quiz> quizList) {
        this.quizList = quizList;
    }

    private QuizMGR() {
        questionList = new ArrayList<>();
        quizList = new ArrayList<>();
        quizDoneList = new ArrayList<>();
    }

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }


    public void addQuestion(Question question){
        questionList.add(question);
    }

    public String getCurrentQuiz() {
        return currentQuiz;
    }

    public void setCurrentQuiz(String currentQuiz) {
        Log.d("M.B", "setCurrentQuiz: ");
        this.currentQuiz = currentQuiz;
        this.questionList = new ArrayList<>();
        this.complete = false;
    }

    public void onQuestionListRetrieved(){
        Log.d("M.B", "onQuestionListRetrieved: ");
        complete = true;
    }

    public boolean isWaitingForListOfQuiz() {
        return isWaitingForListOfQuiz;
    }

    public void setWaitingForListOfQuiz() {
        isWaitingForListOfQuiz = false;
    }
    public ArrayList<Quiz> getQuizDoneList() {
        return quizDoneList;
    }

    public void setQuizDoneList(ArrayList<Quiz> quizDoneList) {
        this.quizDoneList = quizDoneList;
    }

}
