package com.example.qrallye;

import java.util.ArrayList;

public class QuizMGR {
    private static final QuizMGR ourInstance = new QuizMGR();

    private ArrayList<Question> questionList;
    private ArrayList<Quiz> quizList;
    public boolean complete = false;
    private String currentQuiz = "";

    public static QuizMGR getInstance() {
        return ourInstance;
    }

    private QuizMGR() {
        questionList = new ArrayList<>();
        quizList = new ArrayList<>();
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

    public String getCurrentQuiz() {
        return currentQuiz;
    }

    public void setCurrentQuiz(String currentQuiz) {

        this.currentQuiz = currentQuiz;
        this.questionList = new ArrayList<>();
        this.complete = false;
    }

    public void onQuestionListRetrieved(){
        complete = true;
    }

}
