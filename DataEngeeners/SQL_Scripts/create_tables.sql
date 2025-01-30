-- Создание таблицы users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    city VARCHAR NOT NULL,
    phone_number VARCHAR NOT NULL UNIQUE,
    email VARCHAR NOT NULL UNIQUE,
    telegram_id BIGINT UNIQUE,
    chat_id BIGINT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    role VARCHAR NOT NULL
);

-- Создание таблицы admin_users
CREATE TABLE admin_users (
    id SERIAL PRIMARY KEY,
    username VARCHAR NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    role VARCHAR NOT NULL DEFAULT 'admin'
);

-- Создание таблицы applications
CREATE TABLE applications (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    direction_id INT,
    type VARCHAR NOT NULL,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status VARCHAR NOT NULL
);

-- Создание таблицы button_activity
CREATE TABLE button_activity (
    id SERIAL PRIMARY KEY,
    button_type VARCHAR NOT NULL,
    end_date DATE NOT NULL,
    start_date DATE NOT NULL
);

-- Создание таблицы directions
CREATE TABLE directions (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL
);

-- Создание таблицы events
CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    event_type VARCHAR NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    details JSON NOT NULL
);

-- Создание таблицы message_templates
CREATE TABLE message_templates (
    id SERIAL PRIMARY KEY,
    message_type VARCHAR NOT NULL,
    message_content TEXT NOT NULL
);

-- Добавление внешнего ключа для таблицы applications (user_id)
ALTER TABLE applications
ADD CONSTRAINT applications_fk1 FOREIGN KEY (user_id)
REFERENCES users (id)
ON DELETE CASCADE;

-- Добавление внешнего ключа для таблицы applications (direction_id)
ALTER TABLE applications
ADD CONSTRAINT applications_fk2 FOREIGN KEY (direction_id)
REFERENCES directions (id)
ON DELETE SET NULL;

-- Добавление внешнего ключа для таблицы events (user_id)
ALTER TABLE events
ADD CONSTRAINT events_fk1 FOREIGN KEY (user_id)
REFERENCES users (id)
ON DELETE CASCADE;
