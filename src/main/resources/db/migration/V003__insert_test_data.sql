-- Flyway Migration V003: Insert test data
-- Description: Add initial test data for development and testing

-- Step 1: Insert legal entities
INSERT INTO legal_entities (name)
VALUES ('ОООО'),
       ('b'),
       ('c');

-- Step 2: Insert employees WITHOUT specifying IDs
INSERT INTO employees (email, full_name, mattermost, online_status, password_hash, photo_url, position, profile_level,
                       role, telegram, work_status, workplace, legal_entity_id)
VALUES ('admin@mail.ru', 'Админов Админ Админович', 'admin.adminov', false,
        '$2a$10$y1GQ8zw/AAccxbv2uo429eeLjjzhGwhL3Hd06fEzYxrMkkpwL1Poi',
        'http://localhost:9000/icons/a6b105ef-f2cc-49bb-99b3-e83314c547db-icon_dragon.png', 'Разработчик', '2 level',
        'admin', '@Admin', 'in_office', 'Офис 123', 1),
       ('user@mail.ru', 'user', 'user.mattermost', false,
        '$2a$10$GQtpHbHHAf7Yn2XGlXihsO0OiuezcfYZEbMe5qW5HRvsmgVoNrPFG', NULL, 'Разработчик', '3 level', 'employee',
        '@ada', 'sick_leave', 'Офис 123', 2),
       ('ivanaa.ivanov@company.com', 'Иванaaa Иванов', 'ivanaaa.ivanov', false,
        '$2a$10$c394.8o.9cQMT9jODCnGP.5reSsh2E8BI9yd0pieMTbd2koLlfdWK', NULL, 'Разработчик', '1 level', 'employee',
        '@Ivaaaanov', 'in_office', 'Офис 123', 2);

-- Step 3: Insert departments
INSERT INTO departments (name, head_id)
VALUES ('department 1', 1),
       ('department 2', 1),
       ('department 3', 1),
       ('department 4', 1),
       ('department 55', 1),
       ('IT Department', 1),
       ('HR Department', 1);

-- Step 4: Insert subdivisions
INSERT INTO subdivisions (name, department_id, head_id)
VALUES ('Development', 1, 1),
       ('Testing', 1, 1),
       ('Recruitment', 2, 1),
       ('subdivision name', 6, 1);

-- Step 5: Update employees with department/subdivision/supervisor
UPDATE employees
SET department_id  = 1,
    subdivision_id = 2,
    supervisor_id  = 1
WHERE email = 'admin@mail.ru';
UPDATE employees
SET department_id  = 1,
    subdivision_id = 1,
    supervisor_id  = 1
WHERE email = 'user@mail.ru';
UPDATE employees
SET department_id  = 2,
    subdivision_id = 3,
    supervisor_id  = 1
WHERE email = 'ivanaa.ivanov@company.com';

-- Step 6: Insert employee profiles
INSERT INTO employee_profiles (employee_id, about_me, birthday, city, employment_status, hobbies, status_comment,
                               status_state)
VALUES (1, 'я админка', NULL, 'Питер', 'permanent', 'чтение, футбол', 'статус коммент', 'статус стате'),
       (2, 'я работник работаю', NULL, 'ekb', 'temporary', 'programming', 'статус коммент', 'статус стате'),
       (3, 'я тоже работник', NULL, 'Москва', 'permanent', 'Теннис, баскетбол', 'статус коммент', 'статус стате');

-- Step 7: Insert education
INSERT INTO education (degree, end_year, specialty, start_year, university, employee_id)
VALUES ('string', 2004, 'прикладная инфа', 1997, 'urfu', 1),
       ('string', 2027, 'программка', 2014, 'urfu', 1),
       ('string', 2021, 'ивт', 2020, 'urfu', 3),
       ('string', 2023, 'радиотехника', 2022, 'urfu', 3);

-- Step 8: Insert projects
INSERT INTO projects (confluence_url, description, name)
VALUES ('http://......', 'разработка серверной логики', 'Backend'),
       ('http://......', 'разработка клиентской части', 'frontend'),
       ('http://......', 'девопс настройка', 'devops');

-- Step 9: Insert employee projects
INSERT INTO employee_projects (employee_id, project_id)
VALUES (1, 2),
       (2, 3),
       (3, 3);

-- Step 10: Insert skills
INSERT INTO skills (name, type)
VALUES ('JavaScript', 'technical'),
       ('Java', 'string'),
       ('Читат', 'string'),
       ('писат', 'string'),
       ('string', 'string');

