package com.ai.soiltester.ui.results;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ai.soiltester.R;
import com.ai.soiltester.databinding.ActivityResultsBinding;
import com.ai.soiltester.models.Crop;
import com.ai.soiltester.models.SensorReading;
import com.ai.soiltester.models.TestResult;
import com.ai.soiltester.ui.home.HomeActivity;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private ActivityResultsBinding binding;
    private TestResult testResult;
    private CropRecommendationsAdapter cropAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get test result from intent
        testResult = getIntent().getParcelableExtra("test_result");

        if (testResult == null) {
            // Show empty state
            showEmptyState();
            return;
        }

        // Setup UI
        setupUI();
        displayResults();
    }

    private void setupUI() {
        // Set title
        binding.toolbar.setTitle(getString(R.string.test_results));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup RecyclerView for crops
        cropAdapter = new CropRecommendationsAdapter();
        binding.recyclerViewCrops.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCrops.setAdapter(cropAdapter);

        // Setup click listeners
        binding.buttonShareResults.setOnClickListener(v -> shareResults());
        binding.buttonBackToHome.setOnClickListener(v -> navigateToHome());
    }

    private void displayResults() {
        // Display sensor readings
        displaySensorReadings(testResult.getReading());

        // Display crop recommendations
        List<Crop> crops = testResult.getRecommendedCrops();
        if (crops != null && !crops.isEmpty()) {
            cropAdapter.updateCrops(crops);
            binding.textNoCrops.setVisibility(View.GONE);
            binding.recyclerViewCrops.setVisibility(View.VISIBLE);
        } else {
            binding.textNoCrops.setVisibility(View.VISIBLE);
            binding.recyclerViewCrops.setVisibility(View.GONE);
        }

        // Display timestamp
        binding.textTimestamp.setText("Test completed: " + testResult.getFormattedTimestamp());
    }

    private void displaySensorReadings(SensorReading reading) {
        if (reading == null) {
            return;
        }

        binding.textMoistureValue.setText(reading.getMoistureText());
        binding.textTemperatureValue.setText(reading.getTemperatureText());
        binding.textHumidityValue.setText(reading.getHumidityText());

        // Update progress bars
        binding.progressBarMoisture.setProgress((int) reading.getMoisture());
        binding.progressBarTemperature.setProgress((int) Math.max(0, Math.min(100, (reading.getTemperature() + 20) * 100 / 80)));
        binding.progressBarHumidity.setProgress((int) reading.getHumidity());

        // Show sensor readings section
        binding.layoutSensorReadings.setVisibility(View.VISIBLE);
    }

    private void showEmptyState() {
        binding.layoutMainContent.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.VISIBLE);

        binding.buttonBackToHomeEmpty.setOnClickListener(v -> navigateToHome());
    }

    private void shareResults() {
        if (testResult == null) {
            return;
        }

        String shareText = testResult.getShareableText();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AI Soil Tester Results");

        startActivity(Intent.createChooser(shareIntent, "Share Soil Test Results"));
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}