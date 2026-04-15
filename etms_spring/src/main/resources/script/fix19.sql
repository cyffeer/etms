CREATE TABLE IF NOT EXISTS audit_log (
    audit_log_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    user_role VARCHAR(20) NOT NULL,
    action VARCHAR(30) NOT NULL,
    entity_type VARCHAR(60) NOT NULL,
    entity_id VARCHAR(120),
    request_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    logged_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_log_logged_at ON audit_log (logged_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_log_username ON audit_log (username);
CREATE INDEX IF NOT EXISTS idx_audit_log_entity_type ON audit_log (entity_type);
