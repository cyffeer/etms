BEGIN;

ALTER TABLE skills_inventory
    DROP COLUMN IF EXISTS employee_number;

COMMIT;
