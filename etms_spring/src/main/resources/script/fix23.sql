ALTER TABLE IF EXISTS leave_types
    ADD COLUMN IF NOT EXISTS annual_entitlement_days INTEGER NOT NULL DEFAULT 0;

UPDATE leave_types
SET annual_entitlement_days = CASE leave_type_code
    WHEN 'VACATION' THEN 15
    WHEN 'SICK' THEN 10
    WHEN 'HOLIDAY' THEN 5
    ELSE COALESCE(annual_entitlement_days, 0)
END;
