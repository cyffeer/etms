BEGIN;
ALTER TABLE skill_lvl ADD COLUMN IF NOT EXISTS skill_lvl_name VARCHAR(255);
ALTER TABLE skill_lvl ADD COLUMN IF NOT EXISTS skill_lvl_code VARCHAR(100);
ALTER TABLE skill_lvl ADD COLUMN IF NOT EXISTS level_rank INTEGER;

UPDATE skill_lvl
SET skill_lvl_name = COALESCE(skill_lvl_name, lvl_name),
    skill_lvl_code = COALESCE(skill_lvl_code, lvl_code),
    level_rank     = COALESCE(level_rank, lvl_rank);
COMMIT;