<!-- src/main/webapp/dashboard.jsp -->
<!-- This is a placeholder for the dashboard after login. Customize as needed. -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>
<h2>Welcome, ${user.username}!</h2>
<p>Role: ${user.role}</p>
<a href="profile">Edit Profile</a><br>
<!-- Add links to other modules as needed -->
<a href="logout">Logout</a> <!-- Assume a LogoutServlet exists -->
</body>
</html>