package com.example.qrallye;

import android.util.Log;
import java.util.ArrayList;


public class QuizMGR {
    private static QuizMGR ourInstance;
    private ArrayList<Question> questionList;
    private ArrayList<Quiz> quizList;
    private ArrayList<Quiz> finishedQuizList;
    public boolean complete = false;
    private boolean isWaitingForListOfQuiz = true;
    private boolean isWaitingForListOfFinishedQuiz = true;
    private String currentQuiz = "";
    private ArrayList<Team> opponentTeamPositionList;
    private boolean isWaitingForListOfOpponentPosition = true;

    public static QuizMGR getInstance() {
        if  (ourInstance == null) {
            synchronized (QuizMGR.class) {
                if (ourInstance == null) {
                    ourInstance = new QuizMGR();
                }
            }
        }

        return ourInstance;
    }

    private QuizMGR() {
        questionList = new ArrayList<>();
        quizList = null;
        finishedQuizList = new ArrayList<>();
        opponentTeamPositionList = new ArrayList<>();
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
        this.isWaitingForListOfQuiz = true;
        DatabaseMGR.getInstance().getListOfQuiz();
    }

    public boolean isWaitingForListOfQuiz() {
        return isWaitingForListOfQuiz;
    }

    public void setWaitingForListOfQuizDone(boolean bool) {
        isWaitingForListOfQuiz = bool;
    }


    //--------------------------Finished Quizzes List ----------------

    public void retrieveFinishedQuizListFromDB(){
        this.isWaitingForListOfFinishedQuiz = true;
        DatabaseMGR.getInstance().getFinishedQuizzesForTeamLogged();
    }

    public ArrayList<Quiz> getFinishedQuizList() {
        return finishedQuizList;
    }

    public void setFinishedQuizList(ArrayList<Quiz> finishedQuizList) {
        this.finishedQuizList = finishedQuizList;
    }

    public boolean isWaitingForListOfFinishedQuiz() {
        return isWaitingForListOfFinishedQuiz;
    }

    public void setWaitingForListOfFinishedQuizDone(boolean bool) {
        isWaitingForListOfFinishedQuiz = bool;
    }

    //-------------------------Opponent Teams Position List-------------

    public void retrieveOpponentTeamPositionListFromDB() {
        isWaitingForListOfOpponentPosition = true;
        DatabaseMGR.getInstance().getListOfOpponentPosition();
    }

    public ArrayList<Team> getListOfOpponentPosition() {
        return this.opponentTeamPositionList;
    }

    public void setListOfOpponentPosition(ArrayList<Team> opponentTeamPositionList) {
        this.opponentTeamPositionList = opponentTeamPositionList;
    }

    public boolean isWaitingForListOfOpponentPosition() {
        return isWaitingForListOfOpponentPosition;
    }

    public void setWaitingForListOfOpponentPosition(boolean bool) {
        isWaitingForListOfOpponentPosition = bool;
    }
}
