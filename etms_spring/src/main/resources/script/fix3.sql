BEGIN;

-- Add the entity-expected columns
ALTER TABLE employees
    ADD COLUMN IF NOT EXISTS employee_id INTEGER,
    ADD COLUMN IF NOT EXISTS employee_code VARCHAR(50),
    ADD COLUMN IF NOT EXISTS first_name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS last_name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS email VARCHAR(255),
    ADD COLUMN IF NOT EXISTS hire_date DATE,
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

-- Backfill from legacy columns where possible
UPDATE employees
SET employee_id   = COALESCE(employee_id, emp_no),
    employee_code = COALESCE(employee_code, 'EMP_' || emp_no::text),
    first_name    = COALESCE(first_name, given_nm),
    last_name     = COALESCE(last_name, surname),
    is_active     = COALESCE(is_active, TRUE),
    created_at    = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at    = COALESCE(updated_at, CURRENT_TIMESTAMP)
WHERE employee_id IS NULL
   OR employee_code IS NULL
   OR first_name IS NULL
   OR last_name IS NULL
   OR is_active IS NULL
   OR created_at IS NULL
   OR updated_at IS NULL;

-- Enforce entity nullability
ALTER TABLE employees
    ALTER COLUMN employee_id SET NOT NULL,
    ALTER COLUMN employee_code SET NOT NULL,
    ALTER COLUMN first_name SET NOT NULL,
    ALTER COLUMN last_name SET NOT NULL,
    ALTER COLUMN is_active SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

-- Constraints expected by the entity
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_employees_employee_code'
    ) THEN
        ALTER TABLE employees
            ADD CONSTRAINT uq_employees_employee_code UNIQUE (employee_code);
    END IF;
END $$;

-- Make employee_id generated for new rows
CREATE SEQUENCE IF NOT EXISTS employees_employee_id_seq;
ALTER TABLE employees ALTER COLUMN employee_id SET DEFAULT nextval('employees_employee_id_seq');
ALTER SEQUENCE employees_employee_id_seq OWNED BY employees.employee_id;

SELECT setval(
    'employees_employee_id_seq',
    COALESCE((SELECT MAX(employee_id) FROM employees), 1),
    COALESCE((SELECT MAX(employee_id) FROM employees), 0) > 0
);

COMMIT;