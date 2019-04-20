package com.example.qrallye;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class LaunchScreenActivity extends AppCompatActivity {

    private static final String TAG = "LaunchScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
        if(!sp.getString(getString(R.string.teamNamePref), "").equals("")){
            SessionMGR.getInstance().login(sp.getString(getString(R.string.teamNamePref), ""), this);
        }else{
            try{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), ConnectionActivity.class));
                        finish();
                    }
                }, 1500);
            }catch (Exception e){

            }
        }

    }


    public void goToConnectionActivity() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
        sp.edit().clear().apply();
        startActivity(new Intent(getApplicationContext(), ConnectionActivity.class));
        finish();
    }

    public void goToNext() {
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
}
