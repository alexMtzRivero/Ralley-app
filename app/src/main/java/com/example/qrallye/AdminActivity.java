package com.example.qrallye;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private final static String TAG = "AdminActivity";
    private ListView list;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        LinearLayout iv_back = findViewById(R.id.back_arrow);
        list = findViewById(R.id.adminList);
        progressBar = findViewById(R.id.progressBar);
        SessionMGR.getInstance().requestAdminList(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    public void refreshList(ArrayList<Administrators> adminList){
        progressBar.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        AdminAdapter adapter = new AdminAdapter(getApplicationContext(),adminList);
        list.setAdapter(adapter);
    }
}
