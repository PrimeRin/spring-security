-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);


INSERT INTO roles (name, description, created_at, updated_at) VALUES
    ('ROLE_ADMIN', 'Administrator with full access', NOW(), NOW()),
    ('ROLE_LIBRARIAN', 'Librarian with book management access', NOW(), NOW()),
    ('ROLE_USER', 'Regular user with read-only access', NOW(), NOW());
