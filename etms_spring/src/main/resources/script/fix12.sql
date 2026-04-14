BEGIN;

-- ---------------------------------------------------------
-- PDF-facing aliases for skill master tables
-- ---------------------------------------------------------
ALTER TABLE skills
    ADD COLUMN IF NOT EXISTS skill_nm VARCHAR(25);

UPDATE skills
SET skill_nm = COALESCE(skill_nm, skill_name);

ALTER TABLE skill_lvl
    ADD COLUMN IF NOT EXISTS skill_lvl_nm VARCHAR(25);

UPDATE skill_lvl
SET skill_lvl_nm = COALESCE(skill_lvl_nm, skill_lvl_name, lvl_name);

-- ---------------------------------------------------------
-- Bring skills_inventory back to the PDF-style composite key
-- while keeping the compatibility surrogate column.
-- ---------------------------------------------------------
ALTER TABLE skills_inventory
    ADD COLUMN IF NOT EXISTS emp_no INTEGER;

UPDATE skills_inventory
SET emp_no = COALESCE(emp_no, employee_number);

ALTER TABLE skills_inventory
    ALTER COLUMN emp_no SET NOT NULL;

ALTER TABLE skills_inventory
    DROP CONSTRAINT IF EXISTS pk_skills_inventory;

ALTER TABLE skills_inventory
    ADD CONSTRAINT pk_skills_inventory PRIMARY KEY (emp_no, skill_id);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_skills_inventory_emp_no'
    ) THEN
        ALTER TABLE skills_inventory
            ADD CONSTRAINT fk_skills_inventory_emp_no
            FOREIGN KEY (emp_no) REFERENCES employees(emp_no);
    END IF;
END $$;

-- ---------------------------------------------------------
-- Missing foreign keys for lookup/master tables
-- ---------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_skill_lvl_skill'
    ) THEN
        ALTER TABLE skill_lvl
            ADD CONSTRAINT fk_skill_lvl_skill
            FOREIGN KEY (skill_id) REFERENCES skills(skill_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_dept_members_department_code'
    ) THEN
        ALTER TABLE dept_members
            ADD CONSTRAINT fk_dept_members_department_code
            FOREIGN KEY (department_code) REFERENCES department(department_code);
    END IF;
END $$;

-- ---------------------------------------------------------
-- PDF-style flag columns
-- ---------------------------------------------------------
ALTER TABLE vendor_info
    ALTER COLUMN active_flag TYPE VARCHAR(1)
    USING CASE
        WHEN active_flag IS NULL THEN NULL
        WHEN active_flag THEN 'Y'
        ELSE 'N'
    END;

ALTER TABLE np_test_emp_hist
    ALTER COLUMN pass_flag TYPE VARCHAR(1)
    USING CASE
        WHEN pass_flag IS NULL THEN NULL
        WHEN pass_flag THEN 'Y'
        ELSE 'N'
    END;

ALTER TABLE np_test_emp_hist
    ALTER COLUMN take_flag TYPE VARCHAR(1)
    USING CASE
        WHEN take_flag IS NULL THEN NULL
        WHEN take_flag THEN 'Y'
        ELSE 'N'
    END;

COMMIT;
