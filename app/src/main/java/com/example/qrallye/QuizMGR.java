package com.example.qrallye;

import java.util.List;

public class QuizMGR {
    private static final QuizMGR ourInstance = new QuizMGR();

    private List<Question> questionList;

    public static QuizMGR getInstance() {
        return ourInstance;
    }
    private QuizMGR() {
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
}
