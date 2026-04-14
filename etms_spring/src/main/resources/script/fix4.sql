BEGIN;

ALTER TABLE department
    ADD COLUMN IF NOT EXISTS department_id INTEGER,
    ADD COLUMN IF NOT EXISTS department_code VARCHAR(50),
    ADD COLUMN IF NOT EXISTS department_name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

-- Backfill without casting D001-style values to integer
WITH ranked AS (
    SELECT
        dept_code,
        row_number() OVER (ORDER BY dept_code) AS rn
    FROM department
)
UPDATE department d
SET department_id   = COALESCE(d.department_id, r.rn),
    department_code = COALESCE(d.department_code, d.dept_code),
    department_name = COALESCE(d.department_name, d.dept_name),
    is_active       = COALESCE(d.is_active, TRUE),
    created_at      = COALESCE(d.created_at, CURRENT_TIMESTAMP),
    updated_at      = COALESCE(d.updated_at, CURRENT_TIMESTAMP)
FROM ranked r
WHERE d.dept_code = r.dept_code
  AND (
      d.department_id IS NULL OR
      d.department_code IS NULL OR
      d.department_name IS NULL OR
      d.is_active IS NULL OR
      d.created_at IS NULL OR
      d.updated_at IS NULL
  );

ALTER TABLE department
    ALTER COLUMN department_id SET NOT NULL,
    ALTER COLUMN department_code SET NOT NULL,
    ALTER COLUMN department_name SET NOT NULL,
    ALTER COLUMN is_active SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_department_department_code'
    ) THEN
        ALTER TABLE department
            ADD CONSTRAINT uq_department_department_code UNIQUE (department_code);
    END IF;
END $$;

CREATE SEQUENCE IF NOT EXISTS department_department_id_seq;
ALTER TABLE department ALTER COLUMN department_id SET DEFAULT nextval('department_department_id_seq');
ALTER SEQUENCE department_department_id_seq OWNED BY department.department_id;

SELECT setval(
    'department_department_id_seq',
    COALESCE((SELECT MAX(department_id) FROM department), 1),
    COALESCE((SELECT MAX(department_id) FROM department), 0) > 0
);

COMMIT;