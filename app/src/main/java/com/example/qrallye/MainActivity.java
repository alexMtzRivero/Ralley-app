package com.example.qrallye;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Choreographer;

public class MainActivity extends AppCompatActivity implements FragmentCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QRCodeFragment qrCodeFragment = new QRCodeFragment();
        DatabaseMGR.getInstance().getAdmin();
        DatabaseMGR.getInstance().getTeam("Catsu");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,qrCodeFragment,"TAG_QRCODE").commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("TAG_QRCODE");
        if(fragment != null){
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }

}
