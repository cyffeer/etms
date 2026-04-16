INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1001', 'PROMOTION', 'Promotion to Senior Engineering Director', 'Recognized for leading the platform modernization program', 'D001', 'PR-1001', CURRENT_DATE - 320, NULL, 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'PR-1001'
);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1002', 'CITATION', 'Service Excellence Citation', 'Recognized for outstanding onboarding support', 'D002', 'CT-1002', CURRENT_DATE - 260, NULL, 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'CT-1002'
);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1003', 'PROJECT_ASSIGNMENT', 'Finance Automation Workstream', 'Assigned to lead the finance automation rollout', 'D003', 'PA-1003', CURRENT_DATE - 210, CURRENT_DATE + 90, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'PA-1003'
);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1004', 'PROMOTION', 'Promotion to Lead Developer', 'Promotion after delivery of the quarterly integration release', 'D001', 'PR-1004', CURRENT_DATE - 180, NULL, 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'PR-1004'
);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1005', 'RESIGNATION', 'Resignation Request', 'Employee submitted resignation effective at month end', 'D001', 'RS-1005', CURRENT_DATE - 30, CURRENT_DATE + 15, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'RS-1005'
);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1006', 'SUSPENSION', 'Temporary Suspension', 'Pending investigation following repeated policy violations', 'D001', 'SP-1006', CURRENT_DATE - 20, CURRENT_DATE - 13, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'SP-1006'
);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1007', 'PROJECT_ASSIGNMENT', 'APAC Rollout Support', 'Assigned as backup lead for the APAC deployment', 'D003', 'PA-1007', CURRENT_DATE - 90, CURRENT_DATE + 60, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'PA-1007'
);

INSERT INTO employee_event (
    emp_no,
    event_type,
    title,
    description,
    department_code,
    reference_code,
    effective_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT '1008', 'TERMINATION', 'Termination Notice', 'Separation after escalation review', 'D002', 'TM-1008', CURRENT_DATE - 10, CURRENT_DATE + 10, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM employee_event WHERE reference_code = 'TM-1008'
);
