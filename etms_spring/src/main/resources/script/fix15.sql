ALTER TABLE login
    ALTER COLUMN password TYPE VARCHAR(255);

ALTER TABLE login
    ADD COLUMN IF NOT EXISTS role VARCHAR(20);

UPDATE login
SET role = CASE lower(user_id)
    WHEN 'admin' THEN 'ADMIN'
    WHEN 'alice' THEN 'HR'
    WHEN 'brian' THEN 'MANAGER'
    ELSE COALESCE(role, 'EMPLOYEE')
END
WHERE role IS NULL
   OR trim(role) = '';

INSERT INTO login (user_id, password, role)
SELECT 'employee', 'employee123', 'EMPLOYEE'
WHERE NOT EXISTS (
    SELECT 1
    FROM login
    WHERE lower(user_id) = 'employee'
);

ALTER TABLE login
    ALTER COLUMN role SET NOT NULL;

ALTER TABLE login
    ALTER COLUMN role SET DEFAULT 'EMPLOYEE';
