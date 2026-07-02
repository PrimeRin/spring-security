-- Create permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );

-- Create role_permissions junction table
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
    );

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

INSERT INTO permissions (name, description, created_at, updated_at) VALUES
        ('BOOK_CREATE', 'Create new books', NOW(), NOW()),
        ('BOOK_READ', 'View books', NOW(), NOW()),
        ('BOOK_UPDATE', 'Update existing books', NOW(), NOW()),
        ('BOOK_DELETE', 'Delete books', NOW(), NOW()),
        ('USER_CREATE', 'Create new users', NOW(), NOW()),
        ('USER_READ', 'View users', NOW(), NOW()),
        ('USER_UPDATE', 'Update users', NOW(), NOW()),
        ('USER_DELETE', 'Delete users', NOW(), NOW()),
        ('ADMIN_ACCESS', 'Administrator access', NOW(), NOW()),
        ('SYSTEM_CONFIG', 'Configure system settings', NOW(), NOW());

INSERT INTO role_permissions (role_id, permission_id)
SELECT
    (SELECT id FROM roles WHERE name = 'ROLE_LIBRARIAN'),
    id
FROM permissions
WHERE name IN ('BOOK_CREATE', 'BOOK_READ', 'BOOK_UPDATE', 'BOOK_DELETE');

-- Assign permissions to ROLE_USER
INSERT INTO role_permissions (role_id, permission_id)
SELECT
    (SELECT id FROM roles WHERE name = 'ROLE_USER'),
    id
FROM permissions
WHERE name = 'BOOK_READ';