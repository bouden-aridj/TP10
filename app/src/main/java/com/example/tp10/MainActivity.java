package com.example.tp10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tp10.adapter.AppointmentAdapter;
import com.example.tp10.model.Appointment;
import com.example.tp10.repository.AppointmentRepository;
import com.example.tp10.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppointmentAdapter.OnAppointmentDeleteListener {

    private TextView tvWelcome, tvNoAppointments;
    private Button btnToggleMode, btnAddAppointment, btnStatistics;
    private ImageButton btnLogout;
    private ListView lvAppointments;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private AppointmentRepository repository;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvNoAppointments = findViewById(R.id.tvNoAppointments);
        btnToggleMode = findViewById(R.id.btnToggleMode);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnLogout = findViewById(R.id.btnLogout);
        lvAppointments = findViewById(R.id.lvAppointments);
        progressBar = findViewById(R.id.progressBar);

        repository = new AppointmentRepository(this);
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(this, appointmentList, this);
        lvAppointments.setAdapter(adapter);

        tvWelcome.setText("Welcome, " + sessionManager.getFullName());
        updateDataSourceBadge();

        btnToggleMode.setOnClickListener(v -> toggleDataSourceMode());

        btnAddAppointment.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddAppointmentActivity.class));
        });

        btnStatistics.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void updateDataSourceBadge() {
        String mode = sessionManager.getDataSourceMode();
        if (SessionManager.MODE_WEB.equals(mode)) {
            btnToggleMode.setText("Data Source: PHP REST API 🔄");
        } else {
            btnToggleMode.setText("Data Source: Local SQLite 🔄");
        }
    }

    private void toggleDataSourceMode() {
        String currentMode = sessionManager.getDataSourceMode();
        String newMode = SessionManager.MODE_WEB.equals(currentMode) ? SessionManager.MODE_SQLITE : SessionManager.MODE_WEB;
        sessionManager.setDataSourceMode(newMode);
        updateDataSourceBadge();
        
        Toast.makeText(this, "Switched data source to: " + (SessionManager.MODE_WEB.equals(newMode) ? "PHP REST API" : "Local SQLite"), Toast.LENGTH_SHORT).show();
        loadAppointments();
    }

    private void loadAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        long userId = sessionManager.getUserId();

        repository.getAppointments(userId, new AppointmentRepository.RepositoryCallback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> result) {
                progressBar.setVisibility(View.GONE);
                appointmentList.clear();
                if (result != null && !result.isEmpty()) {
                    appointmentList.addAll(result);
                    tvNoAppointments.setVisibility(View.GONE);
                } else {
                    tvNoAppointments.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error loading appointments: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDelete(Appointment appointment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete appointment for " + appointment.getFirstName() + " " + appointment.getLastName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    repository.deleteAppointment(appointment.getId(), new AppointmentRepository.RepositoryCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Appointment deleted", Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this, "Failed to delete: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
