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

    private QuizMGR() {
        questionList = new ArrayList<>();
        quizList = new ArrayList<>();
        FinishedQuizList = null;
    }

    //-------------------------- Current Quiz ----------------

    public String getCurrentQuiz() {
        return currentQuiz;
    }

    public void setCurrentQuiz(String currentQuiz) {
        Log.d("M.B", "setCurrentQuiz: ");
        this.currentQuiz = currentQuiz;
        this.questionList = new ArrayList<>();
        this.complete = false;
    }

    //-------------------------- Question List ----------------

    public ArrayList<Question> getQuestionList() {
        return questionList;
    }

    public void addQuestion(Question question){
        questionList.add(question);
    }

    public void onQuestionListRetrieved(){
        Log.d("M.B", "onQuestionListRetrieved: ");
        complete = true;
    }

    //-------------------------- Quiz List ----------------

    public ArrayList<Quiz> getQuizList() {
        return quizList;
    }
    public void setQuizList(ArrayList<Quiz> quizList) {
        this.quizList = quizList;
    }

    public void retrieveQuizList(){
        DatabaseMGR.getInstance().getListOfQuiz();
    }

    public boolean isWaitingForListOfQuiz() {
        return isWaitingForListOfQuiz;
    }

    public void setWaitingForListOfQuizDone(boolean bool) {
        isWaitingForListOfQuiz = bool;
    }


    //--------------------------Finished Quizzes List ----------------

    public ArrayList<Quiz> getFinishedQuizList() {
        return FinishedQuizList;
    }

    public void setFinishedQuizList(ArrayList<Quiz> finishedQuizList) {
        this.FinishedQuizList = finishedQuizList;
    }

    public boolean isWaitingForListOfFinishedQuiz() {
        return isWaitingForListOfFinishedQuiz;
    }

    public void setWaitingForListOfFinishedQuizDone(boolean bool) {
        isWaitingForListOfFinishedQuiz = bool;
    }
}
