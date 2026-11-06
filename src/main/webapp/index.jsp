<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Clearance Management System</title>
    <style>
        /* ===== Simpleng styling lang muna (pwede palitan ng Bootstrap sa susunod) ===== */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(135deg, #1e3c72, #2a5298);
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-box {
            background: #fff;
            padding: 40px;
            border-radius: 10px;
            width: 400px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            text-align: center;
        }
        .login-box h1 {
            font-size: 20px;
            color: #1e3c72;
            margin-bottom: 6px;
        }
        .login-box .subtitle {
            font-size: 12px;
            color: #888;
            margin-bottom: 25px;
        }
        .login-box label {
            display: block;
            text-align: left;
            font-size: 13px;
            color: #555;
            margin: 12px 0 5px;
        }
        .login-box input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 14px;
        }
        .login-box button {
            width: 100%;
            padding: 11px;
            margin-top: 20px;
            background: #1e3c72;
            color: #fff;
            border: none;
            border-radius: 5px;
            font-size: 15px;
            cursor: pointer;
        }
        .login-box button:hover { background: #16305c; }
        .error-msg {
            background: #fdecea;
            color: #c0392b;
            padding: 10px;
            border-radius: 5px;
            font-size: 13px;
            margin-top: 15px;
        }
    </style>
</head>
<body>
    <div class="login-box">
    <h1>ACADEMIC &amp; ADMINISTRATIVE<br>CLEARANCE MANAGEMENT SYSTEM</h1>
    <p class="subtitle">School Clearance Portal - Official Access Only</p>

    <!-- Ang form na ito ay nagpo-post sa /login (LoginServlet) -->
    <form action="${pageContext.request.contextPath}/login" method="POST">
        <label for="username">School ID / Username</label>
        <input type="text" id="username" name="username" placeholder="School ID or username" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Password" required>

        <button type="submit">SIGN IN</button>
    </form>

    <!-- Dito pinapakita ang error message kapag mali ang login (galing sa LoginServlet) -->
    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
        <div class="error-msg"><%= error %></div>
    <%
        }
    %>
    </div>
</body>
</html>
