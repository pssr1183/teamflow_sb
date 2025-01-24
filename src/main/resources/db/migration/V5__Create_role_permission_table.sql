CREATE TABLE role_permissions (
    id SERIAL PRIMARY KEY,
    role_id BIGINT not null references roles(id),
    permission_id BIGINT not null references permissions(id)
);