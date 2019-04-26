package com.example.qrallye;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrallye.databinding.NavigationBarBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements FragmentCallback, ServiceCallbacks {

    private final String TAG = "MainActivity";
    private boolean isChronoRunning = false;
    private DBInteractionsService dbInteractionsService;

    public enum fragmentDisplayed {
        Map, Scan, Progress, Quiz, Question
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Handler serviceCallsHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.navbar).setVisibility(View.VISIBLE);

        serviceCallsHandler = new Handler();
        Intent dbInteractionsServiceIntent = new Intent(this, DBInteractionsService.class);
        dbInteractionsServiceIntent.setAction(DBInteractionsService.ACTION_getQuizzes);
        startService(dbInteractionsServiceIntent);
        bindService(dbInteractionsServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        dbInteractionsServiceIntent.setAction(DBInteractionsService.ACTION_getOpponentsPosition);
        startService(dbInteractionsServiceIntent);

        final NavigationBarBinding binding = DataBindingUtil.bind((findViewById(R.id.navbar)));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.get("fragmentType") != null) {
            switch ((fragmentDisplayed) bundle.get("fragmentType")) {
                case Map:
                    binding.setSelected((ImageView) findViewById(R.id.navMap));
                    changeFragmentDisplayed(new MapFragment());
                    break;
                case Scan:
                    binding.setSelected((ImageView) findViewById(R.id.navScan));
                    changeFragmentDisplayed(new QRCodeFragment(), "TAG_QRCODE");
                    break;
                case Quiz:
                    binding.setSelected((ImageView) findViewById(R.id.navQuizz));
                    changeFragmentDisplayed(new QuizzFragment());
                    break;
                case Progress:
                    binding.setSelected((ImageView) findViewById(R.id.navProgress));
                    changeFragmentDisplayed(new ProgressFragment());
                    break;
                case Question:
                    binding.setSelected((ImageView) findViewById(R.id.navQuizz));
                    QuestionFragment questionFragment = new QuestionFragment();
                    Bundle args = new Bundle();
                    args.putString(getResources().getString(R.string.currentQuiz), bundle.getString(getResources().getString(R.string.currentQuiz)));
                    questionFragment.setArguments(args);
                    changeFragmentDisplayed(questionFragment, fragmentDisplayed.Question.toString());
                    findViewById(R.id.navbar).setVisibility(View.GONE);
                    break;
            }
        } else {
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
                        changeFragmentDisplayed(new QRCodeFragment(), "TAG_QRCODE");
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
                        finish();
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


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    5);

        } else {
            initializeGeoLocalisation();
            Intent geoServiceIntent = new Intent(this, GeolocalisationService.class);
            startService(geoServiceIntent);
        }
    }

    /**
     * On service connection with activity
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBInteractionsService.LocalBinder binder = (DBInteractionsService.LocalBinder) service;
            dbInteractionsService = binder.getService();
            dbInteractionsService.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    public void opponentsPositionsRetrieved() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.getClass().equals(MapFragment.class)) {
            MapFragment mapFragment = (MapFragment) fragment;
            mapFragment.opponentsPositionRetrieved();
            serviceCallsHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Intent dbInteractionsServiceIntent = new Intent(MainActivity.this, DBInteractionsService.class);
                        dbInteractionsServiceIntent.setAction(DBInteractionsService.ACTION_getOpponentsPosition);
                        startService(dbInteractionsServiceIntent);
                    } catch (Exception e) {
                        Log.e(TAG, "asynchronous retrieve of opponent position: ", e);
                    }
                }
            }, 10000);
        }
    }

    @Override
    public void quizzesRetrieved() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment != null){
            if (fragment.getClass().equals(MapFragment.class)){
                MapFragment mapFragment = (MapFragment) fragment;
                mapFragment.quizzesRetrieved();
            }else if(fragment.getClass().equals(QuizzFragment.class)){
                QuizzFragment quizzFragment = (QuizzFragment) fragment;
                quizzFragment.quizzesRetrieved();
            }
        }
    }

    @Override
    public void finishedQuizzesRetrieved() {

    }

    @Override
    protected void onDestroy() {
        try{
            serviceCallsHandler.removeCallbacksAndMessages(null);
            unbindService(serviceConnection);
        }catch(Exception e){
            Log.e(TAG, "onDestroy: ", e);
        }
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void initializeGeoLocalisation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            SessionMGR.getInstance().updatePosition(new GeoPoint(location.getLatitude(), location.getLongitude()));
                        }
                    }
                });
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                GeoPoint position = new GeoPoint(location.getLatitude(), location.getLongitude());
                SessionMGR.getInstance().updatePosition(position);
            }
        };

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 5) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeGeoLocalisation();
                Intent serviceIntent = new Intent(this, GeolocalisationService.class);
                startService(serviceIntent);
            }
            return;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        try{
            startChrono(false);
        }catch(Exception e){
            Log.e(TAG, "onResume: ", e);
        }
    }

    private void changeFragmentDisplayed(Fragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, f)
                .addToBackStack(null)
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
        findViewById(R.id.navbar).setVisibility(View.VISIBLE);
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
        if(stringResult!= null) {
            if (stringResult.contains("Quiz")) {
                args.putString(getResources().getString(R.string.currentQuiz), stringResult);
                questionFragment.setArguments(args);
                changeFragmentDisplayed(questionFragment, fragmentDisplayed.Question.toString());
                findViewById(R.id.navbar).setVisibility(View.GONE);
            } else if (stringResult.equals("startRace")) {
                DatabaseMGR.getInstance().setStartRallye();
                changeFragmentDisplayed(new MapFragment());
                Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
                startChrono(true);
            } else if (stringResult.equals("endRace")) {
                DatabaseMGR.getInstance().setEndtRallye();
                changeFragmentDisplayed(new MapFragment());
                stopChrono();
                Toast.makeText(getApplicationContext(), "end", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Non valid QR", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public  void stopChrono(){
        Chronometer chrono = findViewById(R.id.timer);
        chrono.stop();
        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
        sp.edit().putLong("endDate", Calendar.getInstance().getTimeInMillis()).apply();
    }

    public void startChrono(boolean scaned){

        //check shared references
        String startTimeKey = "startTime";
        SharedPreferences sp = getSharedPreferences(getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
        Date timeStart = SessionMGR.getInstance().getLogedTeam().getStartTimer();
        Chronometer chrono = findViewById(R.id.timer);
        Date timeEnd = SessionMGR.getInstance().getLogedTeam().getEndTimer();

        // if the chorno is alredy finished
        if(timeEnd!= null || sp.getLong("endDate", 0) != 0){
            long timelaps;
            if (timeEnd != null){
                timelaps = timeEnd.getTime() - timeStart.getTime();

            }else{
                timelaps = sp.getLong("endDate", 0) - timeStart.getTime();
            }
            chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
            Log.e("time","cerrando timer");
        }
        else {
            // if we have shared preferences
            if (sp.contains(startTimeKey)) {
                long timelaps = Calendar.getInstance().getTimeInMillis() - sp.getLong(startTimeKey, 0);
                chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
                isChronoRunning = true;
                chrono.start();

            } else {
                if (timeStart != null) {
                    // insert the time in shared preferences
                    sp.edit().putLong(startTimeKey, timeStart.getTime()).apply();
                    long timelaps = Calendar.getInstance().getTimeInMillis() - sp.getLong(startTimeKey, 0);
                    chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
                    isChronoRunning = true;
                    chrono.start();
                }
                else{
                    if(scaned) {
                        long timelaps = 0;
                        chrono.setBase(SystemClock.elapsedRealtime() - timelaps);
                        isChronoRunning = true;
                        chrono.start();
                        SessionMGR.getInstance().getLogedTeam().setStartTimer(Calendar.getInstance().getTime());
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentDisplayed.Question.toString());
        if(fragment == null){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }
}
