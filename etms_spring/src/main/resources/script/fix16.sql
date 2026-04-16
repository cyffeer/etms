CREATE TABLE IF NOT EXISTS leave_types (
    leave_type_id BIGSERIAL PRIMARY KEY,
    leave_type_code VARCHAR(30) NOT NULL UNIQUE,
    leave_type_name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    annual_entitlement_days INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO leave_types (leave_type_code, leave_type_name, description, annual_entitlement_days, active)
VALUES
    ('VACATION', 'Vacation Leave', 'Scheduled vacation and annual leave', 15, TRUE),
    ('SICK', 'Sick Leave', 'Medical and wellness leave', 10, TRUE),
    ('HOLIDAY', 'Holiday Leave', 'Special and regular holiday leave', 5, TRUE)
ON CONFLICT (leave_type_code) DO UPDATE
SET leave_type_name = EXCLUDED.leave_type_name,
    description = EXCLUDED.description,
    annual_entitlement_days = EXCLUDED.annual_entitlement_days,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP;
