package com.example.qrallye;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements TimerFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QRCodeFragment qrCodeFragment = new QRCodeFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,qrCodeFragment).commit();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        
    }
}
