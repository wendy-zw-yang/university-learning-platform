<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户资料管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
        /* 页面特定样式 */
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
        input[type="email"] {
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
        input[type="email"]:focus {
            outline: none;
            border-color: #007bff;
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

        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
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

        .profile-info {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #007bff;
        }

        .profile-info h3 {
            margin-top: 0;
            color: #495057;
        }

        .profile-info p {
            margin: 8px 0;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>

<div class="container">
    <h1>用户资料管理</h1>

    <!-- 显示成功或错误消息 -->
    ${not empty success ? '<div class="message success">' + success + '</div>' : ''}
    ${not empty error ? '<div class="message error">' + error + '</div>' : ''}

    <!-- 显示当前用户信息 -->
    <div class="profile-info">
        <h3>当前账户信息</h3>
        <p><strong>用户名:</strong> ${user.username}</p>
        <p><strong>邮箱:</strong> ${user.email}</p>
        <p><strong>角色:</strong>
            ${user.role eq 'admin' ? '管理员' :
                    user.role eq 'teacher' ? '教师' :
                            user.role eq 'student' ? '学生' :
                                    user.role}
        </p>
    </div>

    <!-- 修改资料表单 -->
    <form action="profile" method="post">
        <div class="form-group">
            <label for="email">邮箱地址 <span class="required">*</span></label>
            <input type="email" id="email" name="email" value="${user.email}" required>
        </div>

        <div class="form-group">
            <label for="newPassword">新密码 (留空则不修改)</label>
            <input type="password" id="newPassword" name="newPassword" placeholder="不填写则不修改密码">
        </div>

        <div class="form-group">
            <label for="confirmPassword">确认新密码</label>
            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="再次输入新密码">
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">更新资料</button>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">取消</a>
        </div>
    </form>
</div>
</body>
</html>
