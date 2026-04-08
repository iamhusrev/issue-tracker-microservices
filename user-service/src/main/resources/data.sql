INSERT INTO roles(insert_date_time, insert_user_id, is_deleted, last_update_date_time, last_update_user_id, description)
SELECT '2022-01-05 00:00:00', 1, false, '2022-01-05 00:00:00', 1, 'Admin'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE description = 'Admin');

INSERT INTO roles(insert_date_time, insert_user_id, is_deleted, last_update_date_time, last_update_user_id, description)
SELECT '2022-01-05 00:00:00', 1, false, '2022-01-05 00:00:00', 1, 'Manager'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE description = 'Manager');

INSERT INTO roles(insert_date_time, insert_user_id, is_deleted, last_update_date_time, last_update_user_id, description)
SELECT '2022-01-05 00:00:00', 1, false, '2022-01-05 00:00:00', 1, 'Employee'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE description = 'Employee');
