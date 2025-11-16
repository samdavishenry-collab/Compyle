package com.ai.soiltester;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ai.soiltester.ui.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make activity fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        // Set splash screen layout
        setContentView(R.layout.activity_splash);

        // Initialize splash screen
        initSplashScreen();

        // Navigate to HomeActivity after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToHome();
            }
        }, SPLASH_DELAY);
    }

    private void initSplashScreen() {
        ImageView splashLogo = findViewById(R.id.splash_logo);
        if (splashLogo != null) {
            splashLogo.setImageResource(R.drawable.ic_app_logo);
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Finish MainActivity so user can't go back to splash
    }

    @Override
    public void onBackPressed() {
        // Disable back button on splash screen
        super.onBackPressed();
    }
}