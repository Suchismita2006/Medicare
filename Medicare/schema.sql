-- ==========================================================
-- MEDICARE HOSPITAL MANAGEMENT SYSTEM
-- Database Schema Script
-- MySQL 8.0+
-- ==========================================================

-- 1. Create the Database
CREATE DATABASE IF NOT EXISTS medicare_db;
USE medicare_db;

-- 2. Drop existing tables if re-initializing (in reverse order of foreign keys)
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS patients;

-- 3. Create 'patients' Table
-- Stores basic patient demographics and diagnosed disease
CREATE TABLE patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    disease VARCHAR(100) NOT NULL
);

-- 4. Create 'doctors' Table
-- Stores doctor specialization, contact details and consultation fee
CREATE TABLE doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    consultation_fee DOUBLE NOT NULL
);

-- 5. Create 'appointments' Table
-- Links a patient with a doctor for a scheduled appointment date
CREATE TABLE appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'Scheduled',
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- 6. Create 'bills' Table
-- Stores billing details for a patient including consultation & extra charges
CREATE TABLE bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    consultation_charge DOUBLE NOT NULL,
    additional_charge DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
);

-- ==========================================================
-- Sample Initial Data for Testing & Demonstration
-- ==========================================================

-- Insert sample patients
INSERT INTO patients (name, age, gender, phone, disease) VALUES
('Rahul Sharma', 32, 'Male', '9876543210', 'Viral Fever'),
('Priya Patel', 28, 'Female', '9812345678', 'Migraine'),
('Amit Verma', 45, 'Male', '9845612340', 'Hypertension');

-- Insert sample doctors
INSERT INTO doctors (name, specialization, phone, consultation_fee) VALUES
('Dr. Ananya Roy', 'General Physician', '9988776655', 500.00),
('Dr. Vikram Malhotra', 'Cardiologist', '9977665544', 800.00),
('Dr. Sneha Nair', 'Neurologist', '9966554433', 750.00);

-- Insert sample appointments
INSERT INTO appointments (patient_id, doctor_id, appointment_date, status) VALUES
(1, 1, CURDATE(), 'Scheduled'),
(2, 3, CURDATE(), 'Scheduled');

-- Insert sample bill
INSERT INTO bills (patient_id, consultation_charge, additional_charge, total_amount) VALUES
(1, 500.00, 150.00, 650.00);

-- Verification Query
-- SELECT * FROM patients;
-- SELECT * FROM doctors;
-- SELECT * FROM appointments;
-- SELECT * FROM bills;
