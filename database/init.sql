-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS learning_platform
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE learning_platform;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'student', 'teacher') NOT NULL DEFAULT 'student',
    email VARCHAR(100) NOT NULL UNIQUE,
    avatar VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入测试数据（密码使用MD5加密）
-- 测试账号: admin / admin123 (密码的MD5: 0192023a7bbd73250516f069df18b500)
-- 测试账号: student / student123 (密码的MD5: e56a207acd1e6714735487c547c7c5d0)
INSERT INTO users (username, password, role, email, avatar) VALUES
('admin', '123456', 'admin', 'admin@example.com', NULL),
('student', '123456', 'student', 'student@example.com', NULL),
('teacher', '123456', 'teacher', 'teacher@example.com', NULL)
ON DUPLICATE KEY UPDATE username=username;

