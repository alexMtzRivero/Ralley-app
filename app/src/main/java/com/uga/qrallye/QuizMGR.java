package com.uga.qrallye;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


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
    private boolean isWaitingForListOfProgress = true;
    private HashMap<String, ProgressItem> progressList;
    private boolean isWaitingForListOfAnswers = true;
    private HashSet<String> answersList;

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
        quizList = new ArrayList<>();
        finishedQuizList = new ArrayList<>();
        opponentTeamPositionList = new ArrayList<>();
        progressList = new HashMap<>();
        answersList = null;
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

    //----------------------Progress List-------------------------------

    public void retrieveProgressListFromDB() {
        isWaitingForListOfProgress = true;
        DatabaseMGR.getInstance().getProgressList();
    }

    public ArrayList<ProgressItem> getProgressList() {
        return new ArrayList<>(this.progressList.values());
    }

    public void setProgressList(HashMap<String, ProgressItem> progressList) {
        this.progressList = progressList;
    }

    public void addProgressListItem(ProgressItem progressList) {
        this.progressList.put(progressList.getTeam(), progressList);
    }

    public boolean isWaitingForListOfProgress() {
        return isWaitingForListOfProgress;
    }

    public void setWaitingForListOfProgress() {
        isWaitingForListOfProgress = false;
    }

    public void setProgressItemQuizzesCount(String key, int count) {
        if(this.progressList.get(key) != null)
            this.progressList.get(key).setQuizzesCount(count);
    }

    //----------------------Answers List-------------------------------

    public void retrieveAnswersListFromDB() {
        this.answersList = null;
        this.isWaitingForListOfAnswers = true;
        DatabaseMGR.getInstance().getAnswersList();
    }

    public HashSet<String> getAnswersList() {
        return this.answersList;
    }

    public boolean isWaitingForListOfAnswers() {
        return isWaitingForListOfAnswers;
    }

    public void setWaitingForListOfAnswersDone() {
        isWaitingForListOfAnswers = false;
    }

    public void setAnswersList(HashSet<String> res) {
        this.answersList = res;
    }
}
