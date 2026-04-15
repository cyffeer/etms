CREATE SEQUENCE IF NOT EXISTS passport_info_id_seq;
CREATE SEQUENCE IF NOT EXISTS visa_info_id_seq;
CREATE SEQUENCE IF NOT EXISTS np_test_emp_hist_id_seq;

ALTER TABLE IF EXISTS passport_info
    ADD COLUMN IF NOT EXISTS passport_info_id BIGINT,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE IF EXISTS passport_info
    ALTER COLUMN passport_info_id SET DEFAULT nextval('passport_info_id_seq');

UPDATE passport_info
SET passport_info_id = nextval('passport_info_id_seq')
WHERE passport_info_id IS NULL;

UPDATE passport_info
SET created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

CREATE UNIQUE INDEX IF NOT EXISTS idx_passport_info_info_id ON passport_info (passport_info_id);

ALTER TABLE IF EXISTS visa_info
    ADD COLUMN IF NOT EXISTS visa_info_id BIGINT,
    ADD COLUMN IF NOT EXISTS cancel_flag VARCHAR(3),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE IF EXISTS visa_info
    ALTER COLUMN visa_info_id SET DEFAULT nextval('visa_info_id_seq');

UPDATE visa_info
SET visa_info_id = nextval('visa_info_id_seq')
WHERE visa_info_id IS NULL;

UPDATE visa_info
SET cancel_flag = COALESCE(cancel_flag, 'NO'),
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

CREATE UNIQUE INDEX IF NOT EXISTS idx_visa_info_info_id ON visa_info (visa_info_id);

ALTER TABLE IF EXISTS np_test_emp_hist
    ADD COLUMN IF NOT EXISTS np_test_emp_hist_id BIGINT,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE IF EXISTS np_test_emp_hist
    ALTER COLUMN np_test_emp_hist_id SET DEFAULT nextval('np_test_emp_hist_id_seq');

UPDATE np_test_emp_hist
SET np_test_emp_hist_id = nextval('np_test_emp_hist_id_seq')
WHERE np_test_emp_hist_id IS NULL;

UPDATE np_test_emp_hist
SET created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

CREATE UNIQUE INDEX IF NOT EXISTS idx_np_test_emp_hist_info_id ON np_test_emp_hist (np_test_emp_hist_id);

ALTER TABLE IF EXISTS trng_hist
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE trng_hist
SET created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

ALTER TABLE IF EXISTS trng_info
    ADD COLUMN IF NOT EXISTS trng_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS trng_type_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS vendor_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS start_date DATE,
    ADD COLUMN IF NOT EXISTS end_date DATE,
    ADD COLUMN IF NOT EXISTS location VARCHAR(150),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE trng_info ti
SET trng_code = COALESCE(ti.trng_code, 'TRNG-' || ti.trng_id),
    trng_type_code = COALESCE(
        ti.trng_type_code,
        (SELECT tt.trng_type_nm FROM trng_type tt WHERE tt.trng_type_id = ti.trng_type_id)
    ),
    vendor_code = COALESCE(
        ti.vendor_code,
        (SELECT COALESCE(vi.vendor_code, vi.vendor_nm) FROM vendor_info vi WHERE vi.vendor_id = ti.vendor_id)
    ),
    is_active = COALESCE(ti.is_active, TRUE),
    created_at = COALESCE(ti.created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(ti.updated_at, CURRENT_TIMESTAMP);

ALTER TABLE IF EXISTS np_test_hist
    ADD COLUMN IF NOT EXISTS np_lvl_info_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS test_center VARCHAR(150),
    ADD COLUMN IF NOT EXISTS test_level VARCHAR(50),
    ADD COLUMN IF NOT EXISTS score INTEGER,
    ADD COLUMN IF NOT EXISTS passed BOOLEAN,
    ADD COLUMN IF NOT EXISTS remarks VARCHAR(255),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE np_test_hist nth
SET np_lvl_info_code = COALESCE(
        nth.np_lvl_info_code,
        (SELECT nli.np_lvl_info_code FROM np_lvl_info nli WHERE nli.np_lvl_id = nth.np_lvl_id)
    ),
    test_level = COALESCE(
        nth.test_level,
        (SELECT nli.np_lvl_info_name FROM np_lvl_info nli WHERE nli.np_lvl_id = nth.np_lvl_id)
    ),
    passed = COALESCE(nth.passed, FALSE),
    created_at = COALESCE(nth.created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(nth.updated_at, CURRENT_TIMESTAMP);

ALTER TABLE IF EXISTS vendor_type
    ADD COLUMN IF NOT EXISTS vendor_type_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE vendor_type
SET vendor_type_code = COALESCE(vendor_type_code, vendor_type_nm),
    is_active = COALESCE(is_active, TRUE),
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

CREATE UNIQUE INDEX IF NOT EXISTS idx_vendor_type_code ON vendor_type (vendor_type_code);

ALTER TABLE IF EXISTS vendor_info
    ADD COLUMN IF NOT EXISTS vendor_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS vendor_type_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS contact_email VARCHAR(150),
    ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(30),
    ADD COLUMN IF NOT EXISTS address_line VARCHAR(255),
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE vendor_info
SET vendor_code = COALESCE(vendor_code, vendor_nm),
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

CREATE UNIQUE INDEX IF NOT EXISTS idx_vendor_info_code ON vendor_info (vendor_code);

ALTER TABLE IF EXISTS np_type
    ADD COLUMN IF NOT EXISTS np_type_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE np_type
SET np_type_code = COALESCE(np_type_code, np_type_nm),
    is_active = COALESCE(is_active, TRUE),
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

CREATE UNIQUE INDEX IF NOT EXISTS idx_np_type_code ON np_type (np_type_code);

ALTER TABLE IF EXISTS visa_type
    ADD COLUMN IF NOT EXISTS visa_type_code VARCHAR(30),
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;

UPDATE visa_type
SET visa_type_code = COALESCE(visa_type_code, visa_type_nm),
    is_active = COALESCE(is_active, TRUE),
    created_at = COALESCE(created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(updated_at, CURRENT_TIMESTAMP);

CREATE UNIQUE INDEX IF NOT EXISTS idx_visa_type_code ON visa_type (visa_type_code);
