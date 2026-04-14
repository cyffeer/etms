-- Populate leave_records from leaves without mixed-type COALESCE
-- Run once (or after TRUNCATE if you want a clean reload)

INSERT INTO leave_records (employee_number, start_date, end_date, leave_type, status, remarks)
SELECT emp_no::text, vl_date, vl_date, 'VACATION', 'APPROVED', NULL
FROM leaves
WHERE vl_date IS NOT NULL

UNION ALL
SELECT emp_no::text, sl_date, sl_date, 'SICK', 'APPROVED', NULL
FROM leaves
WHERE sl_date IS NOT NULL

UNION ALL
SELECT emp_no::text, hl_date, hl_date, 'HOLIDAY', 'APPROVED', NULL
FROM leaves
WHERE hl_date IS NOT NULL;