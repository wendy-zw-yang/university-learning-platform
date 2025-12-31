<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%
    // 验证用户是否登录且为管理员
    UserModel user = (UserModel) session.getAttribute("user");
    if (user == null || !"admin".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>管理员主页 - 大学学习平台</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
    
        body {
            background-color: #f5f5f5;
        }
    
        .navbar {
            background: #38990b;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }
    
        .navbar h1 {
            font-size: 24px;
        }
    
        .navbar .user-info {
            display: flex;
            align-items: center;
            gap: 20px;
        }
    
        .navbar .user-info span {
            background: rgba(255, 255, 255, 0.2);
            padding: 8px 15px;
            border-radius: 20px;
        }
    
        .navbar a {
            color: white;
            text-decoration: none;
            padding: 8px 15px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 5px;
        }
    
        .navbar a:hover {
            background: rgba(255, 255, 255, 0.3);
        }
    
        .container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }
    
        .welcome-card {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
    
        .welcome-card h2 {
            color: #4d4d4d;
            margin-bottom: 10px;
        }
    
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }
    
        .dashboard-card {
            background: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            text-align: center;
            cursor: pointer;
        }
    
        .dashboard-card:hover {
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
        }
    
        .dashboard-card h3 {
            color: #333;
            margin-bottom: 10px;
        }
    
        .dashboard-card p {
            color: #666;
            font-size: 14px;
        }
    
        .icon {
            font-size: 48px;
            margin-bottom: 15px;
        }
    
        .badge {
            display: inline-block;
            padding: 5px 12px;
            background: #f5576c;
            color: white;
            border-radius: 15px;
            font-size: 12px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<%@ include file="navbar.jsp" %>
    
    <div class="container">
        <div class="welcome-card">
            <h2>欢迎回来，<%= user.getUsername() %>！</h2>
        </div>
        
        <div class="dashboard-grid">
            <div class="dashboard-card" onclick="goToTeachers()">
                <div class="icon">👥</div>
                <h3>教师管理</h3>
            </div>
            
            <div class="dashboard-card" onclick="goToCourses()">
                <div class="icon">📚</div>
                <h3>课程管理</h3>
            </div>
            
            <div class="dashboard-card" onclick="goToResource()">
                <div class="icon">📝</div>
                <h3>学习资源管理</h3>
            </div>
            
            <div class="dashboard-card" onclick="goToQuestions()">
                <div class="icon">📊</div>
                <h3>问答内容管理</h3>
            </div>
        </div>
    </div>
</body>
<script>
    function goToResource() {
        window.location.href = "<%= request.getContextPath() %>/admin/resource";
    }
</script>
<script>
    function goToTeachers() {
        window.location.href='${pageContext.request.contextPath}/admin/teachers';
    }
</script>
<script>
    function goToCourses() {
        window.location.href='${pageContext.request.contextPath}/admin/courses';
    }
</script>
<script>
    function goToQuestions() {
        window.location.href='${pageContext.request.contextPath}/admin/questions';
    }
</script>
</html>