-- Step 11: Insert skill grade descriptions (ИСПРАВЛЕНО: skill_id только 1-5)
INSERT INTO skill_grade_descriptions (grade, description, skill_id)
VALUES (1, 'Базовый синтаксис...', 2),
       (2, 'Функции и замыкания...', 2),
       (5, 'string', 3),
       (5, 'string', 5);

-- Step 12: Insert employee skills
INSERT INTO employee_skills (confirmation_date, confirmation_document_url, confirmation_method, confirmation_status,
                             proficiency_level, employee_id, skill_id)
VALUES ('2025-05-27',
        'http://localhost:9000/skill-docs/e31fbd4a-4285-4d43-ba3e-9747ff5d97e7-_6da1b801-4f37-4d50-bd9f-86238d49ef05.jpeg',
        'certificate', 'confirmed', 4, 1, 3),
       ('2025-05-27', NULL, 'interview', 'confirmed', 5, 1, 2);

-- Step 13: Insert skill confirmation requests
INSERT INTO skill_confirmation_requests (approval_date, requested_proficiency_level, status, approver_id, employee_id,
                                         skill_id, created_date)
VALUES ('2025-05-27', 1, 'approved', 1, 1, 2, '2025-05-27'),
       ('2025-05-27', 5, 'rejected', 1, 1, 2, '2025-05-27'),
       ('2025-05-27', 4, 'rejected', 3, 1, 2, '2025-05-27'),
       ('2025-05-27', 4, 'approved', 1, 1, 3, '2025-05-27'),
       ('2025-05-25', 5, 'approved', 1, 1, 2, '2025-05-27');

-- Step 14: Insert skill suggestions
INSERT INTO skill_suggestions (approval_date, skill_name, skill_type, status, approved_by, suggested_by)
VALUES ('2025-05-27', 'Читат', 'string', 'approved', 1, 1),
       ('2025-05-27', 'писат', 'string', 'rejected', 1, 1),
       ('2025-05-27', 'string', 'string', 'rejected', 1, 1);

-- Step 15: Insert communities
INSERT INTO communities (description, name, type, creator_id)
VALUES ('описание сообщества', 'первое сообщество', 'open', 1),
       ('описание сообщества', 'closecom', 'closed', 1),
       ('описание сообщества', 'closedUsers', 'closed', 3),
       ('описание сообщества', 'strinsssssg', 'open', 1),
       ('описание сообщества', 'strinssssgfgfsg', 'closed', 1),
       ('описание сообщества', 'striавыаывng', 'open', 3),
       ('описание сообщества', 'striавыsdfsdаывng', 'open', 3),
       ('описание сообщества', 'stiывng', 'closed', 3);

-- Step 16: Insert community members
INSERT INTO community_members (role, community_id, employee_id)
VALUES ('admin', 1, 1),
       ('admin', 2, 1),
       ('member', 2, 3),
       ('member', 3, 1),
       ('admin', 3, 3),
       ('admin', 4, 1),
       ('member', 4, 3),
       ('admin', 5, 1),
       ('member', 5, 3),
       ('member', 6, 1),
       ('admin', 6, 3),
       ('admin', 7, 3),
       ('admin', 8, 3);

-- Step 17: Insert community membership requests
INSERT INTO community_membership_requests (approval_timestamp, request_timestamp, status, approver_id, community_id,
                                           employee_id)
VALUES ('2025-05-25 16:38:17.731553', '2025-05-25 16:37:02.879267', 'approved', 1, 2, 3),
       ('2025-05-25 16:45:48.333871', '2025-05-25 16:40:14.595911', 'rejected', 3, 3, 1),
       ('2025-05-25 16:46:41.628695', '2025-05-25 16:46:12.762513', 'approved', 3, 3, 1),
       ('2025-05-25 16:54:13.321312', '2025-05-25 16:53:26.14225', 'approved', 1, 5, 3),
       ('2025-05-25 18:06:35.346945', '2025-05-25 18:06:01.050638', 'approved', 3, 3, 1),
       ('2025-05-25 18:51:26.127775', '2025-05-25 18:50:51.036473', 'approved', 3, 8, 1);

