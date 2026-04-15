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

UPDATE vendor_info vi
SET vendor_code = COALESCE(vi.vendor_code, vi.vendor_nm),
    vendor_type_code = COALESCE(
        vi.vendor_type_code,
        (SELECT vt.vendor_type_code FROM vendor_type vt WHERE vt.vendor_type_id = vi.vendor_type_id)
    ),
    contact_phone = COALESCE(vi.contact_phone, vi.contact_phone1, vi.contact_phone2),
    created_at = COALESCE(vi.created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(vi.updated_at, CURRENT_TIMESTAMP);

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
    passed = COALESCE(
        nth.passed,
        EXISTS (
            SELECT 1
            FROM np_test_emp_hist nteh
            WHERE nteh.np_test_id = nth.np_test_id
              AND COALESCE(nteh.pass_flag, 'N') = 'Y'
        )
    ),
    score = COALESCE(
        nth.score,
        (SELECT MAX(nteh.points) FROM np_test_emp_hist nteh WHERE nteh.np_test_id = nth.np_test_id)
    ),
    created_at = COALESCE(nth.created_at, CURRENT_TIMESTAMP),
    updated_at = COALESCE(nth.updated_at, CURRENT_TIMESTAMP);

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
