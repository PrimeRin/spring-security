-- V2__create_books_table.sql
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    published_year INTEGER,
    genre VARCHAR(100),
    description TEXT,
    price DECIMAL(10, 2),
    stock_quantity INTEGER DEFAULT 0,
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_books_title ON books(title);

INSERT INTO books (title, author, isbn, published_year, genre, description, price, stock_quantity, created_by, created_at, updated_at)
VALUES
    ('Clean Code: A Handbook of Agile Software Craftsmanship',
     'Robert C. Martin',
     '9780132350884',
     2008,
     'Programming',
     'Even bad code can function. But if code isnt clean, it can bring a development organization to its knees.',
     39.99,
     50,
     'admin',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),

    ('Effective Java',
     'Joshua Bloch',
     '9780134685991',
     2018,
     'Programming',
     'The definitive guide to Java programming language best practices.',
     49.99,
     35,
     'admin',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),

    ('The Pragmatic Programmer',
     'David Thomas, Andrew Hunt',
     '9780135957059',
     2019,
     'Programming',
     'One of those rare tech books youll read, re-read, and read again over the years.',
     45.99,
     40,
     'john_doe',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP),

    ('Head First Design Patterns',
     'Eric Freeman, Elisabeth Robson',
     '9780596007126',
     2004,
     'Programming',
     'Shows you the patterns that matter, when to use them, why, and how to apply them.',
     54.99,
     25,
     'jane_smith',
     CURRENT_TIMESTAMP,
     CURRENT_TIMESTAMP);
