# 课程管理和教师管理模块 - 使用说明

## 概述

本项目实现了大学生线上学习资源共享与问答系统的两个核心管理模块：
1. **课程管理模块**：管理员可以对课程进行增删改查操作
2. **教师管理模块**：管理员可以对教师进行管理，并为教师分配课程

## 项目结构

```
src/main/java/com/ulp/
├── bean/                          # 数据模型层
│   ├── UserModel.java            # 用户基础模型（包含id、用户名、密码、角色等）
│   ├── StudentModel.java         # 学生模型（继承UserModel）
│   ├── TeacherModel.java         # 教师模型（继承UserModel）
│   ├── AdminModel.java           # 管理员模型
│   ├── CourseModel.java          # 课程模型
│   ├── QuestionModel.java        # 问题模型
│   ├── AnswerModel.java          # 回答模型
│   ├── ResourceModel.java        # 资源模型
│   ├── NotificationModel.java    # 通知模型
│   ├── CourseWithQuestionCount.java # 带问题计数的课程模型
│   └── QuestionWithAnswers.java  # 带回答的问题模型
├── dao/                          # 数据访问层接口
│   ├── UserDao.java              # 用户数据访问接口
│   ├── CourseDao.java            # 课程数据访问接口
│   ├── QuestionDao.java          # 问题数据访问接口
│   ├── AnswerDao.java            # 回答数据访问接口
│   ├── ResourceDao.java          # 资源数据访问接口
│   ├── NotificationDao.java      # 通知数据访问接口
│   ├── StudentCourseDao.java     # 学生课程数据访问接口
│   ├── TeacherDao.java           # 教师数据访问接口
│   ├── AdminDao.java             # 管理员数据访问接口
│   └── impl/                     # 数据访问层实现
│       ├── UserDaoImpl.java      # 用户数据访问实现
│       ├── CourseDaoImpl.java    # 课程数据访问实现
│       ├── QuestionDaoImpl.java  # 问题数据访问实现
│       ├── AnswerDaoImpl.java    # 回答数据访问实现
│       ├── ResourceDaoImpl.java  # 资源数据访问实现
│       ├── NotificationDaoImpl.java # 通知数据访问实现
│       ├── StudentCourseDaoImpl.java # 学生课程数据访问实现
│       ├── TeacherDaoImpl.java   # 教师数据访问实现
│       └── AdminDaoImpl.java     # 管理员数据访问实现
├── service/                      # 业务逻辑层
│   ├── UserService.java          # 用户服务（CRUD操作）
│   ├── AuthService.java          # 认证服务（登录验证等）
│   ├── CourseService.java        # 课程服务（CRUD操作）
│   ├── TeacherService.java       # 教师服务（CRUD和课程分配）
│   ├── QuestionService.java      # 问题服务（问题管理）
│   ├── AnswerService.java        # 回答服务（回答管理）
│   ├── ResourceService.java      # 资源服务（资源管理）
│   ├── NotificationService.java  # 通知服务（通知管理）
│   ├── StudentCourseService.java # 学生课程服务（选课管理）
│   └── ProfileService.java       # 个人资料服务（资料更新）
├── servlet/                      # 控制器层
│   ├── LoginServlet.java         # 登录控制器
│   ├── LogoutServlet.java        # 登出控制器
│   ├── RegisterServlet.java      # 注册控制器
│   ├── HomepageServlet.java      # 首页控制器
│   ├── ProfileServlet.java       # 个人资料控制器
│   ├── AdminCourseServlet.java   # 管理员课程控制器
│   ├── TeacherServlet.java       # 教师管理控制器
│   ├── QuestionServlet.java      # 问题控制器（学生端）
│   ├── TeacherQuestionServlet.java # 教师问题控制器
│   ├── StudentCourseServlet.java # 学生课程控制器
│   ├── StudentQuestionServlet.java # 学生提问控制器
│   ├── TeacherResourceServlet.java # 教师资源控制器
│   ├── AdminResourceServlet.java # 管理员资源控制器
│   ├── StudentResourceServlet.java # 学生资源控制器
│   ├── StudentServlet.java       # 学生控制器
│   ├── TeacherCourseServlet.java # 教师课程控制器
│   ├── TeacherEditResourceServlet.java # 教师编辑资源控制器
│   ├── StudentEditResourceServlet.java # 学生编辑资源控制器
│   ├── AdminQuestionServlet.java # 管理员问题控制器
│   ├── StudentCenterResourceServlet.java # 学生中心资源控制器
│   ├── StudentCenterQuestionServlet.java # 学生中心问题控制器
│   ├── AdminResourcePreviewServlet.java # 管理员资源预览控制器
│   ├── StudentResourcePreviewServlet.java # 学生资源预览控制器
│   ├── StudentCenterResourcePreviewServlet.java # 学生中心资源预览控制器
│   └── NotificationServlet.java  # 通知控制器
├── filter/                       # 过滤器
│   ├── AuthenticationFilter.java # 认证过滤器
│   └── RoleAuthorizationFilter.java # 角色授权过滤器
└── util/                         # 工具类
    └── PasswordUtil.java         # 密码工具类（加密解密等）

src/main/webapp/                  # 视图层（JSP页面）
├── css/                          # 样式文件
│   └── common.css                # 公共样式
├── WEB-INF/                      # Web应用配置
│   └── web.xml                   # Web应用配置文件
├── login.jsp                     # 登录页面
├── register.jsp                  # 注册页面
├── dashboard.jsp                 # 仪表盘页面
├── profile.jsp                   # 个人资料页面
├── navbar.jsp                    # 导航栏页面
├── courses.jsp                   # 课程列表页面
├── edit_course.jsp               # 课程编辑/添加页面
├── teachers.jsp                  # 教师列表页面
├── edit_teacher.jsp              # 教师编辑/添加页面
├── student_courses.jsp           # 学生课程页面
├── course_question.jsp           # 课程问题页面
├── ask_question.jsp              # 提问页面
├── answer_question.jsp           # 回答问题页面
├── edit_answer.jsp               # 编辑回答页面
├── student_resource.jsp          # 学生资源页面
├── teacher_resource.jsp          # 教师资源页面
├── admin_resource.jsp            # 管理员资源页面
├── admin_edit_resource.jsp       # 管理员编辑资源页面
├── teacher_upload_resource.jsp   # 教师上传资源页面
├── teacher_edit_resources.jsp    # 教师编辑资源页面
├── teacher_courses.jsp           # 教师课程页面
├── teacher_edit_course.jsp       # 教师编辑课程页面
├── admin_homepage.jsp            # 管理员首页
├── teacher_homepage.jsp          # 教师首页
├── student_homepage.jsp          # 学生首页
├── student_center.jsp            # 学生中心页面
├── student_center_edit_question.jsp # 学生中心编辑问题页面
├── student_center_edit_resource.jsp # 学生中心编辑资源页面
├── student_center_resource_preview.jsp # 学生中心资源预览页面
├── student_resource_preview.jsp  # 学生资源预览页面
├── admin_resource_preview.jsp    # 管理员资源预览页面
├── admin_course_question.jsp     # 管理员课程问题页面
└── 其他JSP页面...

```

