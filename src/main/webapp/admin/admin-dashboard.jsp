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
    <title>Admin Dashboard - Clearance System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, Helvetica, sans-serif; background: #f4f6f9; }
        .topbar {
            background: #1e293b; color: #fff;
            padding: 14px 25px; display: flex;
            justify-content: space-between; align-items: center;
        }
        .topbar h2 { font-size: 17px; }
        .topbar a { color: #cbd5e1; text-decoration: none; font-size: 13px; }
        .content { max-width: 900px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #1e293b; margin-bottom: 10px; }
        .logout-btn { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>SYSTEM ADMINISTRATION</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="logout-btn" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="card">
            <h3>User Management</h3>
            <p style="font-size:14px; color:#555;">Manage student, officer, and registrar accounts.</p>
            <p style="margin-top:12px;">
                <a href="${pageContext.request.contextPath}/admin/users"
                   style="background:#1e293b; color:#fff; padding:9px 18px; border-radius:5px;
                          text-decoration:none; font-size:14px; display:inline-block;">
                    Open User Management
                </a>
            </p>
        </div>

        <div class="card">
            <h3>Audit Logs</h3>
            <p style="font-size:14px; color:#555;">View the full trail of officer decisions, registrar approvals, and user actions.</p>
            <p style="margin-top:12px;">
                <a href="${pageContext.request.contextPath}/admin/audit"
                   style="background:#1e293b; color:#fff; padding:9px 18px; border-radius:5px;
                          text-decoration:none; font-size:14px; display:inline-block;">
                    Open Audit Logs
                </a>
            </p>
        </div>

        <div class="card">
            <h3>Offices &amp; Programs</h3>
            <p style="font-size:14px; color:#555;">Manage clearance offices and academic programs.</p>
            <p style="margin-top:12px;">
                <a href="${pageContext.request.contextPath}/admin/offices"
                   style="background:#1e293b; color:#fff; padding:9px 18px; border-radius:5px;
                          text-decoration:none; font-size:14px; display:inline-block;">
                    Open Offices &amp; Programs Management
                </a>
            </p>
        </div>

        <div class="card">
            <h3>Academic Terms &amp; Clearance Cycles</h3>
            <p style="font-size:14px; color:#555;">Manage academic terms and clearance cycles with required offices.</p>
            <p style="margin-top:12px;">
                <a href="${pageContext.request.contextPath}/admin/terms"
                   style="background:#1e293b; color:#fff; padding:9px 18px; border-radius:5px;
                          text-decoration:none; font-size:14px; display:inline-block; margin-right:8px;">
                    Open Academic Terms
                </a>
                <a href="${pageContext.request.contextPath}/admin/cycles"
                   style="background:#1e293b; color:#fff; padding:9px 18px; border-radius:5px;
                          text-decoration:none; font-size:14px; display:inline-block;">
                    Open Clearance Cycles
                </a>
            </p>
        </div>
    </div>
</body>
</html>
