package com.example.qrallye;

import android.util.Log;

import java.util.ArrayList;


public class QuizMGR {
    private static final QuizMGR ourInstance = new QuizMGR();
    private ArrayList<Question> questionList;
    private ArrayList<Quiz> quizList;
    private ArrayList<Quiz> FinishedQuizList;
    public boolean complete = false;
    private boolean isWaitingForListOfQuiz = true;
    private boolean isWaitingForListOfFinishedQuiz = true;
    private String currentQuiz = "";
    public static QuizMGR getInstance() {
        return ourInstance;
    }

    public ArrayList<Quiz> getQuizList() {
        return quizList;
    }

    public boolean retrieveQuizList(){
        ArrayList<Quiz> res = DatabaseMGR.getInstance().getListOfQuiz();
        if(this.quizList.size() == 0)
            this.quizList = res;
        return true;
    }

    private QuizMGR() {
        questionList = new ArrayList<>();
        quizList = new ArrayList<>();
        FinishedQuizList = new ArrayList<>();
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

    public void setWaitingForListOfQuizDone() {
        isWaitingForListOfQuiz = false;
    }
    public ArrayList<Quiz> getFinishedQuizList() {
        return FinishedQuizList;
    }

    public void setFinishedQuizList(ArrayList<Quiz> finishedQuizList) {
        this.FinishedQuizList = finishedQuizList;
    }

    public boolean isWaitingForListOfFinishedQuiz() {
        return isWaitingForListOfQuiz;
    }

    public void setWaitingForListOfFinishedQuizDone() {
        isWaitingForListOfQuiz = false;
    }
}
