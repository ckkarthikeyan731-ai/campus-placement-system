-- ============================================================
--  Campus Placement Management System — MySQL DDL Script
--  Author : Karthikeyan C K | 25IT347
--  College : St. Joseph's College of Engineering
-- ============================================================

-- 1. Create & use the database
CREATE DATABASE IF NOT EXISTS campus_placement_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_placement_db;

-- 2. Admin users table
CREATE TABLE IF NOT EXISTS admin_users (
    id       INT          NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Default admin credential  (username: admin | password: admin123)
INSERT INTO admin_users (username, password)
VALUES ('admin', 'admin123')
ON DUPLICATE KEY UPDATE password = 'admin123';

-- 3. Placement records table
CREATE TABLE IF NOT EXISTS placement_records (
    id           INT           NOT NULL AUTO_INCREMENT,
    student_name VARCHAR(100)  NOT NULL,
    roll_no      VARCHAR(30)   NOT NULL UNIQUE,
    department   VARCHAR(80)   NOT NULL,
    company      VARCHAR(100)  NOT NULL,
    status       VARCHAR(30)   NOT NULL DEFAULT 'Placed',
    package_lpa  DOUBLE        NOT NULL DEFAULT 0.0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Sample seed data (optional — delete if not needed)
INSERT IGNORE INTO placement_records (student_name, roll_no, department, company, status, package_lpa) VALUES
('Aarav Sharma',    '21CS001', 'Computer Science',   'TCS',       'Placed',     6.5),
('Priya Nair',      '21IT002', 'Information Tech',   'Infosys',   'Placed',     5.0),
('Rahul Menon',     '21EC003', 'Electronics',        'Wipro',     'Placed',     4.5),
('Sneha Pillai',    '21ME004', 'Mechanical',         'L&T',       'Placed',     5.5),
('Kiran Kumar',     '21CS005', 'Computer Science',   'Cognizant', 'Placed',     7.0),
('Divya Lakshmi',   '21IT006', 'Information Tech',   'HCL',       'Placed',     6.0),
('Arjun Reddy',     '21CE007', 'Civil Engineering',  'NCC Urban', 'Placed',     4.0),
('Meera Gopal',     '21CS008', 'Computer Science',   'Zoho',      'Placed',     8.5),
('Suresh Babu',     '21IT009', 'Information Tech',   '-',         'Not Placed', 0.0),
('Lakshmi Devi',    '21EC010', 'Electronics',        'Samsung',   'Placed',     9.0);

-- ============================================================
--  Run this script once with:
--  mysql -u root -p < database_setup.sql
-- ============================================================
