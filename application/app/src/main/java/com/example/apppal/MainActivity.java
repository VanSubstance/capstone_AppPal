package com.example.apppal;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpPlayStartbutton();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Sets up the UI components for the live demo with camera input.
     */
    private void setUpPlayStartbutton() {
        Button startCameraButton = findViewById(R.id.button_start_drawing);
        startCameraButton.setOnClickListener(
                v -> {
                    Intent playIntent = new Intent(MainActivity.this, PlayArActivity.class);
                    startActivity(playIntent);
                    finish();
                });
    }
}

