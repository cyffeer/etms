BEGIN;

-- ---------------------------------------------------------
-- Phase 7 seed data for PDF-aligned master tables
-- Keep the existing employee/department/training seed intact.
-- ---------------------------------------------------------

INSERT INTO member_type (
    mbr_type_nm,
    member_type_id,
    member_type_code,
    member_type_name,
    is_active,
    created_at,
    updated_at
) VALUES
    ('Member', 1, 'MEMBER', 'Member', TRUE, NOW(), NOW()),
    ('Leader', 2, 'LEADER', 'Leader', TRUE, NOW(), NOW()),
    ('Manager', 3, 'MANAGER', 'Manager', TRUE, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO visa_type (
    visa_type_nm,
    description
) VALUES
    ('WORK', 'Work visa'),
    ('BUSINESS', 'Business visa'),
    ('RESIDENCE', 'Residence visa')
ON CONFLICT DO NOTHING;

INSERT INTO vendor_type (
    vendor_type_nm
) VALUES
    ('TRAINING'),
    ('CONSULTING')
ON CONFLICT DO NOTHING;

INSERT INTO vendor_info (
    vendor_nm,
    vendor_type_id,
    active_flag,
    contact_nm,
    contact_phone1,
    contact_phone2,
    contact_fax
) VALUES
    ('Nihongo Training Center', 1, 'Y', 'Maria Cruz', '09171234567', '02-123-4567', '02-765-4321'),
    ('Language Solutions Ltd.', 2, 'Y', 'Ken Sato', '09179876543', '02-234-5678', '02-876-5432')
ON CONFLICT DO NOTHING;

INSERT INTO np_type (
    np_type_nm,
    description
) VALUES
    ('JLPT', 'Japanese Language Proficiency Test'),
    ('NAT', 'Nihongo Assessment Test')
ON CONFLICT DO NOTHING;

INSERT INTO np_lvl_info (
    np_type_id,
    lvl_nm,
    description,
    validity,
    allowance1,
    allowance2,
    allowance3,
    allowance4,
    np_lvl_info_id,
    np_lvl_info_code,
    np_lvl_info_name,
    np_type_code,
    allowance_amount,
    allowance_currency,
    valid_from,
    valid_to,
    is_active,
    created_at,
    updated_at
) VALUES
    (1, 'N5', 'Basic Japanese proficiency', 24, 0, 0, 0, 0, 1, 'JLPT-N5', 'JLPT N5', 'JLPT', 1000.00, 'JPY', CURRENT_DATE - 30, CURRENT_DATE + 365, TRUE, NOW(), NOW()),
    (1, 'N4', 'Lower-intermediate Japanese proficiency', 24, 0, 0, 0, 0, 2, 'JLPT-N4', 'JLPT N4', 'JLPT', 2000.00, 'JPY', CURRENT_DATE - 30, CURRENT_DATE + 730, TRUE, NOW(), NOW()),
    (2, 'Basic', 'Basic Japanese communication', 12, 0, 0, 0, 0, 3, 'NAT-BASIC', 'NAT Basic', 'NAT', 500.00, 'JPY', CURRENT_DATE - 15, CURRENT_DATE + 365, TRUE, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO passport_info (
    passport_no,
    emp_no,
    issued,
    expiry
) VALUES
    ('P1001-2024', 1001, DATE '2024-01-10', DATE '2028-01-09'),
    ('P1001-2022', 1001, DATE '2022-01-10', DATE '2026-01-09'),
    ('P1002-2023', 1002, DATE '2023-05-01', DATE '2025-12-31')
ON CONFLICT DO NOTHING;

INSERT INTO visa_info (
    emp_no,
    visa_type_id,
    issued,
    expiry
) VALUES
    (1001, 1, DATE '2025-01-01', CURRENT_DATE + 15),
    (1002, 2, DATE '2024-01-01', CURRENT_DATE - 10),
    (1003, 3, DATE '2025-06-01', CURRENT_DATE + 90)
ON CONFLICT DO NOTHING;

INSERT INTO skills_inventory (
    employee_number,
    emp_no,
    skill_id,
    skill_lvl_id,
    created_at,
    updated_at
) VALUES
    (1001, 1001, 1, 1, NOW(), NOW()),
    (1001, 1001, 2, 4, NOW(), NOW()),
    (1002, 1002, 1, 2, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO np_test_hist (
    np_lvl_id,
    test_date
) VALUES
    (1, CURRENT_DATE - 60),
    (2, CURRENT_DATE - 30),
    (3, CURRENT_DATE - 5)
ON CONFLICT DO NOTHING;

INSERT INTO np_test_emp_hist (
    np_test_id,
    emp_no,
    pass_flag,
    take_flag,
    points
) VALUES
    (1, 1001, 'Y', 'Y', 88),
    (2, 1001, 'N', 'Y', 64),
    (2, 1002, 'Y', 'Y', 91),
    (3, 1001, 'Y', 'N', 95)
ON CONFLICT DO NOTHING;

COMMIT;
