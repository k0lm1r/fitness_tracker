CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(50) NOT NULL UNIQUE CHECK(CHAR_LENGTH(username) >= 3),
    password VARCHAR(100) NOT NULL CHECK(CHAR_LENGTH(password) > 3),
    email VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    owner_id INT REFERENCES users(id) ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS workouts (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category_id INT REFERENCES categories(id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    owner_id INT REFERENCES users(id) ON DELETE SET NULL,
    workout_date DATE NOT NULL CHECK(workout_date <= CURRENT_DATE),
    duration_minutes INT NOT NULL CHECK(duration_minutes > 0),
    calories INT NOT NULL CHECK(calories > 0)
);
CREATE TABLE IF NOT EXISTS images (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    filename VARCHAR(50) NOT NULL UNIQUE,
    path VARCHAR(50) NOT NULL,
    owner_id INT REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS workout_sets (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(100) NOT NULL,
    owner_id INT REFERENCES users(id) ON DELETE SET NULL
);


INSERT INTO users (username, password, email) VALUES ('test1', '$2a$12$WPx9OQggyMmWrepEKVfpse5gx1cVefYtt1yMcEm6HfUFTs3iZYlCy', 'test1@gmail.com');
INSERT INTO categories (name, owner_id) VALUES ('testCategory', 1);
INSERT INTO workout_sets (name, owner_id) VALUES ('set1', 1);


DO $$
BEGIN
    FOR i IN 1..50 LOOP
        INSERT INTO workouts (category_id, name, owner_id, workout_date, duration_minutes, calories)
        VALUES (1, 'workout' || i, 1, CURRENT_DATE, 1, 1);
    END LOOP;
END $$;