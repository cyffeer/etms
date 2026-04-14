-- =========================
-- EMPLOYEES
-- =========================
CREATE TABLE employees (
    emp_no INTEGER PRIMARY KEY,
    batch NUMERIC(3,1),
    surname VARCHAR(25),
    given_nm VARCHAR(25),
    middle_nm VARCHAR(25),
    nickname VARCHAR(15),
    birth_date DATE,
    sss_no VARCHAR(15),
    pagibig_no VARCHAR(20)
);

-- =========================
-- VISA
-- =========================
CREATE TABLE visa_type (
    visa_type_id INTEGER PRIMARY KEY,
    visa_type_nm VARCHAR(25),
    description VARCHAR(150)
);

CREATE TABLE visa_info (
    emp_no INTEGER,
    visa_type_id INTEGER,
    issued DATE,
    expiry DATE,
    PRIMARY KEY (emp_no, visa_type_id),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no),
    FOREIGN KEY (visa_type_id) REFERENCES visa_type(visa_type_id)
);

-- =========================
-- PASSPORT
-- =========================
CREATE TABLE passport_info (
    passport_no VARCHAR(15) PRIMARY KEY,
    emp_no INTEGER,
    issued DATE,
    expiry DATE,
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no)
);

-- =========================
-- JOB GRADE
-- =========================
CREATE TABLE job_grade (
    job_grade_id INTEGER PRIMARY KEY,
    job_grade_nm VARCHAR(25),
    description VARCHAR(150)
);

-- =========================
-- NIHONGO PROFICIENCY
-- =========================
CREATE TABLE np_type (
    np_type_id INTEGER PRIMARY KEY,
    np_type_nm VARCHAR(25),
    description VARCHAR(150)
);

CREATE TABLE np_lvl_info (
    np_lvl_id INTEGER PRIMARY KEY,
    np_type_id INTEGER,
    lvl_nm VARCHAR(25),
    description VARCHAR(150),
    validity INTEGER,
    allowance1 DECIMAL,
    allowance2 DECIMAL,
    allowance3 DECIMAL,
    allowance4 DECIMAL,
    FOREIGN KEY (np_type_id) REFERENCES np_type(np_type_id)
);

CREATE TABLE np_test_hist (
    np_test_id INTEGER PRIMARY KEY,
    np_lvl_id INTEGER,
    test_date DATE,
    FOREIGN KEY (np_lvl_id) REFERENCES np_lvl_info(np_lvl_id)
);

CREATE TABLE np_test_emp_hist (
    np_test_id INTEGER,
    emp_no INTEGER,
    pass_flag VARCHAR(1),
    take_flag VARCHAR(1),
    points INTEGER,
    PRIMARY KEY (np_test_id, emp_no),
    FOREIGN KEY (np_test_id) REFERENCES np_test_hist(np_test_id),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no)
);

-- =========================
-- TRAINING
-- =========================
CREATE TABLE trng_type (
    trng_type_id INTEGER PRIMARY KEY,
    trng_type_nm VARCHAR(25),
    description VARCHAR(150)
);

CREATE TABLE vendor_type (
    vendor_type_id INTEGER PRIMARY KEY,
    vendor_type_nm VARCHAR(25)
);

CREATE TABLE vendor_info (
    vendor_id INTEGER PRIMARY KEY,
    vendor_nm VARCHAR(150),
    vendor_type_id INTEGER,
    active_flag VARCHAR(1),
    contact_nm VARCHAR(100),
    contact_phone1 VARCHAR(50),
    contact_phone2 VARCHAR(50),
    contact_fax VARCHAR(50),
    FOREIGN KEY (vendor_type_id) REFERENCES vendor_type(vendor_type_id)
);

CREATE TABLE trng_info (
    trng_id INTEGER PRIMARY KEY,
    trng_type_id INTEGER,
    description VARCHAR(150),
    vendor_id INTEGER,
    FOREIGN KEY (trng_type_id) REFERENCES trng_type(trng_type_id),
    FOREIGN KEY (vendor_id) REFERENCES vendor_info(vendor_id)
);

CREATE TABLE trng_hist (
    trng_id INTEGER,
    emp_no INTEGER,
    PRIMARY KEY (trng_id, emp_no),
    FOREIGN KEY (trng_id) REFERENCES trng_info(trng_id),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no)
);

-- =========================
-- SKILLS
-- =========================
CREATE TABLE skills (
    skill_id INTEGER PRIMARY KEY,
    skill_nm VARCHAR(25)
);

CREATE TABLE skill_lvl (
    skill_lvl_id INTEGER PRIMARY KEY,
    skill_lvl_nm VARCHAR(25)
);

CREATE TABLE skills_inventory (
    emp_no INTEGER,
    skill_id INTEGER,
    skill_lvl_id INTEGER,
    PRIMARY KEY (emp_no, skill_id),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no),
    FOREIGN KEY (skill_id) REFERENCES skills(skill_id),
    FOREIGN KEY (skill_lvl_id) REFERENCES skill_lvl(skill_lvl_id)
);

-- =========================
-- DEPARTMENT
-- =========================
CREATE TABLE department (
    dept_code VARCHAR(4) PRIMARY KEY,
    dept_name VARCHAR(50),
    abbr VARCHAR(5)
);

CREATE TABLE member_type (
    mbr_type_id INTEGER PRIMARY KEY,
    mbr_type_nm VARCHAR(25)
);

CREATE TABLE dept_members (
    dept_code VARCHAR(4),
    emp_no INTEGER,
    member_start DATE,
    member_end DATE,
    PRIMARY KEY (dept_code, emp_no),
    FOREIGN KEY (dept_code) REFERENCES department(dept_code),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no)
);

-- =========================
-- LOGIN
-- =========================
CREATE TABLE login (
    user_id VARCHAR(10) PRIMARY KEY,
    password VARCHAR(20) NOT NULL
);

-- =========================
-- LEAVES
-- =========================
CREATE TABLE leaves (
    emp_no INTEGER,
    emp_id VARCHAR(10) PRIMARY KEY,
    vacation NUMERIC NOT NULL,
    vacation_balance NUMERIC NOT NULL,
    vl_date DATE NOT NULL,
    sick_start NUMERIC NOT NULL,
    sl_date DATE NOT NULL,
    holiday_start NUMERIC NOT NULL,
    holiday_balance NUMERIC NOT NULL,
    hl_date DATE NOT NULL,
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no)
);

-- =========================
-- ATTENDANCE
-- =========================
CREATE TABLE attendance_records (
    attendance_record_id INTEGER PRIMARY KEY,
    emp_no INTEGER,
    attendance_date DATE,
    time_in TIME,
    time_out TIME,
    status VARCHAR(20),
    FOREIGN KEY (emp_no) REFERENCES employees(emp_no)
);