<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%
    UserModel currentUser = (UserModel) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String roleText = "";
    String roleBadge = "";

    if ("student".equals(currentUser.getRole())) {
        roleText = "项目管理学习平台 - 学生中心";
        roleBadge = "学生";
    } else if ("teacher".equals(currentUser.getRole())) {
        roleText = "项目管理学习平台 - 教师工作台";
        roleBadge = "教师";
    } else if ("admin".equals(currentUser.getRole())) {
        roleText = "项目管理学习平台 - 管理员控制台";
        roleBadge = "管理员";
    }
%>
<div class="navbar-wrapper">
    <div class="navbar">
        <h1>🎓 <%= roleText %></h1>
        <div class="user-info">
            <span><span class="badge"><%= roleBadge %></span> <%= currentUser.getUsername() %></span>
            <a href="<%= request.getContextPath() %>/profile">个人资料</a>
            <a href="<%= request.getContextPath() %>/logout">退出登录</a>
        </div>
    </div>
</div>
