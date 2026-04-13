-- Create the fitness_db database if it doesn't exist
SELECT 'CREATE DATABASE fitness_db'
WHERE NOT EXISTS (
    SELECT FROM pg_database WHERE datname = 'fitness_db'
)\gexec

-- Connect to fitness_db and initialise schema + seed data
\c fitness_db

DROP TABLE IF EXISTS days_of_week CASCADE;
DROP TABLE IF EXISTS workout_exericses CASCADE;
DROP TABLE IF EXISTS days CASCADE;
DROP TABLE IF EXISTS workouts CASCADE;
DROP TABLE IF EXISTS exercises CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS images CASCADE;
DROP TABLE IF EXISTS workout_sets CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(50) NOT NULL UNIQUE CHECK (char_length(username) >= 3),
    password VARCHAR(100) NOT NULL CHECK (char_length(password) > 3),
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL CHECK (char_length(name) >= 1),
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS exercises (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL CHECK (char_length(name) >= 1),
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0)
);

CREATE TABLE IF NOT EXISTS workouts (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL CHECK (char_length(name) >= 1),
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS workout_exercises (
    workout_id BIGINT NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
    exercise_id BIGINT NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
    PRIMARY KEY (workout_id, exercise_id)
);

CREATE TABLE IF NOT EXISTS days (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    date TIMESTAMP NOT NULL,
    workout_id BIGINT NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
    calories INT NOT NULL CHECK (calories > 0)
);

CREATE TABLE IF NOT EXISTS days_of_week (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    day_name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS images (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    filename VARCHAR(255) NOT NULL UNIQUE,
    path VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users (username, password, email)
VALUES ('test1', '$2a$12$WPx9OQggyMmWrepEKVfpse5gx1cVefYtt1yMcEm6HfUFTs3iZYlCy', 'test1@gmail.com');

INSERT INTO categories (name, owner_id) VALUES ('testCategory', 1);

INSERT INTO exercises (category_id, name, duration_minutes)
VALUES (1, 'Push Ups', 10),
       (1, 'Plank', 5);

INSERT INTO workouts (name, owner_id) VALUES ('Morning Routine', 1), ('Evening Stretch', 1);

INSERT INTO workout_exercises (workout_id, exercise_id) VALUES (1, 1), (1, 2), (2, 2);
INSERT INTO days_of_week(day_name) VALUES ('MONDAY'), ('TUESDAY'), ('WEDNESDAY'), ('THURSDAY'), ('FRIDAY'), ('SATURDAY'), ('SUNDAY');
