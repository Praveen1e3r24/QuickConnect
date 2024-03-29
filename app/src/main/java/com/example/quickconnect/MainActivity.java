package com.example.quickconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickconnect.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    Button button, smsRedirect;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        auth=FirebaseAuth.getInstance();
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("x-goog-api-key", System.getenv("PALM_API_KEY"));
////
//        TransportChannelProvider provider = InstantiatingGrpcChannelProvider.newBuilder()
//                .setHeaderProvider(FixedHeaderProvider.create(headers))
//                .build();
//
//        TextServiceSettings settings = TextServiceSettings.newBuilder()
//                .setTransportChannelProvider(provider)
//                .setCredentialsProvider(FixedCredentialsProvider.create(null))
//                .build();
//
//        TextServiceClient client = TextServiceClient.create(settings);



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

        binding.QCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void CreateChat() {

    }
}