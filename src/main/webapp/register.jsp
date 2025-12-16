<!-- src/main/webapp/register.jsp -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h2>Register (Student Only)</h2>
<% if (request.getAttribute("error") != null) { %>
<p style="color: red;"><%= request.getAttribute("error") %></p>
<% } %>
<form action="register" method="post">
    <label>Username: <input type="text" name="username" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <label>Email: <input type="email" name="email" required></label><br>
    <select name="role">
        <option value="student">Student</option>
        <option value="teacher">Teacher</option>
        <option value="admin">Admin</option>
    </select>

    <input type="text" name="avatar" placeholder="Avatar URL" />
    <textarea name="profile" placeholder="Profile"></textarea>
    <input type="text" name="title" placeholder="Title" />

    <button type="submit">Register</button>
</form>
</body>
</html>