BEGIN;

-- =========================================================
-- 1) Add missing FK column in dept_members -> member_type
-- =========================================================
ALTER TABLE IF EXISTS dept_members
    ADD COLUMN IF NOT EXISTS mbr_type_id INTEGER;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_dept_members_member_type'
    ) THEN
        ALTER TABLE dept_members
            ADD CONSTRAINT fk_dept_members_member_type
            FOREIGN KEY (mbr_type_id) REFERENCES member_type(mbr_type_id);
    END IF;
END $$;

-- =========================================================
-- 2) Convert char flags to BOOLEAN
-- =========================================================

-- vendor_info.active_flag
ALTER TABLE vendor_info
    ALTER COLUMN active_flag TYPE BOOLEAN
    USING CASE
        WHEN active_flag IS NULL THEN NULL
        WHEN upper(trim(active_flag)) IN ('Y','1','T','TRUE') THEN TRUE
        WHEN upper(trim(active_flag)) IN ('N','0','F','FALSE') THEN FALSE
        ELSE NULL
    END;

-- np_test_emp_hist.pass_flag
ALTER TABLE np_test_emp_hist
    ALTER COLUMN pass_flag TYPE BOOLEAN
    USING CASE
        WHEN pass_flag IS NULL THEN NULL
        WHEN upper(trim(pass_flag)) IN ('Y','1','T','TRUE') THEN TRUE
        WHEN upper(trim(pass_flag)) IN ('N','0','F','FALSE') THEN FALSE
        ELSE NULL
    END;

-- np_test_emp_hist.take_flag
ALTER TABLE np_test_emp_hist
    ALTER COLUMN take_flag TYPE BOOLEAN
    USING CASE
        WHEN take_flag IS NULL THEN NULL
        WHEN upper(trim(take_flag)) IN ('Y','1','T','TRUE') THEN TRUE
        WHEN upper(trim(take_flag)) IN ('N','0','F','FALSE') THEN FALSE
        ELSE NULL
    END;

-- =========================================================
-- 3) Ensure sequence-backed generation for integer PKs
-- =========================================================

-- employees.emp_no
CREATE SEQUENCE IF NOT EXISTS employees_emp_no_seq;
ALTER TABLE employees ALTER COLUMN emp_no SET DEFAULT nextval('employees_emp_no_seq');
ALTER SEQUENCE employees_emp_no_seq OWNED BY employees.emp_no;
SELECT setval(
  'employees_emp_no_seq',
  GREATEST(COALESCE((SELECT MAX(emp_no) FROM employees), 0), 1),
  COALESCE((SELECT MAX(emp_no) FROM employees), 0) > 0
);

-- visa_type.visa_type_id
CREATE SEQUENCE IF NOT EXISTS visa_type_visa_type_id_seq;
ALTER TABLE visa_type ALTER COLUMN visa_type_id SET DEFAULT nextval('visa_type_visa_type_id_seq');
ALTER SEQUENCE visa_type_visa_type_id_seq OWNED BY visa_type.visa_type_id;
SELECT setval(
  'visa_type_visa_type_id_seq',
  GREATEST(COALESCE((SELECT MAX(visa_type_id) FROM visa_type), 0), 1),
  COALESCE((SELECT MAX(visa_type_id) FROM visa_type), 0) > 0
);

-- np_type.np_type_id
CREATE SEQUENCE IF NOT EXISTS np_type_np_type_id_seq;
ALTER TABLE np_type ALTER COLUMN np_type_id SET DEFAULT nextval('np_type_np_type_id_seq');
ALTER SEQUENCE np_type_np_type_id_seq OWNED BY np_type.np_type_id;
SELECT setval(
  'np_type_np_type_id_seq',
  GREATEST(COALESCE((SELECT MAX(np_type_id) FROM np_type), 0), 1),
  COALESCE((SELECT MAX(np_type_id) FROM np_type), 0) > 0
);

-- np_lvl_info.np_lvl_id
CREATE SEQUENCE IF NOT EXISTS np_lvl_info_np_lvl_id_seq;
ALTER TABLE np_lvl_info ALTER COLUMN np_lvl_id SET DEFAULT nextval('np_lvl_info_np_lvl_id_seq');
ALTER SEQUENCE np_lvl_info_np_lvl_id_seq OWNED BY np_lvl_info.np_lvl_id;
SELECT setval(
  'np_lvl_info_np_lvl_id_seq',
  GREATEST(COALESCE((SELECT MAX(np_lvl_id) FROM np_lvl_info), 0), 1),
  COALESCE((SELECT MAX(np_lvl_id) FROM np_lvl_info), 0) > 0
);

-- np_test_hist.np_test_id
CREATE SEQUENCE IF NOT EXISTS np_test_hist_np_test_id_seq;
ALTER TABLE np_test_hist ALTER COLUMN np_test_id SET DEFAULT nextval('np_test_hist_np_test_id_seq');
ALTER SEQUENCE np_test_hist_np_test_id_seq OWNED BY np_test_hist.np_test_id;
SELECT setval(
  'np_test_hist_np_test_id_seq',
  GREATEST(COALESCE((SELECT MAX(np_test_id) FROM np_test_hist), 0), 1),
  COALESCE((SELECT MAX(np_test_id) FROM np_test_hist), 0) > 0
);

