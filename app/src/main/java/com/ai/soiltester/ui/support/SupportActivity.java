package com.ai.soiltester.ui.support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ai.soiltester.R;
import com.ai.soiltester.databinding.ActivitySupportBinding;
import com.ai.soiltester.ui.home.HomeActivity;

public class SupportActivity extends AppCompatActivity {

    private ActivitySupportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup UI
        setupUI();

        // Setup click listeners
        setupClickListeners();
    }

    private void setupUI() {
        // Set title
        binding.toolbar.setTitle(getString(R.string.support));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set contact information
        binding.textPhoneNumber.setText(R.string.phone_number);
        binding.textEmailAddress.setText(R.string.email_address);
    }

    private void setupClickListeners() {
        // Phone call
        binding.layoutPhone.setOnClickListener(v -> makePhoneCall());

        // Email
        binding.layoutEmail.setOnClickListener(v -> sendEmail());

        // Share app
        binding.buttonShareApp.setOnClickListener(v -> shareApp());

        // Rate app (placeholder)
        binding.buttonRateApp.setOnClickListener(v -> showRateAppDialog());

        // About
        binding.buttonAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + getString(R.string.phone_number)));
        startActivity(intent);
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + getString(R.string.email_address)));
        intent.putExtra(Intent.EXTRA_SUBJECT, "AI Soil Tester Support Request");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out AI Soil Tester - a smart app for soil testing and crop recommendations! 🌱");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AI Soil Tester");
        startActivity(Intent.createChooser(shareIntent, "Share AI Soil Tester"));
    }

    private void showRateAppDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Rate App")
                .setMessage("Thank you for using AI Soil Tester! Rate us on the Play Store to help us improve.")
                .setPositiveButton("Rate Now", (dialog, which) -> {
                    // Placeholder for Play Store link
                    openPlayStore();
                })
                .setNegativeButton("Maybe Later", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About AI Soil Tester")
                .setMessage(getString(R.string.app_description) + "\n\nVersion: 1.0\n\nContact: " +
                          getString(R.string.email_address) + "\nPhone: " + getString(R.string.phone_number))
                .setPositiveButton("OK", null)
                .show();
    }

    private void openPlayStore() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ai.soiltester"));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ai.soiltester"));
            startActivity(intent);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}