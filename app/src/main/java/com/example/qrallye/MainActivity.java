package com.example.qrallye;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.qrallye.databinding.NavigationBarBinding;

public class MainActivity extends AppCompatActivity implements FragmentCallback {

    public enum fragmentDisplayed{
        Map, Scan, Progress, Quizz
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        final NavigationBarBinding binding = DataBindingUtil.bind((findViewById(R.id.navbar)));

        if (bundle != null && bundle.get("fragmentType") != null){
            switch ((fragmentDisplayed)bundle.get("fragmentType")){
                case Map:
                    binding.setSelected((ImageView) findViewById(R.id.navMap));
                    changeFragmentDisplayed(new MapFragment());
                    break;
                case Scan:
                    binding.setSelected((ImageView) findViewById(R.id.navScan));
                    changeFragmentDisplayed(new QRCodeFragment());
                    break;
                case Progress:
                    binding.setSelected((ImageView) findViewById(R.id.navProgress));
                    changeFragmentDisplayed(new MapFragment());
                    break;
                case Quizz:
                    binding.setSelected((ImageView) findViewById(R.id.navQuizz));
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
                        changeFragmentDisplayed(new QRCodeFragment());
                        view.setBackgroundColor(getResources().getColor(R.color.navItemSelected));
                        binding.setSelected((ImageView) view);
                        break;
                    case R.id.navQuizz:
                        //changeFragmentDisplayed(new UpgradesFragment());
                        view.setBackgroundColor(getResources().getColor(R.color.navItemSelected));
                        binding.setSelected((ImageView) view);
                        break;
                    case R.id.navProgress:
                        //changeFragmentDisplayed(new UpgradesFragment());
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