-- Step 18: Insert posts
INSERT INTO posts (content, media_type, media_url, timestamp, type, community_id, employee_id)
VALUES ('Обновленный текст поста...', 'image/jpeg', 'http://example.com/new_image.jpg', '2025-05-17 16:37:25.221589',
        'discussion', NULL, 1),
       ('Привет, команда! Делюсь новостями нашего проекта...', 'image/jpeg', 'http://example.com/image.jpg',
        '2025-05-17 16:38:53.66101', 'news', NULL, 1),
       ('Привет, команда! Делюсь новостями нашего проекта...', 'image/jpeg', 'http://example.com/image.jpg',
        '2025-05-17 16:38:54.546402', 'news', NULL, 1),
       ('adasdasdПривет, команда! Делюсь новостями нашего проекта...', 'image/jpeg', 'http://example.com/image.jpg',
        '2025-05-17 17:21:29.037547', 'news', NULL, 3),
       ('sssssdПривет, команда! Делюсь новостями нашего проекта...', 'image/jpeg', 'http://example.com/image.jpg',
        '2025-05-17 17:21:40.485066', 'news', NULL, 3),
       ('aaaasssssdПривет, команда! Делюсь новостями нашего проекта...', 'image/jpeg', 'http://example.com/image.jpg',
        '2025-05-17 17:21:43.758237', 'news', NULL, 3),
       ('всем привет', 'image/jpeg',
        'http://localhost:9000/posts/005bbf9c-c60a-46f9-9a0c-e22e6d9ad47d-3da3dfb52cb282b934de498bdbc94760.jpg',
        '2025-05-23 17:24:25.284106', 'news', NULL, 1),
       ('вниимание', NULL, NULL, '2025-05-23 17:30:55.536043', 'discussion', NULL, 1),
       ('добрый дкень', 'image/png',
        'http://localhost:9000/posts/aead2d15-c49d-4ba7-8bcb-0ab329d100a3-wallhaven-rr85dm_1920x1080.png',
        '2025-05-23 17:31:46.623213', 'announcement', NULL, 1),
       ('дарова', 'image/jpeg',
        'http://localhost:9000/posts/904a2591-f5a3-49e9-aaf2-b806d3d45cee-3da3dfb52cb282b934de498bdbc94760.jpg',
        '2025-05-24 16:47:26.936751', 'announcement', NULL, 1),
       ('попопопоп', NULL, NULL, '2025-05-25 17:50:43.634795', 'discussion', 3, 3),
       ('string', NULL, NULL, '2025-05-25 18:40:30.863248', 'announcement', 1, 1),
       ('хай', NULL, NULL, '2025-05-25 18:41:04.828796', 'news', 1, 1),
       ('здравствуйте всем', NULL, NULL, '2025-05-25 18:42:59.330206', 'news', NULL, 1),
       ('string', NULL, NULL, '2025-05-25 18:46:30.87198', 'discussion', 6, 1),
       ('fghfghfgh', 'image/jpeg',
        'http://localhost:9000/posts/b47c0f10-77fa-4700-9601-3e5800f11d2c-_b9f3047e-cfb8-4622-91b9-46168ee66a10.jpeg',
        '2025-05-25 18:52:11.919442', 'news', 8, 3),
       ('string', 'image/jpeg',
        'http://localhost:9000/posts/7517ad73-b864-4adc-8db9-a2fdd2a975ef-3da3dfb52cb282b934de498bdbc94760.jpg',
        '2025-05-27 10:53:36.790619', 'news', NULL, 1);

-- Step 19: Insert comments
INSERT INTO comments (content, timestamp, employee_id, post_id)
VALUES ('Круто', '2025-05-24 17:31:04.295623', 1, 1),
       ('красиво', '2025-05-24 17:31:07.580402', 1, 1),
       ('замечательно', '2025-05-24 17:31:12.181696', 1, 1),
       ('класс', '2025-05-24 17:31:18.556377', 1, 1),
       ('коммент', '2025-05-24 17:32:25.248139', 1, 9),
       ('коммент 2', '2025-05-24 17:39:11.542973', 3, 1),
       ('коммент 3', '2025-05-24 17:43:21.285043', 3, 9),
       ('коммент 4', '2025-05-24 17:43:25.135987', 3, 9),
       ('string', '2025-05-29 17:31:02.43231', 1, 1);

-- Step 20: Insert likes
INSERT INTO likes (timestamp, employee_id, post_id)
VALUES ('2025-05-20 14:01:35.305597', 1, 1),
       ('2025-05-20 14:06:03.011822', 1, 10),
       ('2025-05-25 17:55:24.022253', 1, 16),
       ('2025-05-25 17:56:53.593405', 3, 16),
       ('2025-05-25 18:44:25.681285', 1, 17);

-- Step 21: Insert gratitude achievements
INSERT INTO gratitude_achievements (card_url, content, timestamp, type, receiver_id, sender_id)
VALUES (NULL, 'string', '2025-05-28 19:07:15.800531', 'achievement', 2, 1);
