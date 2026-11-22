package com.example.tp10.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.tp10.model.Appointment;
import com.example.tp10.model.User;
import com.example.tp10.util.PasswordUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    private final String baseUrl;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public ApiClient(String baseUrl) {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        this.baseUrl = baseUrl;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void register(final String username, final String password, final String fullName, final ApiCallback<User> callback) {
        executor.execute(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("password_hash", PasswordUtils.hashPassword(password));
                json.put("full_name", fullName);

                String response = sendHttpRequest("register.php", "POST", json.toString());
                JSONObject resObj = new JSONObject(response);

                if ("success".equalsIgnoreCase(resObj.optString("status"))) {
                    JSONObject userObj = resObj.getJSONObject("user");
                    User user = User.fromJSONObject(userObj);
                    postSuccess(callback, user);
                } else {
                    postError(callback, resObj.optString("message", "Registration failed"));
                }
            } catch (Exception e) {
                postError(callback, "Network or server error: " + e.getMessage());
            }
        });
    }

    public void login(final String username, final String password, final ApiCallback<User> callback) {
        executor.execute(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("password_hash", PasswordUtils.hashPassword(password));

                String response = sendHttpRequest("login.php", "POST", json.toString());
                JSONObject resObj = new JSONObject(response);

                if ("success".equalsIgnoreCase(resObj.optString("status"))) {
                    JSONObject userObj = resObj.getJSONObject("user");
                    User user = User.fromJSONObject(userObj);
                    postSuccess(callback, user);
                } else {
                    postError(callback, resObj.optString("message", "Invalid login credentials"));
                }
            } catch (Exception e) {
                postError(callback, "Connection failed: " + e.getMessage());
            }
        });
    }

    public void getAppointments(final long userId, final ApiCallback<List<Appointment>> callback) {
        executor.execute(() -> {
            try {
                String endpoint = "list_appointments.php?user_id=" + userId;
                String response = sendHttpRequest(endpoint, "GET", null);
                JSONObject resObj = new JSONObject(response);

                if ("success".equalsIgnoreCase(resObj.optString("status"))) {
                    JSONArray arr = resObj.getJSONArray("data");
                    List<Appointment> list = new ArrayList<>();
                    for (int i = 0; i < arr.length(); i++) {
                        list.add(Appointment.fromJSONObject(arr.getJSONObject(i)));
                    }
                    postSuccess(callback, list);
                } else {
                    postError(callback, resObj.optString("message", "Failed to fetch appointments"));
                }
            } catch (Exception e) {
                postError(callback, "Error fetching data: " + e.getMessage());
            }
        });
    }

    public void addAppointment(final Appointment app, final ApiCallback<Appointment> callback) {
        executor.execute(() -> {
            try {
                JSONObject json = app.toJSONObject();
                String response = sendHttpRequest("add_appointment.php", "POST", json.toString());
                JSONObject resObj = new JSONObject(response);

                if ("success".equalsIgnoreCase(resObj.optString("status"))) {
                    long newId = resObj.optLong("id", 0);
                    if (newId > 0) {
                        app.setId(newId);
                    }
                    postSuccess(callback, app);
                } else {
                    postError(callback, resObj.optString("message", "Failed to add appointment"));
                }
            } catch (Exception e) {
                postError(callback, "Error creating appointment: " + e.getMessage());
            }
        });
    }

    public void deleteAppointment(final long appointmentId, final ApiCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("id", appointmentId);
                String response = sendHttpRequest("delete_appointment.php", "POST", json.toString());
                JSONObject resObj = new JSONObject(response);

                if ("success".equalsIgnoreCase(resObj.optString("status"))) {
                    postSuccess(callback, true);
                } else {
                    postError(callback, resObj.optString("message", "Failed to delete appointment"));
                }
            } catch (Exception e) {
                postError(callback, "Error deleting appointment: " + e.getMessage());
            }
        });
    }

    private String sendHttpRequest(String endpoint, String method, String jsonBody) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");

        if (jsonBody != null && !jsonBody.isEmpty() && ("POST".equals(method) || "PUT".equals(method))) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }
        }

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();

        if (is == null) {
            throw new Exception("HTTP error code: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line.trim());
        }
        reader.close();
        conn.disconnect();

        return response.toString();
    }

    private <T> void postSuccess(final ApiCallback<T> callback, final T result) {
        if (callback != null) {
            mainHandler.post(() -> callback.onSuccess(result));
        }
    }

    private <T> void postError(final ApiCallback<T> callback, final String message) {
        if (callback != null) {
            mainHandler.post(() -> callback.onError(message));
        }
    }
}
