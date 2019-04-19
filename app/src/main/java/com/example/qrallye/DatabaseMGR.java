package com.example.qrallye;

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
        final ArrayList<Administrators> adminList = new ArrayList<>();
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
                    adminList.add(administrators);
                }
                SessionMGR.getInstance().setAdminList(adminList);
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

    public void getQuestionsFromQuiz(final String quizName){

        QuizMGR.getInstance().setCurrentQuiz(quizName);

        CollectionReference questionsRef = quizzesCollections.document(quizName).collection("Questions");

        questionsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e(TAG, "onComplete: starting : ZRSQBGWFBHDTEHBWTDEFHBDGEWTSNBGDWSTENB");
                if(task.isSuccessful())
                {
                    Log.e(TAG, "onComplete: task.successful : ZRSQBGWFBHDTEHBWTDEFHBDGEWTSNBGDWSTENB");
                    if(task.getResult() != null && task.getResult().size() != 0){
                        Log.d(TAG, "onComplete: getQuestionsFromQuiz "+ task.getResult().toString());
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.d(TAG, "onComplete: snapshot "+ snapshot.getData());
                            Question tmp = snapshot.toObject(Question.class);
                            Log.d(TAG, "onComplete: question "+tmp.getQuestion());
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
                    }
                    else {
                        Log.e(TAG, "onComplete: ERREUR RECUPERATION QUESTIONS");
                    }
                }
                else
                {
                    Log.e(TAG, "onComplete: FAILED GET");
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
                        //QuizMGR.getInstance().setQuizList(quizList);
                        Log.d(TAG, "onComplete: quizList "+ quizList);
                    }
                    else{
                        Log.d(TAG, "onComplete: getListOfQuiz = ERROR");
                    }
                }
                QuizMGR.getInstance().setWaitingForListOfQuiz();
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
                        Log.d(TAG, "onComplete: " + doc.getData());
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
                        Log.d(TAG, "onComplete: " + doc.getData());
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

    public void getQuizFinishForTeamLogged(){
        final Team team = SessionMGR.getInstance().getLogedTeam();
        final ArrayList<Quiz> quizList = new ArrayList<>();
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
                            quizList.add(quiz);
                        }
                        sortList(quizList);
                        Log.d(TAG, "onComplete: list "+ quizList);
                        QuizMGR.getInstance().setQuizDoneList(quizList);
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
