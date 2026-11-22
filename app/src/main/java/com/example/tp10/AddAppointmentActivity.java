package com.example.tp10;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp10.model.Appointment;
import com.example.tp10.repository.AppointmentRepository;
import com.example.tp10.util.SessionManager;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhone, etAge, etDoctorName, etNotes;
    private Spinner spSex, spSpecialty, spStatus;
    private Button btnSelectDate, btnSelectTime, btnSave, btnCancel;
    private ProgressBar progressBar;

    private String selectedDate = "";
    private String selectedTime = "";

    private SessionManager sessionManager;
    private AppointmentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        sessionManager = new SessionManager(this);
        repository = new AppointmentRepository(this);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAge = findViewById(R.id.etAge);
        etDoctorName = findViewById(R.id.etDoctorName);
        etNotes = findViewById(R.id.etNotes);

        spSex = findViewById(R.id.spSex);
        spSpecialty = findViewById(R.id.spSpecialty);
        spStatus = findViewById(R.id.spStatus);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        setupSpinners();
        setDefaultDateTime();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSave.setOnClickListener(v -> saveAppointment());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> sexAdapter = ArrayAdapter.createFromResource(
                this, R.array.sex_options, R.layout.spinner_item);
        sexAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spSex.setAdapter(sexAdapter);

        ArrayAdapter<CharSequence> specAdapter = ArrayAdapter.createFromResource(
                this, R.array.specialty_options, R.layout.spinner_item);
        specAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spSpecialty.setAdapter(specAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this, R.array.status_options, R.layout.spinner_item);
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);
    }

    private void setDefaultDateTime() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month, day);
        btnSelectDate.setText("📅 " + selectedDate);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        selectedTime = String.format(Locale.US, "%02d:%02d", hour, minute);
        btnSelectTime.setText("🕒 " + selectedTime);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
            btnSelectDate.setText("📅 " + selectedDate);
        }, year, month, day);
        dialog.show();
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            selectedTime = String.format(Locale.US, "%02d:%02d", hourOfDay, minute1);
            btnSelectTime.setText("🕒 " + selectedTime);
        }, hour, minute, true);
        dialog.show();
    }

    private void saveAppointment() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String doctorName = etDoctorName.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || ageStr.isEmpty() || doctorName.isEmpty()) {
            Toast.makeText(this, "Please fill in all patient and doctor details", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age number", Toast.LENGTH_SHORT).show();
            return;
        }

        String sex = spSex.getSelectedItem().toString();
        String specialty = spSpecialty.getSelectedItem().toString();
        String status = spStatus.getSelectedItem().toString();
        long userId = sessionManager.getUserId();

        Appointment appointment = new Appointment(
                userId, firstName, lastName, email, phone, age, sex,
                doctorName, specialty, selectedDate, selectedTime, status, notes
        );

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        repository.addAppointment(appointment, new AppointmentRepository.RepositoryCallback<Appointment>() {
            @Override
            public void onSuccess(Appointment result) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddAppointmentActivity.this, "Appointment created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(AddAppointmentActivity.this, "Failed to create appointment: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
