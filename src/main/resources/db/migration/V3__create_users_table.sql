CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT,
    account_non_expired BOOLEAN DEFAULT true NOT NULL,
    account_non_locked BOOLEAN DEFAULT true NOT NULL,
    credentials_non_expired BOOLEAN DEFAULT true NOT NULL,
    enabled BOOLEAN DEFAULT true NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL
    );

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- ============================================
-- SEED USERS
-- ============================================
-- Password for all users: 'password123' (BCrypt encoded)
-- Use this password to login: password123

INSERT INTO users (username, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at, updated_at)
VALUES
    ('admin', 'admin@example.com', '$2a$12$NagzyZaSBCWCFhGCHxRN7ufEgmMmGks/BWPtXe6SJQ9GzKWX8036e', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('john_doe', 'john@example.com', '$2a$12$NagzyZaSBCWCFhGCHxRN7ufEgmMmGks/BWPtXe6SJQ9GzKWX8036e', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('jane_smith', 'jane@example.com', '$2a$12$NagzyZaSBCWCFhGCHxRN7ufEgmMmGks/BWPtXe6SJQ9GzKWX8036e', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('mike_wilson', 'mike@example.com', '$2a$12$NagzyZaSBCWCFhGCHxRN7ufEgmMmGks/BWPtXe6SJQ9GzKWX8036e', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('disabled_user', 'disabled@example.com', '$2a$12$NagzyZaSBCWCFhGCHxRN7ufEgmMmGks/BWPtXe6SJQ9GzKWX8036e', true, true, true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('locked_user', 'locked@example.com', '$2a$12$NagzyZaSBCWCFhGCHxRN7ufEgmMmGks/BWPtXe6SJQ9GzKWX8036e', true, false, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

UPDATE users
SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE username = 'admin';

UPDATE users
SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER')
WHERE username = 'john_doe';