ALTER TABLE users
DROP COLUMN role;

ALTER TABLE users
ADD COLUMN role_id BIGINT;

ALTER TABLE users
ADD CONSTRAINT fk_users_role
FOREIGN KEY (role_id) REFERENCES roles (id)
ON DELETE SET NULL;