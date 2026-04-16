-- ETMS presentation seed data for PostgreSQL

-- Clear existing sample/demo data in dependency-safe order.
TRUNCATE TABLE
    attendance_records,
    leave_records,
    leaves,
    trng_hist,
    skills_inventory,
    dept_members,
    employee_event,
    visa_info,
    visa_type,
    passport_info,
    np_test_emp_hist,
    np_test_hist,
    np_lvl_info,
    np_type,
    trng_info,
    trng_type,
    vendor_info,
    vendor_type,
    skill_lvl,
    skills,
    leave_types,
    member_type,
    department,
    employees,
    login
RESTART IDENTITY CASCADE;

-- -------------------------
-- Departments
-- -------------------------
INSERT INTO department (department_code, department_name, is_active, created_at, updated_at)
VALUES
  ('D001', 'Engineering', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('D002', 'HR', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('D003', 'Finance', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------
-- Member types
-- -------------------------
INSERT INTO member_type (member_type_code, member_type_name, is_active, created_at, updated_at)
VALUES
  ('MT001', 'Manager', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('MT002', 'Lead', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('MT003', 'Developer', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('MT004', 'Analyst', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------
-- Leave types
-- -------------------------
INSERT INTO leave_types (
  leave_type_code,
  leave_type_name,
  description,
  annual_entitlement_days,
  active,
  created_at,
  updated_at
)
VALUES
  ('LT001', 'Vacation', 'Planned annual leave for personal travel or rest', 20, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('LT002', 'Sick', 'Short-term medical leave', 15, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('LT003', 'Personal', 'Personal errands or family matters', 5, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('LT004', 'Bereavement', 'Leave following a family loss', 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('LT005', 'Maternity', 'Parental leave for maternity support', 60, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------
-- Skills
-- -------------------------
INSERT INTO skills (skill_code, skill_name, description, is_active, created_at, updated_at)
VALUES
  ('SKILL-JAVA', 'Java', 'Core Java development', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('SKILL-SPRING', 'Spring Boot', 'Spring Boot application development', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('SKILL-POSTGRES', 'PostgreSQL', 'Database design and SQL tuning', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('SKILL-ANGULAR', 'Angular', 'Frontend development with Angular', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('SKILL-AWS', 'AWS', 'Cloud deployment and operations', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('SKILL-DOCKER', 'Docker', 'Containerized application packaging', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('SKILL-GIT', 'Git', 'Source control and collaboration', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('SKILL-AGILE', 'Agile', 'Agile delivery and team practices', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO skill_lvl (skill_id, skill_lvl_nm, lvl_code, lvl_rank, is_active, created_at, updated_at)
SELECT s.skill_id,
       l.skill_lvl_nm,
       l.lvl_code,
       l.lvl_rank,
       TRUE,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM skills s
CROSS JOIN (
    VALUES
        ('Beginner', 'L1', 1),
        ('Intermediate', 'L2', 2),
        ('Advanced', 'L3', 3)
) AS l(skill_lvl_nm, lvl_code, lvl_rank);

-- -------------------------
-- Training master data
-- -------------------------
INSERT INTO trng_type (trng_type_nm, description)
VALUES
  ('Technical', 'Hands-on technical training'),
  ('Soft Skills', 'Leadership and communication training'),
  ('Compliance', 'Policy, security, and regulatory training');

INSERT INTO vendor_type (vendor_type_code, vendor_type_nm, is_active, created_at, updated_at)
VALUES
  ('VT001', 'Training Partner', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('VT002', 'Certification Partner', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO vendor_info (
  vendor_code,
  vendor_nm,
  vendor_type_code,
  contact_email,
  contact_phone,
  address_line,
  active_flag,
  created_at,
  updated_at
)
VALUES
  ('VEND-001', 'TechBridge Academy', 'VT001', 'training@techbridge.demo', '+63 917 555 0101', 'Makati City, Philippines', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('VEND-002', 'PeopleFirst Learning', 'VT002', 'info@peoplefirst.demo', '+63 917 555 0102', 'Quezon City, Philippines', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO trng_info (
  trng_type_id,
  vendor_id,
  description,
  trng_code,
  trng_type_code,
  vendor_code,
  start_date,
  end_date,
  location,
  is_active,
  certificate_path,
  created_at,
  updated_at
)
SELECT tt.trng_type_id,
       v.vendor_id,
       t.trng_name,
       t.trng_code,
       t.trng_type_code,
       t.vendor_code,
       t.start_date,
       t.end_date,
       t.location,
       TRUE,
       NULL,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM (
    VALUES
        ('TRN-TECH-001', 'Java Spring Boot Accelerator', 'TECH', 'VEND-001', CURRENT_DATE - 220, CURRENT_DATE - 217, 'Online'),
        ('TRN-TECH-002', 'Angular UI Engineering', 'TECH', 'VEND-001', CURRENT_DATE - 180, CURRENT_DATE - 177, 'Hybrid'),
        ('TRN-SOFT-001', 'Leading High-Performing Teams', 'SOFT', 'VEND-002', CURRENT_DATE - 150, CURRENT_DATE - 148, 'Makati'),
        ('TRN-COMP-001', 'Data Privacy and Compliance', 'COMP', 'VEND-002', CURRENT_DATE - 120, CURRENT_DATE - 119, 'Online'),
        ('TRN-TECH-003', 'AWS and Docker Lab', 'TECH', 'VEND-001', CURRENT_DATE - 90, CURRENT_DATE - 87, 'Hybrid')
) AS t(trng_code, trng_name, trng_type_code, vendor_code, start_date, end_date, location)
JOIN trng_type tt ON tt.trng_type_nm =
    CASE t.trng_type_code
        WHEN 'TECH' THEN 'Technical'
        WHEN 'SOFT' THEN 'Soft Skills'
        WHEN 'COMP' THEN 'Compliance'
    END
JOIN vendor_info v ON v.vendor_code = t.vendor_code;

-- -------------------------
-- Employees
-- -------------------------
INSERT INTO employees (
  emp_no,
  employee_code,
  first_name,
  last_name,
  email,
  hire_date,
  photo_path,
  is_active,
  created_at,
  updated_at
)
VALUES
  (1001, 'EMP_001', 'LeBron', 'James', 'lebron.james@etms.demo', DATE '2021-01-11', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1002, 'EMP_002', 'Stephen', 'Curry', 'stephen.curry@etms.demo', DATE '2021-03-15', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1003, 'EMP_003', 'Kevin', 'Durant', 'kevin.durant@etms.demo', DATE '2020-08-10', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1004, 'EMP_004', 'Luka', 'Doncic', 'luka.doncic@etms.demo', DATE '2022-05-09', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1005, 'EMP_005', 'Giannis', 'Antetokounmpo', 'giannis.antetokounmpo@etms.demo', DATE '2023-02-20', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1006, 'EMP_006', 'Jayson', 'Tatum', 'jayson.tatum@etms.demo', DATE '2023-07-17', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1007, 'EMP_007', 'Nikola', 'Jokic', 'nikola.jokic@etms.demo', DATE '2024-01-22', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1008, 'EMP_008', 'Joel', 'Embiid', 'joel.embiid@etms.demo', DATE '2024-03-11', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1009, 'EMP_009', 'Damian', 'Lillard', 'damian.lillard@etms.demo', DATE '2024-08-05', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (1010, 'EMP_010', 'Anthony', 'Edwards', 'anthony.edwards@etms.demo', DATE '2025-01-13', NULL, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------
-- Department memberships
-- -------------------------
WITH assignment_data (dept_code, emp_no, member_type_code, member_start, member_end) AS (
    VALUES
        ('D001', 1001, 'MT001', DATE '2021-01-11', NULL),
        ('D002', 1002, 'MT004', DATE '2021-03-15', NULL),
        ('D003', 1003, 'MT001', DATE '2020-08-10', NULL),
        ('D001', 1004, 'MT002', DATE '2022-05-09', NULL),
        ('D001', 1005, 'MT003', DATE '2023-02-20', NULL),
        ('D001', 1006, 'MT003', DATE '2023-07-17', NULL),
        ('D003', 1007, 'MT004', DATE '2024-01-22', NULL),
        ('D002', 1008, 'MT004', DATE '2024-03-11', NULL),
        ('D001', 1009, 'MT003', DATE '2024-08-05', NULL),
        ('D003', 1010, 'MT004', DATE '2025-01-13', NULL)
)
INSERT INTO dept_members (
  dept_code,
  emp_no,
  mbr_type_id,
  member_start,
  member_end,
  created_at,
  updated_at
)
SELECT a.dept_code,
       a.emp_no,
       mt.member_type_id,
       a.member_start::date,
       a.member_end::date,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM assignment_data a
JOIN member_type mt ON mt.member_type_code = a.member_type_code;

-- -------------------------
-- Leave records
-- -------------------------
INSERT INTO leave_records (
  employee_number,
  leave_type,
  start_date,
  end_date,
  status,
  remarks
)
VALUES
  ('1005', 'Vacation', CURRENT_DATE - 65, CURRENT_DATE - 63, 'APPROVED', 'Annual trip to Cebu'),
  ('1006', 'Sick', CURRENT_DATE - 42, CURRENT_DATE - 41, 'APPROVED', 'Flu recovery leave'),
  ('1007', 'Personal', CURRENT_DATE - 25, CURRENT_DATE - 25, 'APPROVED', 'Personal errands'),
  ('1008', 'Bereavement', CURRENT_DATE - 102, CURRENT_DATE - 100, 'APPROVED', 'Family bereavement support'),
  ('1009', 'Vacation', CURRENT_DATE + 7, CURRENT_DATE + 9, 'APPROVED', 'Planned long weekend'),
  ('1010', 'Personal', CURRENT_DATE + 14, CURRENT_DATE + 15, 'PENDING', 'Family event request');

-- -------------------------
-- Attendance records
-- -------------------------
WITH attendance_days AS (
    SELECT CURRENT_DATE - 29 + day_offset AS attendance_date
    FROM generate_series(0, 29) AS day_offset
),
attendance_status AS (
    SELECT e.emp_no,
           d.attendance_date,
           CASE
               WHEN EXTRACT(DOW FROM d.attendance_date) IN (0, 6) THEN 'REST_DAY'
               WHEN MOD(EXTRACT(DAY FROM d.attendance_date)::int + e.emp_no, 7) = 0 THEN 'ABSENT'
               WHEN MOD(EXTRACT(DAY FROM d.attendance_date)::int + e.emp_no, 5) = 0 THEN 'LATE'
               ELSE 'PRESENT'
           END AS status
    FROM (VALUES (1005), (1006), (1007)) AS e(emp_no)
    CROSS JOIN attendance_days d
)
INSERT INTO attendance_records (
  emp_no,
  attendance_date,
  time_in,
  time_out,
  status
)
SELECT emp_no,
       attendance_date,
       CASE status
           WHEN 'PRESENT' THEN TIME '08:55:00'
           WHEN 'LATE' THEN TIME '09:18:00'
           ELSE NULL
       END,
       CASE status
           WHEN 'PRESENT' THEN TIME '18:05:00'
           WHEN 'LATE' THEN TIME '18:20:00'
           ELSE NULL
       END,
       status
FROM attendance_status
ORDER BY attendance_date, emp_no;

-- -------------------------
-- Travel information
-- -------------------------
INSERT INTO visa_type (visa_type_code, visa_type_nm, description, is_active, created_at, updated_at)
VALUES
  ('VISA-WORK', 'Work Visa', 'Employment authorization visa', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('VISA-BUS', 'Business Visa', 'Short business travel visa', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('VISA-TOUR', 'Tourist Visa', 'Personal travel visa', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO passport_info (
  passport_no,
  emp_no,
  issued,
  expiry,
  created_at,
  updated_at
)
VALUES
  ('P-1001-2029', '1001', CURRENT_DATE - 780, CURRENT_DATE + 1040, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('P-1003-2029', '1003', CURRENT_DATE - 760, CURRENT_DATE + 1020, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('P-1007-2029', '1007', CURRENT_DATE - 730, CURRENT_DATE + 990, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO visa_info (
  emp_no,
  visa_type_id,
  issued,
  expiry,
  cancel_flag,
  created_at,
  updated_at
)
SELECT v.employee_number::integer,
       vt.visa_type_id,
       v.issued_date,
       v.expiry_date,
       v.cancel_flag,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM (
    VALUES
        ('1001', 'VISA-WORK', CURRENT_DATE - 420, CURRENT_DATE + 540, 'NO'),
        ('1003', 'VISA-BUS', CURRENT_DATE - 240, CURRENT_DATE + 300, 'NO'),
        ('1007', 'VISA-TOUR', CURRENT_DATE - 120, CURRENT_DATE + 180, 'YES')
) AS v(employee_number, visa_type_code, issued_date, expiry_date, cancel_flag)
JOIN visa_type vt ON vt.visa_type_code = v.visa_type_code;

-- -------------------------
-- Nihongo proficiency
-- -------------------------
INSERT INTO np_type (np_type_code, np_type_nm, is_active, created_at, updated_at)
VALUES
  ('N5', 'N5', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('N4', 'N4', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('N3', 'N3', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('N2', 'N2', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('N1', 'N1', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO np_lvl_info (
  np_lvl_info_code,
  np_lvl_info_name,
  np_type_code,
  valid_from,
  valid_to,
  allowance_amount,
  allowance_currency,
  is_active,
  created_at,
  updated_at
)
VALUES
  ('NP-N5', 'Beginner - N5', 'N5', DATE '2020-01-01', DATE '2099-12-31', 0.00, 'JPY', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('NP-N4', 'Elementary - N4', 'N4', DATE '2020-01-01', DATE '2099-12-31', 1500.00, 'JPY', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('NP-N3', 'Intermediate - N3', 'N3', DATE '2020-01-01', DATE '2099-12-31', 3000.00, 'JPY', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('NP-N2', 'Upper Intermediate - N2', 'N2', DATE '2020-01-01', DATE '2099-12-31', 5000.00, 'JPY', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('NP-N1', 'Advanced - N1', 'N1', DATE '2020-01-01', DATE '2099-12-31', 8000.00, 'JPY', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO np_test_hist (
  np_lvl_id,
  np_lvl_info_code,
  test_date,
  test_center,
  test_level,
  score,
  passed,
  remarks,
  created_at,
  updated_at
)
SELECT n.np_lvl_id,
       t.np_lvl_info_code,
       t.test_date,
       t.test_center,
       t.test_level,
       t.score,
       t.passed,
       t.remarks,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM (
    VALUES
        ('NP-N5', CURRENT_DATE - 210, 'Manila Testing Center', 'N5', 96, TRUE, 'First-time pass'),
        ('NP-N4', CURRENT_DATE - 180, 'Cebu Testing Center', 'N4', 88, TRUE, 'Solid foundation'),
        ('NP-N3', CURRENT_DATE - 150, 'Manila Testing Center', 'N3', 79, TRUE, 'Good grammar and reading'),
        ('NP-N2', CURRENT_DATE - 120, 'Tokyo Language Hub', 'N2', 67, TRUE, 'Passed after retake'),
        ('NP-N1', CURRENT_DATE - 90, 'Tokyo Language Hub', 'N1', 58, FALSE, 'Needs additional study')
) AS t(np_lvl_info_code, test_date, test_center, test_level, score, passed, remarks)
JOIN np_lvl_info n ON n.np_lvl_info_code = t.np_lvl_info_code;

INSERT INTO np_test_emp_hist (
  np_test_id,
  emp_no,
  pass_flag,
  take_flag,
  points,
  created_at,
  updated_at
)
SELECT h.np_test_id,
       e.emp_no::integer,
       e.pass_flag,
       e.take_flag,
       e.points,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM (
    VALUES
        ('NP-N5', '1005', 'Y', 'Y', 96),
        ('NP-N4', '1006', 'Y', 'Y', 88),
        ('NP-N3', '1007', 'Y', 'Y', 79),
        ('NP-N2', '1008', 'Y', 'Y', 67),
        ('NP-N1', '1009', 'N', 'Y', 58)
) AS e(np_lvl_info_code, emp_no, pass_flag, take_flag, points)
JOIN np_test_hist h ON h.np_lvl_info_code = e.np_lvl_info_code;

-- -------------------------
-- Training history
-- -------------------------
INSERT INTO trng_hist (
  trng_id,
  emp_no,
  created_at,
  updated_at
)
SELECT t.trng_id,
       h.emp_no::integer,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM (
    VALUES
        ('TRN-TECH-001', '1001'),
        ('TRN-TECH-002', '1002'),
        ('TRN-SOFT-001', '1003'),
        ('TRN-COMP-001', '1004'),
        ('TRN-TECH-003', '1005'),
        ('TRN-TECH-001', '1006'),
        ('TRN-SOFT-001', '1007'),
        ('TRN-COMP-001', '1008'),
        ('TRN-TECH-002', '1009'),
        ('TRN-TECH-003', '1010')
) AS h(trng_code, emp_no)
JOIN trng_info t ON t.trng_code = h.trng_code;

-- -------------------------
-- Skills inventory
-- -------------------------
WITH skills_inventory_seed (emp_no, skill_code, lvl_code) AS (
    VALUES
        (1001, 'SKILL-JAVA', 'L3'),
        (1001, 'SKILL-SPRING', 'L3'),
        (1001, 'SKILL-GIT', 'L3'),
        (1002, 'SKILL-AGILE', 'L3'),
        (1002, 'SKILL-ANGULAR', 'L2'),
        (1003, 'SKILL-POSTGRES', 'L3'),
        (1003, 'SKILL-AWS', 'L2'),
        (1004, 'SKILL-ANGULAR', 'L3'),
        (1004, 'SKILL-DOCKER', 'L2'),
        (1005, 'SKILL-JAVA', 'L2'),
        (1005, 'SKILL-SPRING', 'L2'),
        (1006, 'SKILL-GIT', 'L3'),
        (1006, 'SKILL-ANGULAR', 'L2'),
        (1007, 'SKILL-POSTGRES', 'L2'),
        (1008, 'SKILL-AWS', 'L1'),
        (1009, 'SKILL-DOCKER', 'L2'),
        (1010, 'SKILL-AGILE', 'L2')
)
INSERT INTO skills_inventory (
  emp_no,
  skill_id,
  skill_lvl_id,
  created_at,
  updated_at
)
SELECT s.emp_no,
       sk.skill_id,
       sl.skill_lvl_id,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM skills_inventory_seed s
JOIN skills sk ON sk.skill_code = s.skill_code
JOIN skill_lvl sl ON sl.skill_id = sk.skill_id
                AND sl.lvl_code = s.lvl_code;

-- -------------------------
-- Employee events
-- -------------------------
INSERT INTO employee_event (
  emp_no,
  event_type,
  title,
  description,
  department_code,
  reference_code,
  effective_date,
  end_date,
  status,
  created_at,
  updated_at
)
VALUES
  ('1001', 'PROMOTION', 'Promotion to Senior Engineering Director', 'Recognized for leading the platform modernization program', 'D001', 'PR-1001', CURRENT_DATE - 320, NULL, 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('1002', 'CITATION', 'Service Excellence Citation', 'Recognized for outstanding onboarding support', 'D002', 'CT-1002', CURRENT_DATE - 260, NULL, 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('1003', 'PROJECT_ASSIGNMENT', 'Finance Automation Workstream', 'Assigned to lead the finance automation rollout', 'D003', 'PA-1003', CURRENT_DATE - 210, CURRENT_DATE + 90, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('1004', 'PROMOTION', 'Promotion to Lead Developer', 'Promotion after delivery of the quarterly integration release', 'D001', 'PR-1004', CURRENT_DATE - 180, NULL, 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('1005', 'RESIGNATION', 'Resignation Request', 'Employee submitted resignation effective at month end', 'D001', 'RS-1005', CURRENT_DATE - 30, CURRENT_DATE + 15, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('1006', 'SUSPENSION', 'Temporary Suspension', 'Pending investigation following repeated policy violations', 'D001', 'SP-1006', CURRENT_DATE - 20, CURRENT_DATE - 13, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('1007', 'PROJECT_ASSIGNMENT', 'APAC Rollout Support', 'Assigned as backup lead for the APAC deployment', 'D003', 'PA-1007', CURRENT_DATE - 90, CURRENT_DATE + 60, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('1008', 'TERMINATION', 'Termination Notice', 'Separation after escalation review', 'D002', 'TM-1008', CURRENT_DATE - 10, CURRENT_DATE + 10, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -------------------------
-- Login
-- -------------------------
INSERT INTO login (user_id, password, role)
VALUES
  ('admin', 'admin123', 'ADMIN'),
  ('hr', 'hr123', 'HR'),
  ('manager1', 'manager123', 'MANAGER'),
  ('manager2', 'manager123', 'MANAGER'),
  ('1005', 'emp123', 'EMPLOYEE'),
  ('1006', 'emp123', 'EMPLOYEE'),
  ('1007', 'emp123', 'EMPLOYEE'),
  ('1008', 'emp123', 'EMPLOYEE'),
  ('1009', 'emp123', 'EMPLOYEE'),
  ('1010', 'emp123', 'EMPLOYEE');
