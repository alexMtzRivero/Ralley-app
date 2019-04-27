package com.example.qrallye;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class DatabaseMGR {
    private final String TAG = "DatabaseMGR";
    private static final DatabaseMGR ourInstance = new DatabaseMGR();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference adminCollections = db.collection("Administrators");
    private CollectionReference teamCollections = db.collection("Groups");
    private CollectionReference quizzesCollections = db.collection("Quizzes");
    private Team team;
    public static DatabaseMGR getInstance() {
        return ourInstance;
    }

    private DatabaseMGR() {
    }

    public void getAdmin(){
        adminCollections.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Administrators> adminList = new ArrayList<>();
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Administrators administrators = queryDocumentSnapshot.toObject(Administrators.class);
                    adminList.add(administrators);
                }
                Log.i(TAG, "Admins list retrieved");
                SessionMGR.getInstance().onAdminListFound(adminList);
            }
        });

    }

    public void getTeam(final String teamName){
        team = null;
        Log.i(TAG, "getTeam: Recherche de la team");
        DocumentReference teamRef = teamCollections.document(teamName);
        teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.getData()!=null) {
                        Log.d(TAG, "onComplete: " + doc.getData());
                        team = new Team(teamName,
                                (long) doc.get("password"),
                                doc.getGeoPoint("position"),
                                null,
                                doc.getString("color"),
                                (doc.getTimestamp("startRallye") != null) ?doc.getTimestamp("startRallye").toDate():null,
                                (doc.getTimestamp("endRallye") != null) ?doc.getTimestamp("endRallye").toDate():null,
                                (doc.getString("currentQuiz") != null) ?doc.getString("currentQuiz"):"",
                                (doc.getString("token") != null) ?doc.getString("token"):""
                        );
                        Log.d(TAG, "onComplete: team " + team.getStartTimer() );
                        SessionMGR.getInstance().onTeamFound(team);

                    }
                    else SessionMGR.getInstance().onTeamFound(null);
                    Log.e(TAG, "team could not be retrieved");
                }
                else{
                    SessionMGR.getInstance().onTeamFound(null);
                    Log.e(TAG, "team could not be retrieved");
                }
            }
        });
    }

    public void getQuestionsFromQuiz(final String quizName){

        QuizMGR.getInstance().setCurrentQuiz(quizName);

        CollectionReference questionsRef = quizzesCollections.document(quizName).collection("Questions");

        questionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult() != null && task.getResult().size() != 0){
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Question tmp = snapshot.toObject(Question.class);
                            QuizMGR.getInstance().addQuestion(tmp);
                        }
                        Team tmpTeam = SessionMGR.getInstance().getLogedTeam();
                        Map<String,Object> toPush = new HashMap<>();
                        toPush.put("startQuiz", FieldValue.serverTimestamp());
                        Map<String, Object> pushCurrentQuiz = new HashMap<>();
                        pushCurrentQuiz.put("currentQuiz", quizName);
                        teamCollections.document(tmpTeam.getName()).update(pushCurrentQuiz);
                        teamCollections.document(tmpTeam.getName()).collection("Answers").document(quizName).set(toPush);
                        setCurrentQuizForTeamLogged(quizName);
                        Log.i(TAG, "questions retrieved");
                    }
                    else {
                        Log.e(TAG, "questions could not be retrieved");
                    }
                }
                else
                {
                    Log.e(TAG, "questions could not be retrieved");
                }
                QuizMGR.getInstance().onQuestionListRetrieved();
            }
        });
    }

    public ArrayList<Quiz> getListOfQuiz(){
        final ArrayList<Quiz> quizList = new ArrayList<>();
        quizzesCollections.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult() != null){
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Quiz quiz = new Quiz(snapshot.get("nomQuiz").toString(),
                                    snapshot.getGeoPoint("position"),
                                    snapshot.getId(),
                                    (snapshot.getTimestamp("startQuiz") != null) ?snapshot.getTimestamp("startQuiz").toDate():null,
                                    (snapshot.getTimestamp("endQuiz") != null) ?snapshot.getTimestamp("endQuiz").toDate():null);
                            quizList.add(quiz);
                        }
                        sortList(quizList);
                        QuizMGR.getInstance().setQuizList(quizList);
                        Log.i(TAG, "quizList retrieved");
                    }
                    else{
                        Log.e(TAG, "quizList could not be retrieved");
                    }
                }
                QuizMGR.getInstance().setWaitingForListOfQuizDone(false);
            }
        });
        return quizList;
    }
    public void pushAnswersForQuiz(String quizName, ArrayList choices ){
        Team tmpTeam = SessionMGR.getInstance().getLogedTeam();
        Map<String,Object> toPush = new HashMap<>();
        toPush.put("endQuiz", FieldValue.serverTimestamp());
        toPush.put("choices", choices);
        teamCollections.document(tmpTeam.getName()).collection("Answers").document(quizName).update(toPush);
        QuizMGR.getInstance().setCurrentQuiz("");
        setCurrentQuizForTeamLogged("");
    }

    public void setCurrentQuizForTeamLogged(String quizName){
        Team tmpTeam = SessionMGR.getInstance().getLogedTeam();
        Map<String,Object> toPush = new HashMap<>();
        toPush.put("currentQuiz", quizName);
        teamCollections.document(tmpTeam.getName()).update(toPush);
    }




    public void setStartRallye(){
        final Team tmpTeam = SessionMGR.getInstance().getLogedTeam();

        // we check if the time begin is not seted up allredy
        final DocumentReference teamRef = teamCollections.document(tmpTeam.getName());
        teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.getData()!=null) {
                        Log.d(TAG, "onComplete: StartRallye" + doc.getData());
                        if(!doc.getData().containsKey("startRallye")){
                            Map<String,Object> toPush = new HashMap<>();
                            toPush.put("startRallye", FieldValue.serverTimestamp());
                            teamCollections.document(tmpTeam.getName()).update(toPush);
                            SessionMGR.getInstance().updateCurrentTeam();
                        }
                    }
                }
            }
        });

    }
    public void setEndtRallye(){
        final Team tmpTeam = SessionMGR.getInstance().getLogedTeam();

        // we check if the time begin is not seted up allredy
        final DocumentReference teamRef = teamCollections.document(tmpTeam.getName());
        teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.getData()!=null) {
                        Log.i(TAG, "onComplete: EndRallye");
                        if(doc.getData().containsKey("startRallye") && !doc.getData().containsKey("endRallye") ){
                            Map<String,Object> toPush = new HashMap<>();
                            toPush.put("endRallye", FieldValue.serverTimestamp());
                            teamCollections.document(tmpTeam.getName()).update(toPush);
                            SessionMGR.getInstance().updateCurrentTeam();
                        }
                    }
                }
            }
        });
    }

    public void getFinishedQuizzesForTeamLogged(){
        final Team team = SessionMGR.getInstance().getLogedTeam();
        final ArrayList<Quiz> finishedQuizList = new ArrayList<>();
        teamCollections.document(team.getName()).collection("Answers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult() != null){
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Quiz quiz = new Quiz(null,
                                    null,
                                    snapshot.getId(),
                                    (snapshot.getTimestamp("startQuiz") != null) ?snapshot.getTimestamp("startQuiz").toDate():null,
                                    (snapshot.getTimestamp("endQuiz") != null) ?snapshot.getTimestamp("endQuiz").toDate():null);
                            finishedQuizList.add(quiz);
                        }
                        sortList(finishedQuizList);
                        QuizMGR.getInstance().setFinishedQuizList(finishedQuizList);
                    }
                }
                QuizMGR.getInstance().setWaitingForListOfFinishedQuizDone(false);
            }
        });
    }


    private void sortList(ArrayList<Quiz> list) {
        Collections.sort(list, new Comparator<Quiz>() {
            public int compare(Quiz o1, Quiz o2) {
                return extractInt(o1.getId()) - extractInt(o2.getId());
            }

            int extractInt(String s) {
                String num = s.replaceAll("Quiz", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
    }

    public String newToken() {
        String token = UUID.randomUUID().toString();
        Team tmpTeam = SessionMGR.getInstance().getLogedTeam();
        Map<String,Object> toPush = new HashMap<>();
        toPush.put("token", token);
        teamCollections.document(tmpTeam.getName()).update(toPush);
        return token;
    }

    public void getListOfOpponentPosition() {
        teamCollections.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult() != null) {
                        ArrayList<Team> opponentTeamList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (doc.getGeoPoint("position") != null && !doc.getId().equals(SessionMGR.getInstance().getLogedTeam().getName())) {
                                Team team = new Team(doc.getId(), 0, doc.getGeoPoint("position"), new Color(), "", null, null, "", "");
                                opponentTeamList.add(team);
                            }
                        }
                        QuizMGR.getInstance().setListOfOpponentPosition(opponentTeamList);
                    }
                }
                QuizMGR.getInstance().setWaitingForListOfOpponentPosition(false);
            }
        });
    }

    public void pushTeamPosition(){
        Map<String,Object> toPush = new HashMap<>();
        Team tmpTeam = SessionMGR.getInstance().getLogedTeam();
        toPush.put("position", tmpTeam.getPosition());
        teamCollections.document(tmpTeam.getName()).update(toPush);
    }
}
