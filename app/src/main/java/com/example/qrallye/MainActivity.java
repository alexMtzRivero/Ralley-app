package com.example.qrallye;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrallye.databinding.NavigationBarBinding;
import com.google.api.LogDescriptor;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements FragmentCallback {

    private final String TAG = "MainActivity";
    public enum fragmentDisplayed{
        Map, Scan, Progress, Quizz
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLocationClick(int location) {
        changeFragmentDisplayed(new QuestionFragment());
    }

    @Override
    public void onQuizzFinish() {
        changeFragmentDisplayed(new MapFragment());
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
        }
        else if(stringResult.equals("endRace")){
            DatabaseMGR.getInstance().setEndtRallye();
            changeFragmentDisplayed(new MapFragment());

            Toast.makeText(getApplicationContext(),"end",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Non valid QR",Toast.LENGTH_SHORT).show();
        }

    }

}
