package com.example.qrallye;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(getString(R.string.sharedPreferencesFile), MODE_PRIVATE);
                sp.edit().clear().apply();
                Intent intent = new Intent(HomeActivity.this, ConnectionActivity.class);
                startActivity(intent);
                finish();
            }
        });


        findViewById(R.id.homeMapBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("fragmentType", MainActivity.fragmentDisplayed.Map);
                startActivity(intent);
            }
        });
        findViewById(R.id.homeScanBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("fragmentType", MainActivity.fragmentDisplayed.Scan);
                startActivity(intent);
            }
        });
        findViewById(R.id.homeProgressBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("fragmentType", MainActivity.fragmentDisplayed.Progress);
                startActivity(intent);
            }
        });
        findViewById(R.id.homeQuizzBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("fragmentType", MainActivity.fragmentDisplayed.Quiz);
                startActivity(intent);
            }
        });
        findViewById(R.id.homeCallBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.homeWebBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
            }
        });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
}
