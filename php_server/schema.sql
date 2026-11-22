-- MySQL Database Initialization Script for TP10 Medical Appointments
CREATE DATABASE IF NOT EXISTS tp10_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tp10_db;

DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    age INT NOT NULL,
    sex VARCHAR(10) NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Data (Default Admin User: username = admin, password = password123)
-- SHA-256 for 'password123': ef92b778ba765807be9e633d536785616529284657114a574710147209e86e9e
INSERT INTO users (id, username, password_hash, full_name) VALUES
(1, 'admin', 'ef92b778ba765807be9e633d536785616529284657114a574710147209e86e9e', 'Dr. John Doe');

INSERT INTO appointments (user_id, first_name, last_name, email, phone, age, sex, doctor_name, specialty, appointment_date, appointment_time, status, notes) VALUES
(1, 'Alice', 'Smith', 'alice@example.com', '555-0192', 29, 'Female', 'Dr. John Doe', 'General Practice', '2025-04-15', '09:30:00', 'Confirmed', 'Routine annual checkup'),
(1, 'Robert', 'Johnson', 'robert@example.com', '555-0144', 45, 'Male', 'Dr. Sarah Connor', 'Cardiology', '2025-04-18', '14:00:00', 'Scheduled', 'ECG evaluation');