## 功能说明

### 1. 课程管理模块

#### 功能特性
- ✅ 查看所有课程列表
- ✅ 添加新课程
- ✅ 编辑课程信息
- ✅ 删除课程
- ✅ 支持设置课程可见性（全部学生/仅本班）
- ✅ 关联授课教师

#### 访问路径
- 课程列表：`http://localhost:8080/项目名/admin/courses`
- 添加课程：`http://localhost:8080/项目名/admin/courses?action=add`
- 编辑课程：`http://localhost:8080/项目名/admin/courses?action=edit&id=课程ID`

#### 数据库表结构
```sql
CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    teacher_id INT,
    description TEXT,
    college VARCHAR(100),
    visibility ENUM('class_only', 'all') DEFAULT 'all',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);
```

### 2. 教师管理模块

#### 功能特性
- ✅ 查看所有教师列表
- ✅ 添加新教师
- ✅ 编辑教师信息
- ✅ 删除教师
- ✅ 为教师分配多门课程
- ✅ 查看教师授课数量
- ✅ 管理教师个人信息（职称、简介、头像等）

#### 访问路径
- 教师列表：`http://localhost:8080/项目名/admin/teachers`
- 添加教师：`http://localhost:8080/项目名/admin/teachers?action=add`
- 编辑教师：`http://localhost:8080/项目名/admin/teachers?action=edit&id=教师ID`

#### 数据库表结构
```sql
-- 教师信息存储在users表中（role='teacher'）
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'student', 'teacher') NOT NULL,
    email VARCHAR(100),
    avatar VARCHAR(255),
    profile TEXT,
    title VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 教师-课程多对多关系表
CREATE TABLE teacher_courses (
    teacher_id INT NOT NULL,
    course_id INT NOT NULL,
    PRIMARY KEY (teacher_id, course_id),
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);
```

## 技术架构

### MVC模式
- **Model（模型层）**：JavaBean类，定义数据结构
  - `CourseModel`：课程数据
  - `TeacherModel`：教师数据（继承UserModel）
  
- **View（视图层）**：JSP页面，负责UI展示
  - 使用内联CSS样式，美观的响应式设计
  - 支持操作提示和错误信息显示
  
- **Controller（控制器层）**：Servlet类，处理HTTP请求
  - `CourseController`：处理课程相关请求
  - `TeacherController`：处理教师相关请求

