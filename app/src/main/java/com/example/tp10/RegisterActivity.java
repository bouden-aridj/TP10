package com.example.tp10;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp10.model.User;
import com.example.tp10.repository.AppointmentRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> performRegistration());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        AppointmentRepository repository = new AppointmentRepository(this);
        repository.register(username, password, fullName, new AppointmentRepository.RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
