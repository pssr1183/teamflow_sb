CREATE TABLE IF NOT EXISTS  roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Clear existing data (optional - fresh start)
TRUNCATE roles CASCADE;
TRUNCATE permissions CASCADE;
TRUNCATE role_permissions CASCADE;

-- Seed Permissions
INSERT INTO permissions (name) VALUES
('READ'),
('WRITE'),
('DELETE'),
('TASK_CREATE'),
('TASK_UPDATE'),
('TASK_DELETE'),
('TASK_VIEW_ALL'),
('TASK_ASSIGN'),
('TASK_VIEW_ASSIGNED'),
('TASK_UPDATE_OWN'),
('TASK_UPDATE_ALL');

-- Seed Roles
INSERT INTO roles (name) VALUES
('Admin'),
('Manager'),
('Member'),
('User'),
('Reviewer'),
('Developer'),
('Employee'),
('Role1');

-- Seed Role-Permissions (example mappings - adjust as needed)
INSERT INTO role_permissions (role_id, permission_id) VALUES
-- Admin: Full access
((SELECT id FROM roles WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'TASK_VIEW_ALL')),
((SELECT id FROM roles WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'TASK_CREATE')),
((SELECT id FROM roles WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'TASK_UPDATE')),
((SELECT id FROM roles WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'TASK_DELETE')),
((SELECT id FROM roles WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'TASK_ASSIGN')),
((SELECT id FROM roles WHERE name = 'Admin'), (SELECT id FROM permissions WHERE name = 'TASK_UPDATE_ALL')),
-- User: Limited access
((SELECT id FROM roles WHERE name = 'User'), (SELECT id FROM permissions WHERE name = 'TASK_CREATE')),
((SELECT id FROM roles WHERE name = 'User'), (SELECT id FROM permissions WHERE name = 'TASK_VIEW_ASSIGNED')),
((SELECT id FROM roles WHERE name = 'User'), (SELECT id FROM permissions WHERE name = 'TASK_UPDATE_OWN')),
-- Manager: Moderate access
((SELECT id FROM roles WHERE name = 'Manager'), (SELECT id FROM permissions WHERE name = 'TASK_VIEW_ALL')),
((SELECT id FROM roles WHERE name = 'Manager'), (SELECT id FROM permissions WHERE name = 'TASK_ASSIGN'));