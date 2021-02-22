package com.example.social.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.social.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button mRegisterBtn, mLoginBtn;
    SharedPreferences sp;
            //
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        editor = sp.edit();
        String myUID = sp.getString("Current_USERID", "");
        if (myUID!="")
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
        }
        else{
            mRegisterBtn = findViewById(R.id.register_btn);
            mLoginBtn = findViewById(R.id.login_btn);
            mRegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                }
            });
            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }
            });
        }
    }
}