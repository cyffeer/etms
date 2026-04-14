BEGIN;

-- Ensure ID column exists and is populated
ALTER TABLE skills_inventory
  ADD COLUMN IF NOT EXISTS skills_inventory_id BIGINT;

CREATE SEQUENCE IF NOT EXISTS skills_inventory_id_seq;
ALTER TABLE skills_inventory
  ALTER COLUMN skills_inventory_id SET DEFAULT nextval('skills_inventory_id_seq');

UPDATE skills_inventory
SET skills_inventory_id = nextval('skills_inventory_id_seq')
WHERE skills_inventory_id IS NULL;

ALTER TABLE skills_inventory
  ALTER COLUMN skills_inventory_id SET NOT NULL;

-- Replace existing PK only if it is not on skills_inventory_id
DO $$
DECLARE
  v_pk_name text;
  v_pk_on_target boolean;
BEGIN
  SELECT c.conname
  INTO v_pk_name
  FROM pg_constraint c
  WHERE c.conrelid = 'skills_inventory'::regclass
    AND c.contype = 'p'
  LIMIT 1;

  IF v_pk_name IS NULL THEN
    ALTER TABLE skills_inventory
      ADD CONSTRAINT pk_skills_inventory PRIMARY KEY (skills_inventory_id);
  ELSE
    SELECT EXISTS (
      SELECT 1
      FROM pg_constraint c
      JOIN pg_attribute a
        ON a.attrelid = c.conrelid
       AND a.attnum = ANY(c.conkey)
      WHERE c.conrelid = 'skills_inventory'::regclass
        AND c.contype = 'p'
        AND a.attname = 'skills_inventory_id'
    ) INTO v_pk_on_target;

    IF NOT v_pk_on_target THEN
      EXECUTE format('ALTER TABLE skills_inventory DROP CONSTRAINT %I', v_pk_name);
      ALTER TABLE skills_inventory
        ADD CONSTRAINT pk_skills_inventory PRIMARY KEY (skills_inventory_id);
    END IF;
  END IF;
END $$;

COMMIT;