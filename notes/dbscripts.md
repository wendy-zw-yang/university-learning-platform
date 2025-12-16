``` mysql
USE ulpdb;
-- 创建用户表
create table users (
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，主键，自增',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名，唯一',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    role VARCHAR(10) NOT NULL COMMENT '角色：admin-管理员, student-学生, teacher-教师',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '别名',
    profile TEXT COMMENT '简介（仅教师使用）',
    title VARCHAR(50) COMMENT '职称（仅教师使用）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

select * from users;
DELETE from users
```