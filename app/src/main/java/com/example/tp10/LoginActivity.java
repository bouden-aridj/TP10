package com.example.tp10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp10.model.User;
import com.example.tp10.repository.AppointmentRepository;
import com.example.tp10.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etBaseUrl;
    private RadioGroup rgDataSource;
    private RadioButton rbSqlite, rbWeb;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private ProgressBar progressBar;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etBaseUrl = findViewById(R.id.etBaseUrl);
        rgDataSource = findViewById(R.id.rgDataSource);
        rbSqlite = findViewById(R.id.rbSqlite);
        rbWeb = findViewById(R.id.rbWeb);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        // Pre-fill Base URL & Data Source
        etBaseUrl.setText(sessionManager.getBaseUrl());
        if (SessionManager.MODE_WEB.equals(sessionManager.getDataSourceMode())) {
            rbWeb.setChecked(true);
            etBaseUrl.setVisibility(View.VISIBLE);
        } else {
            rbSqlite.setChecked(true);
            etBaseUrl.setVisibility(View.GONE);
        }

        rgDataSource.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbWeb) {
                etBaseUrl.setVisibility(View.VISIBLE);
            } else {
                etBaseUrl.setVisibility(View.GONE);
            }
        });

        btnLogin.setOnClickListener(v -> performLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String mode = rbWeb.isChecked() ? SessionManager.MODE_WEB : SessionManager.MODE_SQLITE;
        String baseUrl = etBaseUrl.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.setDataSourceMode(mode);
        sessionManager.setBaseUrl(baseUrl);

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        AppointmentRepository repository = new AppointmentRepository(this);
        repository.login(username, password, new AppointmentRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                sessionManager.saveUser(user.getId(), user.getUsername(), user.getFullName());
                Toast.makeText(LoginActivity.this, "Login successful! Mode: " + mode, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
