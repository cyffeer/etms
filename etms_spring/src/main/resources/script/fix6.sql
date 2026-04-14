-- Run this as a new script (e.g., fix6_safe.sql)

BEGIN;

-- dept_members compatibility
ALTER TABLE dept_members ADD COLUMN IF NOT EXISTS dept_member_id BIGSERIAL;
ALTER TABLE dept_members ADD COLUMN IF NOT EXISTS department_code VARCHAR(50);
ALTER TABLE dept_members ADD COLUMN IF NOT EXISTS employee_number VARCHAR(50);
ALTER TABLE dept_members ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE dept_members ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE dept_members
SET department_code = COALESCE(department_code, dept_code),
    employee_number = COALESCE(employee_number, emp_no::text),
    created_at = COALESCE(created_at, NOW()),
    updated_at = COALESCE(updated_at, NOW());

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'dept_members_pkey'
  ) THEN
    ALTER TABLE dept_members ADD CONSTRAINT dept_members_pkey PRIMARY KEY (dept_member_id);
  END IF;
END $$;

-- attendance_records compatibility
ALTER TABLE attendance_records ADD COLUMN IF NOT EXISTS employee_number VARCHAR(50);

UPDATE attendance_records
SET employee_number = COALESCE(employee_number, emp_no::text);

COMMIT;

-- leaves compatibility table expected by backend
CREATE TABLE IF NOT EXISTS leave_records (
  leave_record_id BIGSERIAL PRIMARY KEY,
  employee_number VARCHAR(50),
  start_date DATE,
  end_date DATE,
  leave_type VARCHAR(30),
  status VARCHAR(30),
  remarks TEXT
);