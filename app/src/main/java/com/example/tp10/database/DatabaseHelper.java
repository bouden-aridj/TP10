package com.example.tp10.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tp10.model.Appointment;
import com.example.tp10.model.User;
import com.example.tp10.util.PasswordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tp10_medical.db";
    private static final int DATABASE_VERSION = 1;

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_PASSWORD_HASH = "password_hash";
    public static final String COL_USER_FULL_NAME = "full_name";

    // Appointments Table
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COL_APP_ID = "id";
    public static final String COL_APP_USER_ID = "user_id";
    public static final String COL_APP_FIRST_NAME = "first_name";
    public static final String COL_APP_LAST_NAME = "last_name";
    public static final String COL_APP_EMAIL = "email";
    public static final String COL_APP_PHONE = "phone";
    public static final String COL_APP_AGE = "age";
    public static final String COL_APP_SEX = "sex";
    public static final String COL_APP_DOCTOR_NAME = "doctor_name";
    public static final String COL_APP_SPECIALTY = "specialty";
    public static final String COL_APP_DATE = "appointment_date";
    public static final String COL_APP_TIME = "appointment_time";
    public static final String COL_APP_STATUS = "status";
    public static final String COL_APP_NOTES = "notes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD_HASH + " TEXT NOT NULL, " +
                COL_USER_FULL_NAME + " TEXT NOT NULL" +
                ");";

        String createAppointmentsTable = "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                COL_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_APP_USER_ID + " INTEGER NOT NULL, " +
                COL_APP_FIRST_NAME + " TEXT NOT NULL, " +
                COL_APP_LAST_NAME + " TEXT NOT NULL, " +
                COL_APP_EMAIL + " TEXT NOT NULL, " +
                COL_APP_PHONE + " TEXT NOT NULL, " +
                COL_APP_AGE + " INTEGER NOT NULL, " +
                COL_APP_SEX + " TEXT NOT NULL, " +
                COL_APP_DOCTOR_NAME + " TEXT NOT NULL, " +
                COL_APP_SPECIALTY + " TEXT NOT NULL, " +
                COL_APP_DATE + " TEXT NOT NULL, " +
                COL_APP_TIME + " TEXT NOT NULL, " +
                COL_APP_STATUS + " TEXT NOT NULL, " +
                COL_APP_NOTES + " TEXT, " +
                "FOREIGN KEY(" + COL_APP_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")" +
                ");";

        db.execSQL(createUsersTable);
        db.execSQL(createAppointmentsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- USER AUTHENTICATION METHODS ---

    public long registerUser(String username, String plainPassword, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_USERNAME, username.trim());
        values.put(COL_USER_PASSWORD_HASH, PasswordUtils.hashPassword(plainPassword));
        values.put(COL_USER_FULL_NAME, fullName.trim());

        try {
            return db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public User loginUser(String username, String plainPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        String[] columns = {COL_USER_ID, COL_USER_USERNAME, COL_USER_PASSWORD_HASH, COL_USER_FULL_NAME};
        String selection = COL_USER_USERNAME + " = ? AND " + COL_USER_PASSWORD_HASH + " = ?";
        String[] selectionArgs = {username.trim(), hashedPassword};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD_HASH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_FULL_NAME))
            );
            cursor.close();
        }
        return user;
    }

    // --- APPOINTMENT CRUD METHODS ---

    public long addAppointment(Appointment app) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APP_USER_ID, app.getUserId());
        values.put(COL_APP_FIRST_NAME, app.getFirstName());
        values.put(COL_APP_LAST_NAME, app.getLastName());
        values.put(COL_APP_EMAIL, app.getEmail());
        values.put(COL_APP_PHONE, app.getPhone());
        values.put(COL_APP_AGE, app.getAge());
        values.put(COL_APP_SEX, app.getSex());
        values.put(COL_APP_DOCTOR_NAME, app.getDoctorName());
        values.put(COL_APP_SPECIALTY, app.getSpecialty());
        values.put(COL_APP_DATE, app.getAppointmentDate());
        values.put(COL_APP_TIME, app.getAppointmentTime());
        values.put(COL_APP_STATUS, app.getStatus());
        values.put(COL_APP_NOTES, app.getNotes());

        try {
            return db.insert(TABLE_APPOINTMENTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Appointment> getAppointmentsByUser(long userId) {
        List<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COL_APP_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COL_APP_DATE + " DESC, " + COL_APP_TIME + " DESC";

        Cursor cursor = db.query(TABLE_APPOINTMENTS, null, selection, selectionArgs, null, null, orderBy);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Appointment app = new Appointment(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_APP_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_APP_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_LAST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_PHONE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_APP_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_SEX)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_DOCTOR_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_SPECIALTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_STATUS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_NOTES))
                );
                list.add(app);
            }
            cursor.close();
        }
        return list;
    }

    public boolean deleteAppointment(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_APPOINTMENTS, COL_APP_ID + " = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // --- STATISTICS ---

    public Map<String, Integer> getAppointmentStatistics(long userId) {
        Map<String, Integer> stats = new HashMap<>();
        List<Appointment> appointments = getAppointmentsByUser(userId);

        int total = appointments.size();
        int scheduled = 0, confirmed = 0, completed = 0, cancelled = 0;
        int male = 0, female = 0, other = 0;

        Map<String, Integer> specialtyCounts = new HashMap<>();

        for (Appointment app : appointments) {
            // Status
            String status = app.getStatus();
            if ("Confirmed".equalsIgnoreCase(status)) confirmed++;
            else if ("Completed".equalsIgnoreCase(status)) completed++;
            else if ("Cancelled".equalsIgnoreCase(status)) cancelled++;
            else scheduled++;

            // Sex
            String sex = app.getSex();
            if ("Female".equalsIgnoreCase(sex)) female++;
            else if ("Other".equalsIgnoreCase(sex)) other++;
            else male++;

            // Specialty
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
