package com.example.qrallye;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        Log.d(TAG, "getAdmin: Création");
        DocumentReference adminDocument = adminCollections.document();
        Log.d(TAG, "getAdmin: admin =" +adminDocument.toString());
        adminCollections.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: snapshot " + queryDocumentSnapshots.toString());
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Administrators administrators = queryDocumentSnapshot.toObject(Administrators.class);
                    Log.d(TAG, "onSuccess: " + administrators.getUsername());
                }
            }
        });
    }

    public void getTeam(final String teamName){
        team = null;
        Log.d(TAG, "getTeam: Recherche de la team");
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
                                doc.getGeoPoint("pos"),
                                null,
                                doc.getString("color"),
                                (doc.getTimestamp("startRallye") != null) ?doc.getTimestamp("startRallye").toDate():null,
                                (doc.getTimestamp("endRallye") != null) ?doc.getTimestamp("endRallye").toDate():null,
                                (doc.getString("currentQuiz") != null) ?doc.getString("currentQuiz"):""
                        );
                        Log.d(TAG, "onComplete: team " + team.getStartTimer() );
                        SessionMGR.getInstance().onTeamFound(team);

                    }
                    else SessionMGR.getInstance().onTeamFound(null);
                }
                else{
                    SessionMGR.getInstance().onTeamFound(null);
                    Log.d(TAG, "onComplete: ERROR");
                }
            }
        });
    }

    public void getQuestionsFromQuiz(String quizName){

        Team tmpTeam = SessionMGR.getInstance().getLogedTeam();
        QuizMGR.getInstance().setCurrentQuiz(quizName);
        Map<String,Object> toPush = new HashMap<>();
        toPush.put("startQuiz", FieldValue.serverTimestamp());
        teamCollections.document(tmpTeam.getName()).collection("Answers").document(quizName).set(toPush);

        CollectionReference questionsRef = quizzesCollections.document(quizName).collection("Questions");

        questionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult() != null){
                        Log.d(TAG, "onComplete: getQuestionsFromQuiz "+ task.getResult().toString());
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.d(TAG, "onComplete: snapshot "+ snapshot.getData());
                            Question tmp = snapshot.toObject(Question.class);
                            Log.d(TAG, "onComplete: question "+tmp.getQuestion());
                            QuizMGR.getInstance().addQuestion(tmp);
                        }
                        QuizMGR.getInstance().onQuestionListRetrieved();

                    }
                    else {
                        Log.d(TAG, "onComplete: ERREUR RECUPERATION QUESTIONS");
                    }
                }
                else
                {
                    Log.d(TAG, "onComplete: FAILED GET");
                }
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
                            Quiz quiz = new Quiz(snapshot.get("nomQuiz").toString(),snapshot.getGeoPoint("position"),snapshot.getId());
                            quizList.add(quiz);
                        }
                        sortList(quizList);
                        Log.d(TAG, "onComplete: quizList "+ quizList);
                    }
                    else{
                        Log.d(TAG, "onComplete: getListOfQuiz = ERROR");
                    }
                }
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
                        Log.d(TAG, "onComplete: " + doc.getData());
                        if(!doc.getData().containsKey("startRallye")){
                            Map<String,Object> toPush = new HashMap<>();
                            toPush.put("startRallye", FieldValue.serverTimestamp());
                            teamCollections.document(tmpTeam.getName()).update(toPush);
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
                        Log.d(TAG, "onComplete: " + doc.getData());
                        if(doc.getData().containsKey("startRallye") && !doc.getData().containsKey("endRallye") ){
                            Map<String,Object> toPush = new HashMap<>();
                            toPush.put("endRallye", FieldValue.serverTimestamp());
                            teamCollections.document(tmpTeam.getName()).update(toPush);
                        }
                    }
                }
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

}
