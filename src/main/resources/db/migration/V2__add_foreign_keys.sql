-- Flyway Migration V2: Add foreign key constraints
-- Description: Create relationships between tables

-- Comments foreign keys
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(post_id);

-- Communities foreign keys
ALTER TABLE communities
    ADD CONSTRAINT fk_communities_creator FOREIGN KEY (creator_id) REFERENCES employees(employee_id);

-- Community members foreign keys
ALTER TABLE community_members
    ADD CONSTRAINT fk_community_members_community FOREIGN KEY (community_id) REFERENCES communities(community_id);

ALTER TABLE community_members
    ADD CONSTRAINT fk_community_members_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

-- Community membership requests foreign keys
ALTER TABLE community_membership_requests
    ADD CONSTRAINT fk_membership_requests_approver FOREIGN KEY (approver_id) REFERENCES employees(employee_id);

ALTER TABLE community_membership_requests
    ADD CONSTRAINT fk_membership_requests_community FOREIGN KEY (community_id) REFERENCES communities(community_id);

ALTER TABLE community_membership_requests
    ADD CONSTRAINT fk_membership_requests_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

-- Departments foreign keys
ALTER TABLE departments
    ADD CONSTRAINT fk_departments_head FOREIGN KEY (head_id) REFERENCES employees(employee_id);

-- Subdivisions foreign keys
ALTER TABLE subdivisions
    ADD CONSTRAINT fk_subdivisions_department FOREIGN KEY (department_id) REFERENCES departments(department_id);

ALTER TABLE subdivisions
    ADD CONSTRAINT fk_subdivisions_head FOREIGN KEY (head_id) REFERENCES employees(employee_id);

-- Education foreign keys
ALTER TABLE education
    ADD CONSTRAINT fk_education_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

-- Employees foreign keys
ALTER TABLE employees
    ADD CONSTRAINT fk_employees_department FOREIGN KEY (department_id) REFERENCES departments(department_id);

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_legal_entity FOREIGN KEY (legal_entity_id) REFERENCES legal_entities(legal_entity_id);

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_subdivision FOREIGN KEY (subdivision_id) REFERENCES subdivisions(subdivision_id);

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_supervisor FOREIGN KEY (supervisor_id) REFERENCES employees(employee_id);

-- Employee profiles foreign keys
ALTER TABLE employee_profiles
    ADD CONSTRAINT fk_employee_profiles_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

-- Employee projects foreign keys
ALTER TABLE employee_projects
    ADD CONSTRAINT fk_employee_projects_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

ALTER TABLE employee_projects
    ADD CONSTRAINT fk_employee_projects_project FOREIGN KEY (project_id) REFERENCES projects(project_id);

-- Employee skills foreign keys
ALTER TABLE employee_skills
    ADD CONSTRAINT fk_employee_skills_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

ALTER TABLE employee_skills
    ADD CONSTRAINT fk_employee_skills_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id);

-- Gratitude achievements foreign keys
ALTER TABLE gratitude_achievements
    ADD CONSTRAINT fk_gratitude_achievements_receiver FOREIGN KEY (receiver_id) REFERENCES employees(employee_id);

ALTER TABLE gratitude_achievements
    ADD CONSTRAINT fk_gratitude_achievements_sender FOREIGN KEY (sender_id) REFERENCES employees(employee_id);

-- Likes foreign keys
ALTER TABLE likes
    ADD CONSTRAINT fk_likes_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

ALTER TABLE likes
    ADD CONSTRAINT fk_likes_post FOREIGN KEY (post_id) REFERENCES posts(post_id);

-- Posts foreign keys
ALTER TABLE posts
    ADD CONSTRAINT fk_posts_community FOREIGN KEY (community_id) REFERENCES communities(community_id);

ALTER TABLE posts
    ADD CONSTRAINT fk_posts_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

-- Skill confirmation requests foreign keys
ALTER TABLE skill_confirmation_requests
    ADD CONSTRAINT fk_skill_confirmation_approver FOREIGN KEY (approver_id) REFERENCES employees(employee_id);

ALTER TABLE skill_confirmation_requests
    ADD CONSTRAINT fk_skill_confirmation_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id);

ALTER TABLE skill_confirmation_requests
    ADD CONSTRAINT fk_skill_confirmation_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id);

-- Skill grade descriptions foreign keys
ALTER TABLE skill_grade_descriptions
    ADD CONSTRAINT fk_skill_grade_descriptions_skill FOREIGN KEY (skill_id) REFERENCES skills(skill_id);

-- Skill suggestions foreign keys
ALTER TABLE skill_suggestions
    ADD CONSTRAINT fk_skill_suggestions_approved_by FOREIGN KEY (approved_by) REFERENCES employees(employee_id);

ALTER TABLE skill_suggestions
    ADD CONSTRAINT fk_skill_suggestions_suggested_by FOREIGN KEY (suggested_by) REFERENCES employees(employee_id);
