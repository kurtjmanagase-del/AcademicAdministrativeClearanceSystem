<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%
    User user = (User) session.getAttribute("loggedUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar Dashboard - Clearance System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, Helvetica, sans-serif; background: #f4f6f9; }
        .topbar {
            background: #7c2d12; color: #fff;
            padding: 14px 25px; display: flex;
            justify-content: space-between; align-items: center;
        }
        .topbar h2 { font-size: 17px; }
        .topbar a { color: #fed7aa; text-decoration: none; font-size: 13px; }
        .content { max-width: 900px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #7c2d12; margin-bottom: 10px; }
        .logout-btn { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>REGISTRAR PORTAL</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="logout-btn" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="card">
            <h3>Final Verification Queue</h3>
            <p style="font-size:14px; color:#555;">Clearances ready for final verification will appear here.</p>
            <a class="logout-btn" href="${pageContext.request.contextPath}/registrar/verification" style="display:inline-block; margin-top:12px; text-decoration:none; color:#7c2d12; background:#ffedd5; font-weight:bold;">Final Verification</a>
        </div>

        <div class="card">
            <h3>Clearance Cycles</h3>
            <p style="font-size:14px; color:#555;">Manage clearance periods per academic term.</p>
        </div>
    </div>
</body>
</html>
