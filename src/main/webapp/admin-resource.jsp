<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:if test="${empty sessionScope.user or sessionScope.user.role ne 'admin'}">
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>资源管理 - 管理员</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background: #f5f5f5;
        }

        .container {
            min-height: 1200px;
        }

        .header {
            background: #38990b;
            color: white;
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .header h1 {
            font-size: 24px;
            margin-bottom: 10px;
        }

        .content {
            padding: 30px;
        }

        .section {
            margin-bottom: 40px;
        }

        .section-title {
            font-size: 20px;
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #38990b;
        }

        .table-container {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        thead {
            background: #f8f9fa;
        }

        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }

        th {
            font-weight: 600;
            color: #495057;
            font-size: 14px;
        }

        td {
            color: #6c757d;
            font-size: 14px;
        }

        tbody tr:hover {
            background: #f8f9fa;
        }

        .btn {
            padding: 6px 15px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 13px;
            text-decoration: none;
            display: inline-block;
            margin-right: 5px;
            transition: all 0.3s;
        }

        .btn-edit {
            background: #28a745;
            color: white;
        }

        .btn-edit:hover {
            background: #218838;
        }

        .btn-delete {
            background: #dc3545;
            color: white;
        }

        .btn-delete:hover {
            background: #c82333;
        }

        .btn-back {
            background: #6c757d;
            color: white;
            margin-bottom: 20px;
        }

        .btn-back:hover {
            background: #5a6268;
        }

        .description {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .edit-form {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 8px;
            margin-bottom: 30px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
            font-size: 14px;
        }

        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            font-size: 14px;
            font-family: inherit;
        }

        .form-group textarea {
            min-height: 100px;
            resize: vertical;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #38990b;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        .btn-submit {
            background: #38990b;
            color: white;
            padding: 10px 30px;
            font-size: 15px;
        }

        .btn-submit:hover {
            background: #38990b;
        }

        .btn-cancel {
            background: #6c757d;
            color: white;
            padding: 10px 30px;
            font-size: 15px;
        }

        .btn-cancel:hover {
            background: #5a6268;
        }

        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }

        .empty-message {
            text-align: center;
            padding: 40px;
            color: #6c757d;
            font-size: 16px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📚 学习资源管理</h1>
        </div>

        <div class="content">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-back">← 返回主页</a>

            <c:if test="${not empty requestScope.error}">
                <div class="error">${requestScope.error}</div>
            </c:if>

            <!-- 编辑表单 -->
            <c:if test="${not empty requestScope.editResource}">
                <div class="section">
                    <h2 class="section-title">✏️ 修改资源信息</h2>
                    <div class="edit-form">
                        <form action="${pageContext.request.contextPath}/admin/resource" method="post">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="${requestScope.editResource.id}">
                            
                            <div class="form-group">
                                <label for="title">资源标题：</label>
                                <input type="text" id="title" name="title" 
                                       value="${requestScope.editResource.title}" 
                                       required maxlength="100">
                            </div>
                            
                            <div class="form-group">
                                <label for="description">资源描述：</label>
                                <textarea id="description" name="description">${requestScope.editResource.description}</textarea>
                            </div>
                            
                            <div class="form-group">
                                <button type="submit" class="btn btn-submit">保存修改</button>
                                <a href="${pageContext.request.contextPath}/admin/resource" class="btn btn-cancel">取消</a>
                            </div>
                        </form>
                    </div>
                </div>
            </c:if>

            <!-- 资源列表 -->
            <div class="section">
                <h2 class="section-title">📋 所有资源列表</h2>
                
                <c:choose>
                    <c:when test="${not empty requestScope.resources}">
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>标题</th>
                                        <th>描述</th>
                                        <th>课程ID</th>
                                        <th>上传者ID</th>
                                        <th>下载次数</th>
                                        <th>创建时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="resource" items="${requestScope.resources}">
                                        <tr>
                                            <td>${resource.id}</td>
                                            <td>${resource.title}</td>
                                            <td class="description" title="${resource.description}">
                                                <c:choose>
                                                    <c:when test="${not empty resource.description}">
                                                        ${resource.description}
                                                    </c:when>
                                                    <c:otherwise>
                                                        无描述
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${resource.courseId}</td>
                                            <td>${resource.uploaderId}</td>
                                            <td>${resource.downloadCount}</td>
                                            <td>${resource.createdAt}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/admin/resource?action=edit&id=${resource.id}" 
                                                   class="btn btn-edit">编辑</a>
                                                <a href="javascript:void(0);" 
                                                   onclick="confirmDelete(${resource.id})" 
                                                   class="btn btn-delete">删除</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-message">
                            📭 暂无资源数据
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script>
        function confirmDelete(id) {
            if (confirm('确定要删除这个资源吗？此操作不可恢复！')) {
                window.location.href = '${pageContext.request.contextPath}/admin/resource?action=delete&id=' + id;
            }
        }
    </script>
</body>
</html>
