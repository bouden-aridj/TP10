package com.example.tp10;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp10.repository.AppointmentRepository;
import com.example.tp10.util.SessionManager;

import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvTotalAppointments;
    private TextView tvScheduledCount, tvConfirmedCount, tvCompletedCount, tvCancelledCount;
    private TextView tvSexBreakdown, tvSpecialtyBreakdown;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private AppointmentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        sessionManager = new SessionManager(this);
        repository = new AppointmentRepository(this);

        TextView tvModeSubtitle = findViewById(R.id.tvModeSubtitle);
        tvTotalAppointments = findViewById(R.id.tvTotalAppointments);
        tvScheduledCount = findViewById(R.id.tvScheduledCount);
        tvConfirmedCount = findViewById(R.id.tvConfirmedCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvCancelledCount = findViewById(R.id.tvCancelledCount);
        tvSexBreakdown = findViewById(R.id.tvSexBreakdown);
        tvSpecialtyBreakdown = findViewById(R.id.tvSpecialtyBreakdown);
        Button btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        String modeName = SessionManager.MODE_WEB.equals(sessionManager.getDataSourceMode()) ? "PHP REST API (MySQL)" : "Local SQLite";
        tvModeSubtitle.setText(getString(R.string.statistics_title) + " — " + modeName);

        btnBack.setOnClickListener(v -> finish());

        loadStatistics();
    }

    private void loadStatistics() {
        progressBar.setVisibility(View.VISIBLE);
        long userId = sessionManager.getUserId();

        repository.getStatistics(userId, new AppointmentRepository.RepositoryCallback<>() {
            @Override
            public void onSuccess(Map<String, Integer> stats) {
                progressBar.setVisibility(View.GONE);
                if (stats == null) return;

                int total = getStatValue(stats, "total");
                int scheduled = getStatValue(stats, "scheduled");
                int confirmed = getStatValue(stats, "confirmed");
                int completed = getStatValue(stats, "completed");
                int cancelled = getStatValue(stats, "cancelled");

                int male = getStatValue(stats, "male");
                int female = getStatValue(stats, "female");
                int other = getStatValue(stats, "other");

                tvTotalAppointments.setText(String.valueOf(total));
                tvScheduledCount.setText("Scheduled: " + scheduled);
                tvConfirmedCount.setText("Confirmed: " + confirmed);
                tvCompletedCount.setText("Completed: " + completed);
                tvCancelledCount.setText("Cancelled: " + cancelled);

                tvSexBreakdown.setText("Male: " + male + "  |  Female: " + female + "  |  Other: " + other);

                StringBuilder specSb = new StringBuilder();
                for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                    if (entry.getKey().startsWith("spec_")) {
                        String specName = entry.getKey().substring(5);
                        specSb.append("• ").append(specName).append(": ").append(entry.getValue()).append("\n");
                    }
                }

                if (specSb.length() == 0) {
                    tvSpecialtyBreakdown.setText("No specialty data recorded yet.");
                } else {
                    tvSpecialtyBreakdown.setText(specSb.toString().trim());
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StatisticsActivity.this, "Failed to load statistics: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getStatValue(Map<String, Integer> map, String key) {
        Integer val = map.get(key);
        return val != null ? val : 0;
    }
}
