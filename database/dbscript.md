```mysql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS learning_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE learning_platform;

-- 用户表：存储所有角色用户（管理员、学生、教师）
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，主键，自增',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名，唯一',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    role ENUM('admin', 'student', 'teacher') NOT NULL COMMENT '角色：admin-管理员, student-学生, teacher-教师',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像路径',
    profile TEXT COMMENT '简介（仅教师使用）',
    title VARCHAR(50) COMMENT '职称（仅教师使用）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 课程表：存储课程信息
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID，主键，自增',
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    teacher_id INT COMMENT '授课教师ID（简化单教师，实际可通过teacher_courses扩展）',
    description TEXT COMMENT '课程描述',
    college VARCHAR(100) COMMENT '开课学院',
    visibility ENUM('class_only', 'all') DEFAULT 'all' COMMENT '可见性：class_only-仅本班, all-全部学生',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

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

-- 问题表：存储学生提出的问题
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '问题ID，主键，自增',
    title VARCHAR(100) NOT NULL COMMENT '问题标题',
    content TEXT NOT NULL COMMENT '问题内容',
    attachment VARCHAR(255) COMMENT '附件路径（e.g., 图片）',
    course_id INT NOT NULL COMMENT '所属课程ID',
    student_id INT NOT NULL COMMENT '提问学生ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题表';

-- 回答表：存储教师的回答
CREATE TABLE IF NOT EXISTS answers (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '回答ID，主键，自增',
    content TEXT NOT NULL COMMENT '回答内容',
    attachment VARCHAR(255) COMMENT '附件路径（e.g., 图片）',
    question_id INT NOT NULL COMMENT '所属问题ID',
    teacher_id INT NOT NULL COMMENT '回答教师ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- 教师-课程中间表：处理教师与课程的多对多关系
CREATE TABLE IF NOT EXISTS teacher_courses (
    teacher_id INT NOT NULL COMMENT '教师ID',
    course_id INT NOT NULL COMMENT '课程ID',
    PRIMARY KEY (teacher_id, course_id),
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师-课程关联表';

-- 通知表：存储用户通知（如新回答提醒）
CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID，主键，自增',
    user_id INT NOT NULL COMMENT '接收用户ID',
    message VARCHAR(255) NOT NULL COMMENT '通知消息（e.g., 您有X条新回答）',
    type ENUM('answer', 'question') NOT NULL COMMENT '类型：answer-回答通知, question-问题通知',
    related_id INT COMMENT '关联ID（e.g., question_id）',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
```