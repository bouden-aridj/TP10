package com.example.tp10.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.tp10.database.DatabaseHelper;
import com.example.tp10.model.Appointment;
import com.example.tp10.model.User;
import com.example.tp10.network.ApiClient;
import com.example.tp10.util.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppointmentRepository {

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    private final DatabaseHelper dbHelper;
    private final ApiClient apiClient;
    private final SessionManager sessionManager;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public AppointmentRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
        this.sessionManager = new SessionManager(context);
        this.apiClient = new ApiClient(sessionManager.getBaseUrl());
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public boolean isWebMode() {
        return SessionManager.MODE_WEB.equals(sessionManager.getDataSourceMode());
    }

    public void register(final String username, final String password, final String fullName, final RepositoryCallback<User> callback) {
        if (isWebMode()) {
            apiClient.register(username, password, fullName, new ApiClient.ApiCallback<User>() {
                @Override
                public void onSuccess(User result) {
                    if (callback != null) callback.onSuccess(result);
                }

                @Override
                public void onError(String errorMessage) {
                    if (callback != null) callback.onError(errorMessage);
                }
            });
        } else {
            executor.execute(() -> {
                long userId = dbHelper.registerUser(username, password, fullName);
                if (userId > 0) {
                    User user = new User(userId, username, "", fullName);
                    mainHandler.post(() -> {
                        if (callback != null) callback.onSuccess(user);
                    });
                } else {
                    mainHandler.post(() -> {
                        if (callback != null) callback.onError("Registration failed or username already exists");
                    });
                }
            });
        }
    }

    public void login(final String username, final String password, final RepositoryCallback<User> callback) {
        if (isWebMode()) {
            apiClient.login(username, password, new ApiClient.ApiCallback<User>() {
                @Override
                public void onSuccess(User result) {
                    if (callback != null) callback.onSuccess(result);
                }

                @Override
                public void onError(String errorMessage) {
                    if (callback != null) callback.onError(errorMessage);
                }
            });
        } else {
            executor.execute(() -> {
                User user = dbHelper.loginUser(username, password);
                if (user != null) {
                    mainHandler.post(() -> {
                        if (callback != null) callback.onSuccess(user);
                    });
                } else {
                    mainHandler.post(() -> {
                        if (callback != null) callback.onError("Invalid username or password");
                    });
                }
            });
        }
    }

    public void getAppointments(final long userId, final RepositoryCallback<List<Appointment>> callback) {
        if (isWebMode()) {
            apiClient.getAppointments(userId, new ApiClient.ApiCallback<List<Appointment>>() {
                @Override
                public void onSuccess(List<Appointment> result) {
                    if (callback != null) callback.onSuccess(result);
                }

                @Override
                public void onError(String errorMessage) {
                    if (callback != null) callback.onError(errorMessage);
                }
            });
        } else {
            executor.execute(() -> {
                List<Appointment> list = dbHelper.getAppointmentsByUser(userId);
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(list);
                });
            });
        }
    }

    public void addAppointment(final Appointment appointment, final RepositoryCallback<Appointment> callback) {
        if (isWebMode()) {
            apiClient.addAppointment(appointment, new ApiClient.ApiCallback<Appointment>() {
                @Override
                public void onSuccess(Appointment result) {
                    if (callback != null) callback.onSuccess(result);
                }

                @Override
                public void onError(String errorMessage) {
                    if (callback != null) callback.onError(errorMessage);
                }
            });
        } else {
            executor.execute(() -> {
                long id = dbHelper.addAppointment(appointment);
                if (id > 0) {
                    appointment.setId(id);
                    mainHandler.post(() -> {
                        if (callback != null) callback.onSuccess(appointment);
                    });
                } else {
                    mainHandler.post(() -> {
                        if (callback != null) callback.onError("Failed to insert appointment into SQLite");
                    });
                }
            });
        }
    }

    public void deleteAppointment(final long id, final RepositoryCallback<Boolean> callback) {
        if (isWebMode()) {
            apiClient.deleteAppointment(id, new ApiClient.ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (callback != null) callback.onSuccess(result);
                }

                @Override
                public void onError(String errorMessage) {
                    if (callback != null) callback.onError(errorMessage);
                }
            });
        } else {
            executor.execute(() -> {
                boolean success = dbHelper.deleteAppointment(id);
                mainHandler.post(() -> {
                    if (callback != null) {
                        if (success) callback.onSuccess(true);
                        else callback.onError("Failed to delete appointment from SQLite");
                    }
                });
            });
        }
    }

    public void getStatistics(final long userId, final RepositoryCallback<Map<String, Integer>> callback) {
        if (isWebMode()) {
            // Compute statistics from list fetched via Web API
            getAppointments(userId, new RepositoryCallback<List<Appointment>>() {
                @Override
                public void onSuccess(List<Appointment> list) {
                    Map<String, Integer> stats = computeStatisticsFromList(list);
                    if (callback != null) callback.onSuccess(stats);
                }

                @Override
                public void onError(String errorMessage) {
                    if (callback != null) callback.onError(errorMessage);
                }
            });
        } else {
            executor.execute(() -> {
                Map<String, Integer> stats = dbHelper.getAppointmentStatistics(userId);
                mainHandler.post(() -> {
                    if (callback != null) callback.onSuccess(stats);
                });
            });
        }
    }

    private Map<String, Integer> computeStatisticsFromList(List<Appointment> appointments) {
        Map<String, Integer> stats = new HashMap<>();
        if (appointments == null) return stats;

        int total = appointments.size();
        int scheduled = 0, confirmed = 0, completed = 0, cancelled = 0;
        int male = 0, female = 0, other = 0;
        Map<String, Integer> specialtyCounts = new HashMap<>();

        for (Appointment app : appointments) {
            String status = app.getStatus();
            if ("Confirmed".equalsIgnoreCase(status)) confirmed++;
            else if ("Completed".equalsIgnoreCase(status)) completed++;
            else if ("Cancelled".equalsIgnoreCase(status)) cancelled++;
            else scheduled++;

            String sex = app.getSex();
            if ("Female".equalsIgnoreCase(sex)) female++;
            else if ("Other".equalsIgnoreCase(sex)) other++;
            else male++;

            String spec = app.getSpecialty();
            specialtyCounts.put(spec, specialtyCounts.getOrDefault(spec, 0) + 1);
        }

        stats.put("total", total);
        stats.put("scheduled", scheduled);
        stats.put("confirmed", confirmed);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("male", male);
        stats.put("female", female);
        stats.put("other", other);

        for (Map.Entry<String, Integer> entry : specialtyCounts.entrySet()) {
            stats.put("spec_" + entry.getKey(), entry.getValue());
        }

        return stats;
    }
}
