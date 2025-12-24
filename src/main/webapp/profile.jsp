<%--<!-- src/main/webapp/profile.jsp -->--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<html>--%>
<%--<head>--%>
<%--    <title>Profile</title>--%>
<%--</head>--%>
<%--<body>--%>
<%--<h2>Profile</h2>--%>
<%--<% if (request.getAttribute("success") != null) { %>--%>
<%--<p style="color: green;"><%= request.getAttribute("success") %></p>--%>
<%--<% } %>--%>
<%--<form action="profile" method="post" enctype="multipart/form-data">--%>
<%--    <label>Email: <input type="email" name="email" value="${user.email}" required></label><br>--%>
<%--    <label>New Password: <input type="password" name="newPassword"></label><br>--%>
<%--    <label>Avatar: <input type="file" name="avatar"></label><br>--%>
<%--    <input type="submit" value="Update">--%>
<%--</form>--%>
<%--</body>--%>
<%--</html>--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>用户资料管理</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"], input[type="email"], input[type="password"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .btn {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .success-message {
            color: green;
            margin-bottom: 15px;
        }
        .error-message {
            color: red;
            margin-bottom: 15px;
        }
        .profile-info {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<h2>用户资料管理</h2>

<!-- 显示成功或错误消息 -->
<c:if test="${not empty success}">
    <div class="success-message">${success}</div>
</c:if>

<c:if test="${not empty error}">
    <div class="error-message">${error}</div>
</c:if>

<!-- 显示当前用户信息 -->
<div class="profile-info">
    <h3>当前账户信息</h3>
    <p><strong>用户名:</strong> ${user.username}</p>
    <p><strong>邮箱:</strong> ${user.email}</p>
</div>

<!-- 修改资料表单 -->
<form action="profile" method="post">
    <div class="form-group">
        <label for="email">邮箱地址:</label>
        <input type="email" id="email" name="email" value="${user.email}" required>
    </div>

    <div class="form-group">
        <label for="newPassword">新密码 (留空则不修改):</label>
        <input type="password" id="newPassword" name="newPassword" placeholder="不填写则不修改密码">
    </div>

    <div class="form-group">
        <label for="confirmPassword">确认新密码:</label>
        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="再次输入新密码">
    </div>

<%--    <div class="form-group">--%>
<%--        <label for="avatar">头像上传:</label>--%>
<%--        <input type="file" id="avatar" name="avatar" accept="image/*">--%>
<%--        <c:if test="${not empty user.avatar}">--%>
<%--            <div style="margin-top: 10px;">--%>
<%--                <p>当前头像:</p>--%>
<%--                <img src="${user.avatar}" alt="当前头像" style="max-width: 100px; max-height: 100px;">--%>
<%--            </div>--%>
<%--        </c:if>--%>
<%--    </div>--%>

    <input type="submit" value="更新资料" class="btn">
</form>

<!-- 安全退出链接 -->
<div style="margin-top: 20px;">
    <a href="logout" style="color: #dc3545;">退出登录</a>
</div>
</body>
</html>