### Service层
- 封装业务逻辑，与数据库交互
- 提供可复用的服务方法
- 支持事务处理和错误处理

## 使用说明

### 1. 数据库配置

在运行项目前，确保：
1. 已创建数据库 `learning_platform`
2. 已执行数据库初始化SQL（创建表结构）
3. 在 `DBHelper.java` 中配置正确的数据库连接信息

### 2. 部署步骤

1. **导入项目到IDE**（如IntelliJ IDEA或Eclipse）

2. **配置Tomcat服务器**
   - 添加Tomcat 9.0或更高版本
   - 部署war包或直接运行项目

3. **启动服务器**
   - 访问 `http://localhost:8080/项目名/admin/courses` 测试课程管理
   - 访问 `http://localhost:8080/项目名/admin/teachers` 测试教师管理

### 3. 测试流程

#### 测试课程管理：
1. 访问课程列表页
2. 点击"添加新课程"
3. 填写课程信息（名称、学院、描述、可见性）
4. 保存后在列表中查看
5. 测试编辑和删除功能

#### 测试教师管理：
1. 先添加几门课程（用于分配给教师）
2. 访问教师列表页
3. 点击"添加新教师"
4. 填写教师信息并选择授课课程
5. 保存后查看教师列表
6. 测试编辑、删除和课程分配功能

## API接口说明

### CourseController
- `GET /admin/courses` - 显示课程列表
- `GET /admin/courses?action=add` - 显示添加页面
- `GET /admin/courses?action=edit&id=课程ID` - 显示编辑页面
- `POST /admin/courses?action=add` - 添加课程
- `POST /admin/courses?action=update` - 更新课程
- `POST /admin/courses?action=delete&id=课程ID` - 删除课程

### TeacherController
- `GET /admin/teachers` - 显示教师列表
- `GET /admin/teachers?action=add` - 显示添加页面
- `GET /admin/teachers?action=edit&id=教师ID` - 显示编辑页面
- `POST /admin/teachers?action=add` - 添加教师
- `POST /admin/teachers?action=update` - 更新教师
- `POST /admin/teachers?action=delete` - 删除教师

## 主要类和方法

### CourseService
```java
List<CourseModel> getAllCourses()                    // 获取所有课程
CourseModel getCourseById(int id)                    // 根据ID获取课程
boolean addCourse(CourseModel course)                // 添加课程
boolean updateCourse(CourseModel course)             // 更新课程
boolean deleteCourse(int id)                         // 删除课程
List<CourseModel> getCoursesByTeacherId(int id)      // 获取教师的课程
```

### TeacherService
```java
List<TeacherModel> getAllTeachers()                  // 获取所有教师
TeacherModel getTeacherById(int id)                  // 根据ID获取教师
boolean addTeacher(TeacherModel teacher)             // 添加教师
boolean updateTeacher(TeacherModel teacher)          // 更新教师
boolean deleteTeacher(int id)                        // 删除教师
boolean assignCourses(int teacherId, List<Integer>)  // 分配课程
List<Integer> getCourseIdsByTeacherId(int id)        // 获取教师课程ID列表
```

## 注意事项

1. **权限控制**：
   - 所有管理功能需要管理员权限
   - Servlet中已包含Session检查，确保用户已登录

2. **数据验证**：
   - 前端和后端都进行了必填项验证
   - 用户名唯一性由数据库约束保证

3. **密码安全**：
   - ⚠️ 当前版本密码明文存储，生产环境需要加密（建议使用BCrypt）

4. **级联删除**：
   - 删除课程会自动删除相关的teacher_courses关联
   - 删除教师会自动删除相关的teacher_courses关联

5. **错误处理**：
   - 所有数据库操作都包含异常处理
   - 错误信息会在页面上友好显示

## 扩展建议

1. **密码加密**：使用BCrypt或SHA-256加密密码
2. **分页功能**：当数据量大时添加分页
3. **搜索功能**：添加课程名称、教师姓名搜索
4. **批量操作**：支持批量删除、批量分配课程
5. **文件上传**：支持头像图片上传
6. **权限细化**：区分不同级别的管理员权限
7. **操作日志**：记录管理员的操作历史
8. **数据导入导出**：支持Excel批量导入教师和课程信息

## 后续开发

根据设计文档，后续还需要实现：
- 学习资源管理模块
- 学生问答模块
- 教师回答问题模块
- 通知系统
- 用户个人中心

## 技术栈

- **后端**：Java Servlet 4.0, JDBC
- **前端**：JSP, HTML5, CSS3, JavaScript
- **数据库**：MySQL 8.0
- **服务器**：Apache Tomcat 9.0+
- **构建工具**：Maven

## 联系支持

如有问题，请查看：
- 数据库设计文档：`dbDisign.md`
- 功能设计文档：`funcDisign.md`

---

**开发完成日期**：2025年12月9日
**版本**：v1.0
