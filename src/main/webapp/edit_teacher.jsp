<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.TeacherModel" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("teacher") != null ? "编辑教师" : "添加教师" %> - 大学生学习平台</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Microsoft YaHei', Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }
        
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        h1 {
            color: #333;
            margin-bottom: 30px;
            font-size: 28px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }
        
        .required {
            color: #dc3545;
        }
        
        input[type="text"],
        input[type="password"],
        input[type="email"],
        textarea {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.3s;
        }
        
        input[type="text"]:focus,
        input[type="password"]:focus,
        input[type="email"]:focus,
        textarea:focus {
            outline: none;
            border-color: #007bff;
        }
        
        textarea {
            resize: vertical;
            min-height: 80px;
        }
        
        .message {
            padding: 12px 20px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .form-actions {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        
        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
        }
        
        .btn-primary {
            background-color: #007bff;
            color: white;
        }
        
        .btn-primary:hover {
            background-color: #0056b3;
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        
        .help-text {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
        
        .course-selection {
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 15px;
            max-height: 300px;
            overflow-y: auto;
            background-color: #fafafa;
        }
        
        .course-item {
            padding: 8px 12px;
            margin: 5px 0;
            background-color: white;
            border-radius: 4px;
            display: flex;
            align-items: center;
        }
        
        .course-item input[type="checkbox"] {
            margin-right: 10px;
            width: 18px;
            height: 18px;
            cursor: pointer;
        }
        
        .course-item label {
            margin: 0;
            cursor: pointer;
            flex: 1;
            font-weight: normal;
        }
        
        .no-courses {
            text-align: center;
            padding: 20px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <%
            TeacherModel teacher = (TeacherModel) request.getAttribute("teacher");
            List<CourseModel> courses = (List<CourseModel>) request.getAttribute("courses");
            boolean isEdit = teacher != null;
        %>
        
        <h1><%= isEdit ? "编辑教师信息" : "添加新教师" %></h1>
        
        <%-- 显示错误消息 --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <form method="post" action="${pageContext.request.contextPath}/admin/teachers">
            <% if (isEdit) { %>
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="<%= teacher.getUserModel().getId() %>">
            <% } else { %>
                <input type="hidden" name="action" value="add">
            <% } %>
            
            <div class="form-group">
                <label for="username">用户名 <span class="required">*</span></label>
                <input type="text" 
                       id="username" 
                       name="username" 
                       value="<%= isEdit ? teacher.getUserModel().getUsername() : "" %>"
                       required 
                       placeholder="请输入用户名">
                <div class="help-text">教师登录系统使用的用户名</div>
            </div>
            
            <% if (!isEdit) { %>
                <div class="form-group">
                    <label for="password">密码 <span class="required">*</span></label>
                    <input type="password" 
                           id="password" 
                           name="password" 
                           required 
                           placeholder="请输入密码">
                    <div class="help-text">初始登录密码，建议首次登录后修改</div>
                </div>
            <% } %>
            
            <div class="form-group">
                <label for="email">邮箱</label>
                <input type="email" 
                       id="email" 
                       name="email" 
                       value="<%= isEdit && teacher.getUserModel().getEmail() != null ? teacher.getUserModel().getEmail() : "" %>"
                       placeholder="请输入邮箱地址">
            </div>
            
            <div class="form-group">
                <label for="title">职称</label>
                <input type="text" 
                       id="title" 
                       name="title" 
                       value="<%= isEdit && teacher.getUserModel().getTitle() != null ? teacher.getUserModel().getTitle() : "" %>"
                       placeholder="例如：教授、副教授、讲师等">
            </div>
            
            <div class="form-group">
                <label for="profile">个人简介</label>
                <textarea id="profile" 
                          name="profile" 
                          placeholder="请输入教师的个人简介、研究方向等"><%= isEdit && teacher.getUserModel().getProfile() != null ? teacher.getUserModel().getProfile() : "" %></textarea>
            </div>
            
            <div class="form-group">
                <label for="avatar">头像URL</label>
                <input type="text" 
                       id="avatar" 
                       name="avatar" 
                       value="<%= isEdit && teacher.getUserModel().getAvatar() != null ? teacher.getUserModel().getAvatar() : "" %>"
                       placeholder="请输入头像图片的URL地址">
                <div class="help-text">头像图片的网络地址或服务器路径</div>
            </div>
            
            <div class="form-group">
                <label>授课课程</label>
                <div class="course-selection">
                    <%
                        if (courses != null && !courses.isEmpty()) {
                            List<Integer> teacherCourseIds = isEdit ? teacher.getCourseIdList() : null;
                            for (CourseModel course : courses) {
                                boolean isChecked = teacherCourseIds != null && teacherCourseIds.contains(course.getId());
                    %>
                        <div class="course-item">
                            <input type="checkbox" 
                                   id="course_<%= course.getId() %>" 
                                   name="courseIds" 
                                   value="<%= course.getId() %>"
                                   <%= isChecked ? "checked" : "" %>>
                            <label for="course_<%= course.getId() %>">
                                <%= course.getName() %>
                                <% if (course.getCollege() != null && !course.getCollege().isEmpty()) { %>
                                    <span style="color: #999; font-size: 12px;">（<%= course.getCollege() %>）</span>
                                <% } %>
                            </label>
                        </div>
                    <%
                            }
                        } else {
                    %>
                        <div class="no-courses">暂无可分配的课程，请先在课程管理中添加课程</div>
                    <%
                        }
                    %>
                </div>
                <div class="help-text">选择该教师负责授课的课程（可多选）</div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <%= isEdit ? "保存修改" : "添加教师" %>
                </button>
                <a href="${pageContext.request.contextPath}/admin/teachers" class="btn btn-secondary">取消</a>
            </div>
        </form>
    </div>
</body>
</html>
