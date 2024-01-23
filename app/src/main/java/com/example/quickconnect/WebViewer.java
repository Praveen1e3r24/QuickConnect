package com.example.quickconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.quickconnect.databinding.ActivityWebViewerBinding;

public class WebViewer extends AppCompatActivity {

    ActivityWebViewerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewer);
        binding = ActivityWebViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        binding.webview.loadUrl(url);

        binding.webviewBack.setOnClickListener(v -> {
            finish();
        });
    }
}