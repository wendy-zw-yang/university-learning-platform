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

-- 课程表：存储课程信息
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID，主键，自增',
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    description TEXT COMMENT '课程描述',
    department VARCHAR(100) COMMENT '开课学院',
    visibility ENUM('class_only', 'all') DEFAULT 'all' COMMENT '可见性：class_only-仅本班, all-全部学生',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 教师-课程中间表：处理教师与课程的多对多关系
CREATE TABLE IF NOT EXISTS teacher_courses (
    teacher_id INT NOT NULL COMMENT '教师ID',
    course_id INT NOT NULL COMMENT '课程ID',
    PRIMARY KEY (teacher_id, course_id),
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师-课程关联表';


-- 学习资源表：存储上传的资源
CREATE TABLE IF NOT EXISTS resources (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID，主键，自增',
    title VARCHAR(100) NOT NULL COMMENT '资源标题',
    description TEXT COMMENT '资源简介',
    file_path VARCHAR(255) NOT NULL COMMENT '文件路径（e.g., /uploads/resource.pdf）',
    course_id INT NOT NULL COMMENT '所属课程ID',
    uploader_id INT NOT NULL COMMENT '上传者ID',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资源表';

-- 插入测试数据（密码使用MD5加密）
-- 测试账号: admin / admin123 (密码的MD5: 0192023a7bbd73250516f069df18b500)
-- 测试账号: student / student123 (密码的MD5: e56a207acd1e6714735487c547c7c5d0)
INSERT INTO users (username, password, role, email, avatar) VALUES
('admin', '123456', 'admin', 'admin@example.com', NULL),
('student', '123456', 'student', 'student@example.com', NULL),
('teacher', '123456', 'teacher', 'teacher@example.com', NULL)
ON DUPLICATE KEY UPDATE username=username;



