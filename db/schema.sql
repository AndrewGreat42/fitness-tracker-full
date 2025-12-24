-- GreatFitness schema (MySQL)

CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  gender VARCHAR(20) NOT NULL,
  age INT NOT NULL,
  weight DOUBLE NOT NULL,
  height DOUBLE NOT NULL,
  activity_level DOUBLE NOT NULL,
  goal VARCHAR(30) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS meals (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  description VARCHAR(255) NOT NULL,
  calories INT NOT NULL,
  protein DOUBLE NOT NULL,
  fat DOUBLE NOT NULL,
  carbs DOUBLE NOT NULL,
  eaten_at DATETIME NOT NULL,
  CONSTRAINT fk_meals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_meals_user_date (user_id, eaten_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS weight_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  weight DOUBLE NOT NULL,
  logged_at DATETIME NOT NULL,
  CONSTRAINT fk_weight_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_weight_user_date (user_id, logged_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token VARCHAR(64) NOT NULL UNIQUE,
  created_at DATETIME NOT NULL,
  expires_at DATETIME NOT NULL,
  CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_sessions_token (token)
) ENGINE=InnoDB;
