package com.capstone.apppal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class OnBoardingActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_onboarding);

    Button loginButton = findViewById(R.id.login_button);
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // 여기에서 작업하면 될듯
        Intent drawingIntent = new Intent(OnBoardingActivity.this, DrawARActivity.class);
        startActivity(drawingIntent);
        finish();
      }
    });
  }
}
