<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%@ page import="com.ulp.util.DBHelper" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%
    // 验证用户是否登录且为教师
    UserModel user = (UserModel) session.getAttribute("user");
    if (user == null || !"teacher".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // 查询未回答问题的数量
    int unansweredQuestionsCount = 0;
    String sql = "SELECT COUNT(*) FROM questions q " +
                 "LEFT JOIN courses c ON q.course_id = c.id " +
                 "LEFT JOIN teacher_courses tc ON c.id = tc.course_id " +
                 "WHERE (c.teacher_id = ? OR tc.teacher_id = ?) " +
                 "AND q.id NOT IN (SELECT DISTINCT question_id FROM answers)";
    
    try (Connection conn = DBHelper.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, user.getId());
        pstmt.setInt(2, user.getId());
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            unansweredQuestionsCount = rs.getInt(1);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>教师主页 - 大学学习平台</title>
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
    
        .welcome-card, .message-box {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
    
        .welcome-card h2, .message-box h2 {
            color: #4d4d4d;
            margin-bottom: 10px;
        }
        
        .message-box h2 .content {
            display: inline-block;
            background: #f8d7da;
            color: #721c24;
            padding: 5px 10px;
            border-radius: 5px;
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
            <h2>欢迎回来，<%= user.getUsername() %> 老师！</h2>
        </div>
        <!-- 以下完善新问题通知功能 （实现绑定以及跳转功能）-->
        <div class="message-box">
            <% if (unansweredQuestionsCount > 0) { %>
            <h2><a href="${pageContext.request.contextPath}/teacher/questions" style="text-decoration: none; color: #721c24;">
                您所上课程有 <span class="content"><%= unansweredQuestionsCount %></span> 条未回答问题
            </a></h2>
            <% } else { %>
            <h2>您所上课程有 <span class="content"><%= unansweredQuestionsCount %></span> 条未回答问题</h2>
            <% } %>
        </div>
        <div class="dashboard-grid">
            <div class="dashboard-card" onclick="goToUploadResource()">
                <div class="icon">📚</div>
                <h3>发布学习资源</h3>
            </div>
            
            <div class="dashboard-card" onclick="goToAnswerQuestions()">
                <div class="icon">📝</div>
                <h3>回答学生问题</h3>
            </div>
            
            <div class="dashboard-card" onclick="goToCourses()">
                <div class="icon">✅</div>
                <h3>学科权限设置</h3>
            </div>
        </div>
    </div>
</body>
<script>
    function goToCourses() {
        window.location.href='${pageContext.request.contextPath}/teacher/courses';
    }
    function goToUploadResource() {
        window.location.href='${pageContext.request.contextPath}/teacher/resource';
    }
    function goToAnswerQuestions() {
        window.location.href='${pageContext.request.contextPath}/teacher/questions';
    }
</script>
</html>