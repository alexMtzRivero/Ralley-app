package com.uga.qrallye;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ConnectionActivity extends AppCompatActivity {

    private static final String TAG = "ConnectionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        final EditText userText = findViewById(R.id.UserEdt);
        final EditText passText = findViewById(R.id.PasswordEdt);
        final Button validateBtn = findViewById(R.id.validateBtn);

        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userText.getText().length() == 0){
                    userText.startAnimation(AnimationUtils.loadAnimation(ConnectionActivity.this, R.anim.shakinganimation));
                }else if(passText.getText().length() == 0){
                    passText.startAnimation(AnimationUtils.loadAnimation(ConnectionActivity.this, R.anim.shakinganimation));
                }
                else{
                    ProgressBar loadingView = findViewById(R.id.connectionProgressBar);
                    validateBtn.setVisibility(View.GONE);
                    loadingView.setVisibility(View.VISIBLE);
                    SessionMGR.getInstance().login(userText.getText().toString().trim(), passText.getText().toString(), ConnectionActivity.this);
                }
            }
        });

    }

    public void goToNext(){
        String currentQuiz = SessionMGR.getInstance().getLogedTeam().getCurrentQuiz();
        if(currentQuiz.length() == 0){
            startActivity(new Intent(getApplicationContext(), RulesActivity.class));
            finish();
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(getResources().getString(R.string.currentQuiz), currentQuiz);
            intent.putExtra("fragmentType", MainActivity.fragmentDisplayed.Question);
            startActivity(intent);
            finish();
        }
    }
    public  void notifyWrongPasword(){
        ProgressBar loadingView = findViewById(R.id.connectionProgressBar);
        Button validateBtn = findViewById(R.id.validateBtn);
        final EditText passText = findViewById(R.id.PasswordEdt);
        passText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shakinganimation));
        validateBtn.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),getString(R.string.wrong_password),Toast.LENGTH_SHORT).show();
    }

    public  void noTeamFound(){
        if(isNetworkAvailable()){
            notifyWrongUserName();
        }else {
            ProgressBar loadingView = findViewById(R.id.connectionProgressBar);
            Button validateBtn = findViewById(R.id.validateBtn);
            validateBtn.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            Toast.makeText(this, "Connexion impossible : vérifiez votre connexion internet", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch(Exception e){
            Log.e(TAG, "isNetworkAvailable: ", e);
            return false;
        }
    }

    private void notifyWrongUserName(){
        ProgressBar loadingView = findViewById(R.id.connectionProgressBar);
        Button validateBtn = findViewById(R.id.validateBtn);
        final EditText userText = findViewById(R.id.UserEdt);
        userText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shakinganimation));
        validateBtn.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),getString(R.string.wrong_username),Toast.LENGTH_SHORT).show();
    }
}
