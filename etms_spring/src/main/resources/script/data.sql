-- ETMS seed/test data for PostgreSQL

-- Clear in dependency-safe order
DELETE FROM attendance_records;
DELETE FROM trng_hist;
DELETE FROM leaves;
DELETE FROM dept_members;
DELETE FROM trng_info;
DELETE FROM trng_type;
DELETE FROM login;
DELETE FROM employees;
DELETE FROM department;

-- -------------------------
-- Departments
-- -------------------------
INSERT INTO department (dept_code, dept_name, abbr)
VALUES
  ('D001', 'Engineering', 'ENG'),
  ('D002', 'Human Resources', 'HR'),
  ('D003', 'Operations', 'OPS');

-- -------------------------
-- Employees
-- -------------------------
INSERT INTO employees (
  emp_no,
  batch,
  surname,
  given_nm,
  middle_nm,
  nickname,
  birth_date,
  sss_no,
  pagibig_no
) VALUES
  (1001, 1.0, 'Tanaka', 'Alice', 'M', 'Ali', '1995-03-14', 'SSS-1001', 'PAG-1001'),
  (1002, 1.0, 'Cruz', 'Brian', 'L', 'Bri', '1993-07-22', 'SSS-1002', 'PAG-1002'),
  (1003, 1.0, 'Santos', 'Carla', 'J', 'Carly', '1996-11-08', 'SSS-1003', 'PAG-1003'),
  (1004, 2.0, 'Reyes', 'Daniel', 'P', 'Dan', '1990-01-30', 'SSS-1004', 'PAG-1004');

-- -------------------------
-- Department memberships
-- -------------------------
INSERT INTO dept_members (
  dept_code,
  emp_no,
  member_start,
  member_end
) VALUES
  ('D001', 1001, '2024-01-15', NULL),
  ('D001', 1002, '2024-02-01', NULL),
  ('D002', 1003, '2023-11-10', NULL),
  ('D003', 1004, '2022-06-20', '2025-01-31');

-- -------------------------
-- Training types
-- -------------------------
INSERT INTO trng_type (
  trng_type_id,
  trng_type_nm,
  description
) VALUES
  (1, 'Technical', 'Technical training'),
  (2, 'Soft Skills', 'Communication and teamwork training');

-- -------------------------
-- Training info
-- -------------------------
INSERT INTO trng_info (
  trng_id,
  trng_type_id,
  description,
  vendor_id
) VALUES
  (1, 1, 'Java Fundamentals', NULL),
  (2, 1, 'Spring Boot Basics', NULL),
  (3, 2, 'Business Communication', NULL);

-- -------------------------
-- Training history
-- -------------------------
INSERT INTO trng_hist (
  trng_id,
  emp_no
) VALUES
  (1, 1001),
  (2, 1002),
  (3, 1003);

-- -------------------------
-- Leaves
-- -------------------------
INSERT INTO leaves (
  emp_no,
  emp_id,
  vacation,
  vacation_balance,
  vl_date,
  sick_start,
  sl_date,
  holiday_start,
  holiday_balance,
  hl_date
) VALUES
  (1001, 'LV0001', 5, 10, '2026-04-21', 2, '2026-04-10', 1, 3, '2026-04-12'),
  (1002, 'LV0002', 3, 7, '2026-04-10', 1, '2026-04-11', 0, 2, '2026-04-13'),
  (1003, 'LV0003', 2, 8, '2026-04-12', 1, '2026-04-12', 1, 1, '2026-04-14');

-- -------------------------
-- Attendance
-- -------------------------
INSERT INTO attendance_records (
  attendance_record_id,
  emp_no,
  attendance_date,
  time_in,
  time_out,
  status
) VALUES
  (1, 1001, '2026-04-01', '08:58:00', '18:05:00', 'PRESENT'),
  (2, 1002, '2026-04-01', '09:20:00', '18:00:00', 'LATE'),
  (3, 1003, '2026-04-01', '09:00:00', '17:55:00', 'PRESENT'),
  (4, 1001, '2026-04-02', '09:05:00', '18:02:00', 'PRESENT'),
  (5, 1002, '2026-04-02', NULL, NULL, 'ABSENT');

-- -------------------------
-- Login
-- -------------------------
INSERT INTO login (
  user_id,
  password
) VALUES
  ('admin', 'admin123'),
  ('alice', 'alice123'),
  ('brian', 'brian123');