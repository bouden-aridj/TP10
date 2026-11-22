package com.example.tp10.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private long id;
    private String username;
    private String passwordHash;
    private String fullName;

    public User() {
    }

    public User(long id, String username, String passwordHash, String fullName) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    public User(String username, String passwordHash, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        if (id > 0) {
            json.put("id", id);
        }
        json.put("username", username);
        json.put("password_hash", passwordHash);
        json.put("full_name", fullName);
        return json;
    }

    public static User fromJSONObject(JSONObject json) throws JSONException {
        User user = new User();
        user.setId(json.optLong("id", json.optLong("user_id", 0)));
        user.setUsername(json.optString("username", ""));
        user.setPasswordHash(json.optString("password_hash", ""));
        user.setFullName(json.optString("full_name", ""));
        return user;
    }
}
