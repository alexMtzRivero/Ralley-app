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

import java.util.ArrayList;
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
                        GeoPoint pos = doc.getGeoPoint("pos");
                        Location position = new Location("");
                        position.setLongitude(pos.getLongitude());
                        position.setLatitude(pos.getLatitude());
                        team = new Team(teamName,
                                (long) doc.get("password"),
                                position,
                                null,
                                doc.getString("color"),
                                null, null
                        );
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

    // FONCTION POUR LA DATE : FieldValue.serverTimestamp()

    public void getQuestionsFromQuiz(String quizName){

        Team tmpTeam = SessionMGR.getInstance().getLogedTeam();
        QuizMGR.getInstance().setCurrentQuiz(quizName);
        getListOfQuiz();
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
                    }
                    else {
                        Log.d(TAG, "onComplete: ERREUR RECUPERATION QUESTIONS");
                    }
                }
                else
                {

                }
            }
        });
    }

    public ArrayList<String> getListOfQuiz(){
        final ArrayList<String> quizList = new ArrayList<>();
        quizzesCollections.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult() != null){
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            quizList.add(snapshot.getId());
                        }
                        Log.d(TAG, "onComplete: list "+ quizList);
                    }
                    else{
                        Log.d(TAG, "onComplete: getListOfQuiz = ERROR");
                    }
                }
            }
        });

        return quizList;
    }
}
