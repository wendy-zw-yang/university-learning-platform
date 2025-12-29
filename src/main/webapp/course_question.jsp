<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.ulp.servlet.StudentQuestionServlet" %>
<%@ page import="com.ulp.servlet.TeacherQuestionServlet" %>
<%@ page import="com.ulp.bean.QuestionModel" %>
<%@ page import="com.ulp.bean.AnswerModel" %>
<%@ page import="com.ulp.bean.CourseModel" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.ulp.bean.UserModel" %>
<%
    // 获取当前登录用户
    UserModel userObj = (UserModel)session.getAttribute("user");
    if (userObj == null || (!"student".equals(userObj.getRole()) && !"teacher".equals(userObj.getRole()))) {
//        System.out.println("未登录!@ course Question jsp");
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String role = userObj.getRole();
    
    // 获取课程列表和问题列表
    List<?> courses = null;
    List<?> questions = null;
    Integer selectedCourseId = (Integer) request.getAttribute("selectedCourseId");

    // 根据用户角色获取对应的数据
    if ("student".equals(role)) {
        courses = (List<StudentQuestionServlet.CourseWithQuestionCount>) request.getAttribute("courses");
        questions = (List<StudentQuestionServlet.QuestionWithAnswers>) request.getAttribute("questions");
    } else if ("teacher".equals(role)) {
        courses = (List<TeacherQuestionServlet.CourseWithQuestionCount>) request.getAttribute("courses");
        questions = (List<TeacherQuestionServlet.QuestionWithAnswers>) request.getAttribute("questions");
    }

    // 获取错误信息
    String errorMessage = (String) request.getAttribute("error");
    String successMessage = (String) request.getAttribute("success");

    // 格式化日期的工具
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>问答讨论 - <%= "teacher".equals(role) ? "教师" : "学生" %></title>
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
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        h1 {
            color: #333;
            font-size: 28px;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
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
        
        .btn-answer {
            background-color: #28a745;
            color: white;
        }
        
        .btn-answer:hover {
            background-color: #218838;
        }

        .message {
            padding: 12px 20px;
            border-radius: 4px;
            margin-bottom: 20px;
        }

        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .course-list {
            margin-bottom: 30px;
        }

        .course-item {
            background: #f8f9fa;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 4px;
            border-left: 4px solid #007bff;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .course-item:hover {
            background: #e9ecef;
        }

        .course-item.active {
            background: #d1ecf1;
            border-left: 4px solid #17a2b8;
        }

        .course-link {
            text-decoration: none;
            color: #333;
            display: block;
            flex: 1;
        }

        .course-link:hover {
            color: #007bff;
        }

        .course-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .course-count {
            display: inline-block;
            background-color: #17a2b8;
            color: white;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 12px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #333;
        }

        tr:hover {
            background-color: #f8f9fa;
        }

        .no-data {
            text-align: center;
            padding: 40px;
            color: #666;
            font-size: 16px;
        }

        .question-content {
            margin: 10px 0;
            padding: 10px;
            background-color: #f8f9fa;
            border-radius: 4px;
        }

        .answer-content {
            margin: 10px 0 10px 20px;
            padding: 10px;
            background-color: #e9f4ff;
            border-radius: 4px;
            border-left: 3px solid #007bff;
        }

        .question-title {
            font-weight: bold;
            color: #333;
        }

        .attachment-link {
            display: inline-block;
            margin-top: 5px;
            color: #007bff;
            text-decoration: none;
        }

        .attachment-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>💬 问答讨论区</h1>
        <a href="${pageContext.request.contextPath}/<%= "teacher".equals(role) ? "teacher_homepage.jsp" : "student_homepage.jsp" %>" class="btn btn-secondary">返回首页</a>
    </div>

    <%-- 显示消息 --%>
    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="message error"><%= errorMessage %></div>
    <% } %>

    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="message success"><%= successMessage %></div>
    <% } %>

    <% if (questions == null || questions.isEmpty()) { %>
    <!-- 课程列表 -->
    <div class="course-list">
        <h2>课程列表</h2>
        <% if (courses != null && !courses.isEmpty()) { %>
        <% for (Object courseObj : courses) {
            Object courseWithCount = courseObj;
            CourseModel course = null;
            String teacherName = null;
            int questionCount = 0;
            
            // 根据用户角色获取相应类型的对象
            if ("student".equals(role)) {
                StudentQuestionServlet.CourseWithQuestionCount studentCourse = (StudentQuestionServlet.CourseWithQuestionCount) courseObj;
                course = studentCourse.getCourse();
                teacherName = studentCourse.getTeacherName();
                questionCount = studentCourse.getQuestionCount();
            } else if ("teacher".equals(role)) {
                TeacherQuestionServlet.CourseWithQuestionCount teacherCourse = (TeacherQuestionServlet.CourseWithQuestionCount) courseObj;
                course = teacherCourse.getCourse();
                teacherName = teacherCourse.getTeacherName();
                questionCount = teacherCourse.getQuestionCount();
            }
        %>
        <div class="course-item">
            <a href="${pageContext.request.contextPath}/<%= "teacher".equals(role) ? "teacher" : "student" %>/questions?courseId=<%= course.getId() %>" class="course-link">
                <div class="course-header">
                    <div>
                        <strong><%= course.getName() %></strong>
                        <span>ID: <%= course.getId() %></span>
                        <% if (teacherName != null) { %>
                        <span> | 教师: <%= teacherName %></span>
                        <% } %>
                    </div>
                    <div>
                        <span class="course-count"><%= questionCount %> 个问题</span>
                    </div>
                </div>
                <% if (course.getDescription() != null && !course.getDescription().isEmpty()) { %>
                <div><%= course.getDescription() %></div>
                <% } %>
            </a>
            <% if ("student".equals(role)) { %>
            <a href="${pageContext.request.contextPath}/questions?courseId=<%= course.getId() %>" class="btn btn-primary">提问</a>
            <% }%>
        </div>
        <% } %>
        <% } else { %>
        <div class="no-data">
            <p>您还没有加入任何课程</p>
        </div>
        <% } %>
    </div>
    <% } else { %>
    <!-- 问题列表 -->
    <h2>
        <a href="${pageContext.request.contextPath}/<%= "teacher".equals(role) ? "teacher" : "student" %>/questions" style="text-decoration: none; color: #007bff;">&larr; 返回课程列表</a>
        <br><br>
        课程: 
        <% if (courses != null) {
            for (Object courseObj : courses) {
                Object courseWithCount = courseObj;
                CourseModel course = null;
                if ("student".equals(role)) {
                    StudentQuestionServlet.CourseWithQuestionCount studentCourse = (StudentQuestionServlet.CourseWithQuestionCount) courseObj;
                    course = studentCourse.getCourse();
                } else if ("teacher".equals(role)) {
                    TeacherQuestionServlet.CourseWithQuestionCount teacherCourse = (TeacherQuestionServlet.CourseWithQuestionCount) courseObj;
                    course = teacherCourse.getCourse();
                }
                if (course.getId() == selectedCourseId) {
        %>
        <strong><%= course.getName() %></strong>
        <% 
                    break;
                }
            }
        } %>
    </h2>

    <% if (questions != null && !questions.isEmpty()) { %>
    <% for (Object questionObj : questions) {
        Object questionWithAnswers = questionObj;
        QuestionModel question = null;
        String studentName = null;
        List<AnswerModel> answers = null;
        
        // 根据用户角色获取相应类型的对象
        if ("student".equals(role)) {
            StudentQuestionServlet.QuestionWithAnswers studentQuestion = (StudentQuestionServlet.QuestionWithAnswers) questionObj;
            question = studentQuestion.getQuestion();
            studentName = studentQuestion.getStudentName();
            answers = studentQuestion.getAnswers();
        } else if ("teacher".equals(role)) {
            TeacherQuestionServlet.QuestionWithAnswers teacherQuestion = (TeacherQuestionServlet.QuestionWithAnswers) questionObj;
            question = teacherQuestion.getQuestion();
            studentName = teacherQuestion.getStudentName();
            answers = teacherQuestion.getAnswers();
        }
    %>
    <div class="question-content">
        <div class="question-title">
            <%= question.getTitle() %>
            <span style="font-weight: normal; color: #666; font-size: 14px; margin-left: 10px;">
                学生: <%= studentName %> | 
                时间: <%= question.getCreatedAt() != null ? dateFormat.format(question.getCreatedAt()) : "-" %>
            </span>
        </div>
        <div style="margin-top: 8px;">
            <%= question.getContent() %>
        </div>
        <% if (question.getAttachment() != null && !question.getAttachment().isEmpty()) { %>
        <div>
            <a href="<%= request.getContextPath() + question.getAttachment() %>" 
               class="attachment-link" target="_blank">附件: <%= question.getAttachment().substring(question.getAttachment().lastIndexOf('/') + 1) %></a>
        </div>
        <% } %>
        
        <!-- 显示回答 -->
        <div style="margin-top: 15px;">
            <strong>教师回答:</strong>
            <% if (answers != null && !answers.isEmpty()) { %>
                <% for (AnswerModel answer : answers) { %>
                <div class="answer-content">
                    <%= answer.getContent() %>
                    <div style="font-size: 12px; color: #666; margin-top: 5px;">
                        教师: 
                        <% if (answer.getTeacherName() != null) { %>
                        <%= answer.getTeacherName() %>
                        <% } else { %>
                        ID <%= answer.getTeacherId() %>
                        <% } %> | 
                        时间: <%= answer.getCreatedAt() != null ? dateFormat.format(answer.getCreatedAt()) : "-" %>
                    </div>
                    <% if (answer.getAttachment() != null && !answer.getAttachment().isEmpty()) { %>
                    <div>
                        <a href="<%= request.getContextPath() + answer.getAttachment() %>" 
                           class="attachment-link" target="_blank">附件: <%= answer.getAttachment().substring(answer.getAttachment().lastIndexOf('/') + 1) %></a>
                    </div>
                    <% } %>
                </div>
                <% } %>
            <% } else { %>
                <div style="color: #dc3545; font-style: italic;">待回答</div>
                <% if ("teacher".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/questions?action=answer&questionId=<%= question.getId() %>" class="btn btn-answer" style="margin-top: 10px; display: inline-block;">回答问题</a>
                <% } %>
            <% } %>
        </div>
    </div>
    <% } %>
    <% } else { %>
    <div class="no-data">
        <p>该课程暂无问题</p>
    </div>
    <% } %>
    <% } %>
</div>
</body>
</html>