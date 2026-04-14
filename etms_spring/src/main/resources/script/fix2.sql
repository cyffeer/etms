BEGIN;

-- Rename old column if present
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'skills' AND column_name = 'skill_nm'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'skills' AND column_name = 'skill_name'
    ) THEN
        ALTER TABLE skills RENAME COLUMN skill_nm TO skill_name;
    END IF;
END $$;

-- Add missing columns expected by entity
ALTER TABLE skills
    ADD COLUMN IF NOT EXISTS skill_code VARCHAR(50),
    ADD COLUMN IF NOT EXISTS description VARCHAR(255),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

-- Backfill existing rows
UPDATE skills
SET skill_name  = COALESCE(skill_name, 'Skill ' || skill_id::text),
    skill_code  = COALESCE(skill_code, 'SKILL_' || skill_id::text),
    is_active   = COALESCE(is_active, TRUE),
    created_at  = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at  = COALESCE(updated_at, CURRENT_TIMESTAMP);

-- Enforce constraints expected by entity
ALTER TABLE skills
    ALTER COLUMN skill_name TYPE VARCHAR(150),
    ALTER COLUMN skill_name SET NOT NULL,
    ALTER COLUMN skill_code SET NOT NULL,
    ALTER COLUMN is_active SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

-- Unique constraint for skill_code
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_skills_skill_code'
    ) THEN
        ALTER TABLE skills
            ADD CONSTRAINT uq_skills_skill_code UNIQUE (skill_code);
    END IF;
END $$;

COMMIT;