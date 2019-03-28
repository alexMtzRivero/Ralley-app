package com.example.qrallye;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.PatternMatcher;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrallye.databinding.NavigationBarBinding;
import com.google.api.LogDescriptor;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements FragmentCallback {

    private final String TAG = "MainActivity";
    private boolean isChronoRunning = false;
    public enum fragmentDisplayed{
        Map, Scan, Progress, Quizz
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startChrono(false);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        QuizMGR.getInstance().setQuizList(DatabaseMGR.getInstance().getListOfQuiz());


        final NavigationBarBinding binding = DataBindingUtil.bind((findViewById(R.id.navbar)));

        if (bundle != null && bundle.get("fragmentType") != null){
            switch ((fragmentDisplayed)bundle.get("fragmentType")){
                case Map:
                    binding.setSelected((ImageView) findViewById(R.id.navMap));
                    changeFragmentDisplayed(new MapFragment());
                    break;
                case Scan:
                    binding.setSelected((ImageView) findViewById(R.id.navScan));
                    changeFragmentDisplayed(new QRCodeFragment(),"TAG_QRCODE");
                    break;
                case Quizz:
                    binding.setSelected((ImageView) findViewById(R.id.navQuizz));
                    changeFragmentDisplayed(new QuizzFragment());
                    break;
                case Progress:
                    binding.setSelected((ImageView) findViewById(R.id.navProgress));
                    changeFragmentDisplayed(new MapFragment());
                    break;
            }
        }else{
            binding.setSelected((ImageView) findViewById(R.id.navMap));
            changeFragmentDisplayed(new MapFragment());
        }


        View.OnClickListener navItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.navMap:
                        changeFragmentDisplayed(new MapFragment());
                        view.setBackgroundColor(getResources().getColor(R.color.navItemSelected));
                        binding.setSelected((ImageView) view);
                        break;
                    case R.id.navScan:
                        changeFragmentDisplayed(new QRCodeFragment(),"TAG_QRCODE");
                        view.setBackgroundColor(getResources().getColor(R.color.navItemSelected));
                        binding.setSelected((ImageView) view);
                        break;
                    case R.id.navQuizz:
                        changeFragmentDisplayed(new QuizzFragment());
                        view.setBackgroundColor(getResources().getColor(R.color.navItemSelected));
                        binding.setSelected((ImageView) view);
                        break;
                    case R.id.navProgress:
                        changeFragmentDisplayed(new ProgressFragment());
                        view.setBackgroundColor(getResources().getColor(R.color.navItemSelected));
                        binding.setSelected((ImageView) view);
                        break;
                    case R.id.navHome:
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        break;
                    default:
                        break;
                }
            }
        };

        findViewById(R.id.navMap).setOnClickListener(navItemClickListener);
        findViewById(R.id.navScan).setOnClickListener(navItemClickListener);
        findViewById(R.id.navQuizz).setOnClickListener(navItemClickListener);
        findViewById(R.id.navProgress).setOnClickListener(navItemClickListener);
        findViewById(R.id.navHome).setOnClickListener(navItemClickListener);
    }

    private void changeFragmentDisplayed(Fragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit();
    }

    private void changeFragmentDisplayed(Fragment f, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f , tag)
                .commit();
    }

    @Override
    public void onQuizzFinish() {
        findViewById(R.id.navMap).performClick();
    }

    @Override
    public void showScan() {
        findViewById(R.id.navScan).performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle args = new Bundle();
        String stringResult = result.getContents();

        if(stringResult.contains("Quiz")){
            args.putString("key", stringResult);
            questionFragment.setArguments(args);
            changeFragmentDisplayed(questionFragment);
        }
        else if(stringResult.equals("startRace")){
            DatabaseMGR.getInstance().setStartRallye();
            changeFragmentDisplayed(new MapFragment());
            Toast.makeText(getApplicationContext(),"start",Toast.LENGTH_SHORT).show();
            startChrono(true);
        }
        else if(stringResult.equals("endRace")){
            DatabaseMGR.getInstance().setEndtRallye();
            changeFragmentDisplayed(new MapFragment());
            stopChrono();
            Toast.makeText(getApplicationContext(),"end",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Non valid QR",Toast.LENGTH_SHORT).show();
        }

    }
    public  void stopChrono(){
        Chronometer chrono = findViewById(R.id.timer);
        chrono.stop();

    }

    public  void startChrono(boolean scaned){

        //check shared references
        String startTimeKey = "startTime";
        SharedPreferences sp = getSharedPreferences("myPreferences", MODE_PRIVATE);
        Date timeStart = SessionMGR.getInstance().getLogedTeam().getStartTimer();
        Chronometer chrono = findViewById(R.id.timer);
        Date timeEnd = SessionMGR.getInstance().getLogedTeam().getEndTimer();

        // if the chorno is alredy finished
        if(timeEnd!= null){
            long timelaps = timeEnd.getTime() - timeStart.getTime();
            chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
            Log.e("time","cerrando timer");
        }
        else {
            // if we have shared preferences
            if (sp.contains(startTimeKey)) {
                Log.e("time","comenzando de shared preferences");
                long timelaps = Calendar.getInstance().getTimeInMillis() - sp.getLong(startTimeKey, 0);
                chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
                isChronoRunning = true;
                chrono.start();

            } else {
                if (timeStart != null) {
                    Log.e("time","comenzando de firebase");
                    // insert the time in shared preferences
                    sp.edit().putLong(startTimeKey, timeStart.getTime()).apply();
                    long timelaps = Calendar.getInstance().getTimeInMillis() - sp.getLong(startTimeKey, 0);
                    chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
                    isChronoRunning = true;
                    chrono.start();
                }
                else{
                    if(scaned) {
                        Log.e("time", "comenzando de 0");
                        long timelaps = 0;
                        chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
                        isChronoRunning = true;
                        chrono.start();
                    }
                }
            }
        }
    }


}
