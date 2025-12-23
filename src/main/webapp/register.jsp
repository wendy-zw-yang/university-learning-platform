<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户注册 - 大学学习平台</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: rgba(226, 226, 226);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
            width: 450px;
            max-width: 90%;
        }
        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 10px;
        }
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 25px;
            font-size: 14px;
        }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
            text-align: center;
        }
        .error {
            background-color: #fee;
            color: #c33;
            border: 1px solid #fcc;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: 500;
        }
        input[type="text"],
        input[type="password"],
        input[type="email"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        input[type="text"]:focus,
        input[type="password"]:focus,
        input[type="email"]:focus {
            outline: none;
            border-color: #38990b;
        }
        .password-hint {
            font-size: 12px;
            color: #888;
            margin-top: 3px;
        }
        .btn {
            width: 100%;
            padding: 12px;
            background: #38990b;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }
        .btn:hover {
            transform: translateY(-2px);
        }
        .footer-text {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .footer-text a {
            color: #38990b;
            text-decoration: none;
            font-weight: 600;
        }
        .footer-text a:hover {
            text-decoration: underline;
        }
    </style>
    <script>
        function validateForm() {
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            
            if (password.length < 6) {
                alert('密码长度至少为6位');
                return false;
            }
            
            if (password !== confirmPassword) {
                alert('两次输入的密码不一致');
                return false;
            }
            
            return true;
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>用户注册</h2>
        <p class="subtitle">注册为学生账户</p>
        
        <% if (request.getAttribute("error") != null) { %>
            <div class="message error"><%= request.getAttribute("error") %></div>
        <% } %>
        
        <form action="<%= request.getContextPath() %>/register" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="username">用户名</label>
                <input type="text" id="username" name="username" 
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                       required autofocus minlength="3" maxlength="20">
                <div class="password-hint">3-20个字符</div>
            </div>
            
            <div class="form-group">
                <label for="email">邮箱</label>
                <input type="email" id="email" name="email" 
                       value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                       required>
            </div>
            
            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" id="password" name="password" required minlength="6">
                <div class="password-hint">至少6位字符</div>
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">确认密码</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6">
            </div>
            
            <button type="submit" class="btn">注册</button>
        </form>
        
        <div class="footer-text">
            已有账号？ <a href="<%= request.getContextPath() %>/login">立即登录</a>
        </div>
    </div>
</body>
</html>