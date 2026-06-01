-- Création de la base
CREATE DATABASE IF NOT EXISTS medical_db;
USE medical_db;

-- ======================
-- TABLE USERS
-- ======================
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

-- ======================
-- TABLE PATIENTS
-- ======================
CREATE TABLE patients (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          firstname VARCHAR(100),
                          lastname VARCHAR(100),
                          birthdate VARCHAR(50),
                          gender VARCHAR(20),
                          address VARCHAR(255),
                          phone VARCHAR(20),
                          email VARCHAR(100)
);

-- ======================
-- DONNÉES DE TEST
-- ======================

INSERT INTO users (username, password, role) VALUES
                                                 ('doctor1', 'password123', 'ROLE_DOCTOR'),
                                                 ('doctor2', 'password123', 'ROLE_DOCTOR');

INSERT INTO patients (firstname, lastname, birthdate, gender, address, phone, email) VALUES
                                                                                         ('Jean', 'Dupont', '1980-05-12', 'M', 'Paris', '0600000001', 'jean@mail.com'),
                                                                                         ('Marie', 'Martin', '1990-08-22', 'F', 'Lyon', '0600000002', 'marie@mail.com');