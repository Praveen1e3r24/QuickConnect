package com.example.quickconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button, smsRedirect;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();
        button=findViewById(R.id.logout);
//        textView=findViewById(R.id.user_details);
        user=auth.getCurrentUser();

        if(user == null){
            Intent intent=new Intent(getApplicationContext(),Login.class);
            startActivity(intent);
            finish();
        }
        else {
//            textView.setText("Welcome "+user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              FirebaseAuth.getInstance().signOut();
              Intent intent=new Intent(getApplicationContext(),Login.class);
              startActivity(intent);
              finish();
            }
        });

        smsRedirect = findViewById(R.id.smsRedirect);
        smsRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(i);
            }
        });
    }
}