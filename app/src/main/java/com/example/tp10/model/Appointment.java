package com.example.tp10.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Appointment {
    private long id;
    private long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int age;
    private String sex;
    private String doctorName;
    private String specialty;
    private String appointmentDate;
    private String appointmentTime;
    private String status;
    private String notes;

    public Appointment() {
    }

    public Appointment(long id, long userId, String firstName, String lastName, String email,
                       String phone, int age, String sex, String doctorName, String specialty,
                       String appointmentDate, String appointmentTime, String status, String notes) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.sex = sex;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }

    public Appointment(long userId, String firstName, String lastName, String email,
                       String phone, int age, String sex, String doctorName, String specialty,
                       String appointmentDate, String appointmentTime, String status, String notes) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.sex = sex;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        if (id > 0) {
            json.put("id", id);
        }
        json.put("user_id", userId);
        json.put("first_name", firstName);
        json.put("last_name", lastName);
        json.put("email", email);
        json.put("phone", phone);
        json.put("age", age);
        json.put("sex", sex);
        json.put("doctor_name", doctorName);
        json.put("specialty", specialty);
        json.put("appointment_date", appointmentDate);
        json.put("appointment_time", appointmentTime);
        json.put("status", status);
        json.put("notes", notes);
        return json;
    }

    public static Appointment fromJSONObject(JSONObject json) throws JSONException {
        Appointment app = new Appointment();
        app.setId(json.optLong("id", 0));
        app.setUserId(json.optLong("user_id", 0));
        app.setFirstName(json.optString("first_name", ""));
        app.setLastName(json.optString("last_name", ""));
        app.setEmail(json.optString("email", ""));
        app.setPhone(json.optString("phone", ""));
        app.setAge(json.optInt("age", 0));
        app.setSex(json.optString("sex", "Male"));
        app.setDoctorName(json.optString("doctor_name", ""));
        app.setSpecialty(json.optString("specialty", "General Practice"));
        app.setAppointmentDate(json.optString("appointment_date", ""));
        app.setAppointmentTime(json.optString("appointment_time", ""));
        app.setStatus(json.optString("status", "Scheduled"));
        app.setNotes(json.optString("notes", ""));
        return app;
    }
}
