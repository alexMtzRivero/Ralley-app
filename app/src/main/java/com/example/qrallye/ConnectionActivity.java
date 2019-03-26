package com.example.qrallye;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText userText = findViewById(R.id.UserEdt);
        final EditText passText = findViewById(R.id.PasswordEdt);
        Button validateBtn = findViewById(R.id.validateBtn);



        validateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SessionMGR.getInstance().login(userText.getText().toString(),passText.getText().toString())){
                    goToNext();
                }
            }
        });
    }
    private void goToNext(){
        startActivity(new Intent(getApplicationContext(), RulesActivity.class));
    }
}
