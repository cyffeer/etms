CREATE TABLE IF NOT EXISTS leave_types (
    leave_type_id BIGSERIAL PRIMARY KEY,
    leave_type_code VARCHAR(30) NOT NULL UNIQUE,
    leave_type_name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO leave_types (leave_type_code, leave_type_name, description, active)
VALUES
    ('VACATION', 'Vacation Leave', 'Scheduled vacation and annual leave', TRUE),
    ('SICK', 'Sick Leave', 'Medical and wellness leave', TRUE),
    ('HOLIDAY', 'Holiday Leave', 'Special and regular holiday leave', TRUE)
ON CONFLICT (leave_type_code) DO UPDATE
SET leave_type_name = EXCLUDED.leave_type_name,
    description = EXCLUDED.description,
    active = EXCLUDED.active,
    updated_at = CURRENT_TIMESTAMP;