-- trng_type.trng_type_id
CREATE SEQUENCE IF NOT EXISTS trng_type_trng_type_id_seq;
ALTER TABLE trng_type ALTER COLUMN trng_type_id SET DEFAULT nextval('trng_type_trng_type_id_seq');
ALTER SEQUENCE trng_type_trng_type_id_seq OWNED BY trng_type.trng_type_id;
SELECT setval(
  'trng_type_trng_type_id_seq',
  GREATEST(COALESCE((SELECT MAX(trng_type_id) FROM trng_type), 0), 1),
  COALESCE((SELECT MAX(trng_type_id) FROM trng_type), 0) > 0
);

-- vendor_type.vendor_type_id
CREATE SEQUENCE IF NOT EXISTS vendor_type_vendor_type_id_seq;
ALTER TABLE vendor_type ALTER COLUMN vendor_type_id SET DEFAULT nextval('vendor_type_vendor_type_id_seq');
ALTER SEQUENCE vendor_type_vendor_type_id_seq OWNED BY vendor_type.vendor_type_id;
SELECT setval(
  'vendor_type_vendor_type_id_seq',
  GREATEST(COALESCE((SELECT MAX(vendor_type_id) FROM vendor_type), 0), 1),
  COALESCE((SELECT MAX(vendor_type_id) FROM vendor_type), 0) > 0
);

-- vendor_info.vendor_id
CREATE SEQUENCE IF NOT EXISTS vendor_info_vendor_id_seq;
ALTER TABLE vendor_info ALTER COLUMN vendor_id SET DEFAULT nextval('vendor_info_vendor_id_seq');
ALTER SEQUENCE vendor_info_vendor_id_seq OWNED BY vendor_info.vendor_id;
SELECT setval(
  'vendor_info_vendor_id_seq',
  GREATEST(COALESCE((SELECT MAX(vendor_id) FROM vendor_info), 0), 1),
  COALESCE((SELECT MAX(vendor_id) FROM vendor_info), 0) > 0
);

-- trng_info.trng_id
CREATE SEQUENCE IF NOT EXISTS trng_info_trng_id_seq;
ALTER TABLE trng_info ALTER COLUMN trng_id SET DEFAULT nextval('trng_info_trng_id_seq');
ALTER SEQUENCE trng_info_trng_id_seq OWNED BY trng_info.trng_id;
SELECT setval(
  'trng_info_trng_id_seq',
  GREATEST(COALESCE((SELECT MAX(trng_id) FROM trng_info), 0), 1),
  COALESCE((SELECT MAX(trng_id) FROM trng_info), 0) > 0
);

-- skills.skill_id
CREATE SEQUENCE IF NOT EXISTS skills_skill_id_seq;
ALTER TABLE skills ALTER COLUMN skill_id SET DEFAULT nextval('skills_skill_id_seq');
ALTER SEQUENCE skills_skill_id_seq OWNED BY skills.skill_id;
SELECT setval(
  'skills_skill_id_seq',
  GREATEST(COALESCE((SELECT MAX(skill_id) FROM skills), 0), 1),
  COALESCE((SELECT MAX(skill_id) FROM skills), 0) > 0
);

-- skill_lvl.skill_lvl_id
CREATE SEQUENCE IF NOT EXISTS skill_lvl_skill_lvl_id_seq;
ALTER TABLE skill_lvl ALTER COLUMN skill_lvl_id SET DEFAULT nextval('skill_lvl_skill_lvl_id_seq');
ALTER SEQUENCE skill_lvl_skill_lvl_id_seq OWNED BY skill_lvl.skill_lvl_id;
SELECT setval(
  'skill_lvl_skill_lvl_id_seq',
  GREATEST(COALESCE((SELECT MAX(skill_lvl_id) FROM skill_lvl), 0), 1),
  COALESCE((SELECT MAX(skill_lvl_id) FROM skill_lvl), 0) > 0
);

-- member_type.mbr_type_id
CREATE SEQUENCE IF NOT EXISTS member_type_mbr_type_id_seq;
ALTER TABLE member_type ALTER COLUMN mbr_type_id SET DEFAULT nextval('member_type_mbr_type_id_seq');
ALTER SEQUENCE member_type_mbr_type_id_seq OWNED BY member_type.mbr_type_id;
SELECT setval(
  'member_type_mbr_type_id_seq',
  GREATEST(COALESCE((SELECT MAX(mbr_type_id) FROM member_type), 0), 1),
  COALESCE((SELECT MAX(mbr_type_id) FROM member_type), 0) > 0
);

-- attendance_records.attendance_record_id
CREATE SEQUENCE IF NOT EXISTS attendance_records_attendance_record_id_seq;
ALTER TABLE attendance_records ALTER COLUMN attendance_record_id SET DEFAULT nextval('attendance_records_attendance_record_id_seq');
ALTER SEQUENCE attendance_records_attendance_record_id_seq OWNED BY attendance_records.attendance_record_id;
SELECT setval(
  'attendance_records_attendance_record_id_seq',
  GREATEST(COALESCE((SELECT MAX(attendance_record_id) FROM attendance_records), 0), 1),
  COALESCE((SELECT MAX(attendance_record_id) FROM attendance_records), 0) > 0
);

COMMIT;