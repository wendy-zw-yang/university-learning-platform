<!-- src/main/webapp/profile.jsp -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Profile</title>
</head>
<body>
<h2>Profile</h2>
<% if (request.getAttribute("success") != null) { %>
<p style="color: green;"><%= request.getAttribute("success") %></p>
<% } %>
<form action="profile" method="post" enctype="multipart/form-data">
    <label>Email: <input type="email" name="email" value="${user.email}" required></label><br>
    <label>New Password: <input type="password" name="newPassword"></label><br>
    <label>Avatar: <input type="file" name="avatar"></label><br>
    <input type="submit" value="Update">
</form>
</body>
</html>