BEGIN;

DO $$
DECLARE
    seq_name text := 'trng_hist_trng_id_seq';
    next_id bigint;
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_class
        WHERE relkind = 'S'
          AND relname = seq_name
    ) THEN
        EXECUTE format('CREATE SEQUENCE %I OWNED BY trng_hist.trng_id', seq_name);
    END IF;

    EXECUTE format('ALTER TABLE trng_hist ALTER COLUMN trng_id SET DEFAULT nextval(%L)', seq_name);

    SELECT COALESCE(MAX(trng_id), 0) + 1
    INTO next_id
    FROM trng_hist;

    EXECUTE format('SELECT setval(%L, %s, false)', seq_name, next_id);
END $$;

COMMIT;
