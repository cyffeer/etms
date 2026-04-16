UPDATE dept_members
SET mbr_type_id = CASE emp_no
    WHEN 1001 THEN 1
    WHEN 1002 THEN 2
    WHEN 1003 THEN 3
    WHEN 1004 THEN 1
    ELSE mbr_type_id
END
WHERE emp_no IN (1001, 1002, 1003, 1004);
