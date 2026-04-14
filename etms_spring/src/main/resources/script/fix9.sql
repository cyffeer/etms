-- Run as new script: fix9_safe.sql
BEGIN;

-- keep employee_number INTEGER because of FK to employees.emp_no
-- only align ID numeric types used by joins/contracts

ALTER TABLE skills
  ALTER COLUMN skill_id TYPE BIGINT
  USING skill_id::bigint;

ALTER TABLE skill_lvl
  ALTER COLUMN skill_id TYPE BIGINT
  USING skill_id::bigint;

ALTER TABLE skill_lvl
  ALTER COLUMN skill_lvl_id TYPE BIGINT
  USING skill_lvl_id::bigint;

ALTER TABLE skills_inventory
  ALTER COLUMN skill_id TYPE BIGINT
  USING skill_id::bigint;

ALTER TABLE skills_inventory
  ALTER COLUMN skill_lvl_id TYPE BIGINT
  USING skill_lvl_id::bigint;

COMMIT;