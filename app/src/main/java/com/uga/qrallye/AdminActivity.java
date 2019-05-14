package com.uga.qrallye;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private final static String TAG = "AdminActivity";

    private enum DisplayStatus{
        LOADING, LIST, EMPTY_LIST
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        LinearLayout iv_back = findViewById(R.id.back_arrow);
        changeDisplay(DisplayStatus.LOADING);
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
        if(adminList.size() != 0){
            AdminAdapter adapter = new AdminAdapter(getApplicationContext(),adminList);
            ListView list = findViewById(R.id.adminList);
            list.setAdapter(adapter);
            changeDisplay(DisplayStatus.LIST);
        }else{
            changeDisplay(DisplayStatus.EMPTY_LIST);
        }

    }

    private void changeDisplay(DisplayStatus s){
        ListView list = findViewById(R.id.adminList);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView emptyListMessage = findViewById(R.id.empty_list_message);
        switch(s){
            case LOADING:
                list.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                emptyListMessage.setVisibility(View.GONE);
                break;
            case LIST:
                list.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                emptyListMessage.setVisibility(View.GONE);
                break;
            case EMPTY_LIST:
                list.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                emptyListMessage.setVisibility(View.VISIBLE);
                break;
        }
    }
}
