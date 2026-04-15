CREATE TABLE IF NOT EXISTS employee_event (
    employee_event_id BIGSERIAL PRIMARY KEY,
    emp_no VARCHAR(30) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000),
    department_code VARCHAR(30),
    reference_code VARCHAR(50),
    effective_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_employee_event_emp_no ON employee_event (emp_no);
CREATE INDEX IF NOT EXISTS idx_employee_event_type ON employee_event (event_type);
CREATE INDEX IF NOT EXISTS idx_employee_event_effective_date ON employee_event (effective_date);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    status
)
SELECT
    employee_code,
    'PROJECT_ASSIGNMENT',
    'Initial assignment to ' || department_name,
    'Seeded monitoring record for existing employee and department membership.',
    department_code,
    'SEED-' || employee_id,
    COALESCE(hire_date, CURRENT_DATE),
    'ACTIVE'
FROM employees e
JOIN department d ON d.department_id = (
    SELECT department_id
    FROM department
    ORDER BY department_id
    FETCH FIRST 1 ROW ONLY
)
WHERE NOT EXISTS (
    SELECT 1
    FROM employee_event ee
    WHERE ee.emp_no = e.employee_code
      AND ee.event_type = 'PROJECT_ASSIGNMENT'
);
