-- blocker_fix_schema_2.sql

BEGIN;

-- =========================
-- member_type compatibility
-- =========================
ALTER TABLE member_type ADD COLUMN IF NOT EXISTS member_type_id BIGINT;
ALTER TABLE member_type ADD COLUMN IF NOT EXISTS member_type_code VARCHAR(50);
ALTER TABLE member_type ADD COLUMN IF NOT EXISTS member_type_name VARCHAR(100);
ALTER TABLE member_type ADD COLUMN IF NOT EXISTS is_active BOOLEAN;
ALTER TABLE member_type ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE member_type ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='member_type' AND column_name='mbr_type_id'
  ) THEN
    EXECUTE 'UPDATE member_type SET member_type_id = COALESCE(member_type_id, mbr_type_id::bigint)';
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='member_type' AND column_name='mbr_type_code'
  ) THEN
    EXECUTE 'UPDATE member_type SET member_type_code = COALESCE(member_type_code, mbr_type_code)';
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='member_type' AND column_name='mbr_type_name'
  ) THEN
    EXECUTE 'UPDATE member_type SET member_type_name = COALESCE(member_type_name, mbr_type_name)';
  END IF;
END $$;

UPDATE member_type
SET is_active = COALESCE(is_active, TRUE),
    created_at = COALESCE(created_at, NOW()),
    updated_at = COALESCE(updated_at, NOW());

-- =========================
-- np_lvl_info compatibility
-- =========================
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS np_lvl_info_id BIGINT;
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS np_lvl_info_code VARCHAR(50);
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS np_lvl_info_name VARCHAR(100);
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS np_type_code VARCHAR(50);
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS allowance_amount NUMERIC(14,2);
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS allowance_currency VARCHAR(10);
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS valid_from DATE;
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS valid_to DATE;
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS is_active BOOLEAN;
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE np_lvl_info ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

DO $$
BEGIN
  -- try common legacy id column names
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='np_lvl_info' AND column_name='np_lvl_id'
  ) THEN
    EXECUTE 'UPDATE np_lvl_info SET np_lvl_info_id = COALESCE(np_lvl_info_id, np_lvl_id::bigint)';
  ELSIF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='np_lvl_info' AND column_name='nplvl_id'
  ) THEN
    EXECUTE 'UPDATE np_lvl_info SET np_lvl_info_id = COALESCE(np_lvl_info_id, nplvl_id::bigint)';
  END IF;
END $$;

-- fill remaining null ids with generated values
CREATE SEQUENCE IF NOT EXISTS np_lvl_info_id_seq START 1;
UPDATE np_lvl_info
SET np_lvl_info_id = COALESCE(np_lvl_info_id, nextval('np_lvl_info_id_seq'));

UPDATE np_lvl_info
SET is_active = COALESCE(is_active, TRUE),
    created_at = COALESCE(created_at, NOW()),
    updated_at = COALESCE(updated_at, NOW());

COMMIT;