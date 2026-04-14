DELETE FROM skills_inventory si
WHERE NOT EXISTS (
    SELECT 1 FROM employees e WHERE e.emp_no = si.employee_number
)
OR NOT EXISTS (
    SELECT 1 FROM skills s WHERE s.skill_id = si.skill_id
)
OR NOT EXISTS (
    SELECT 1 FROM skill_lvl sl WHERE sl.skill_lvl_id = si.skill_lvl_id
);