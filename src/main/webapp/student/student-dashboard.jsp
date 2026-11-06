<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%
    // Kunin ang naka-login na user mula sa session
    // (Siguraduhin nang dumaan sa AuthFilter bago makarating dito)
    User user = (User) session.getAttribute("loggedUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - Clearance System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, Helvetica, sans-serif; background: #f4f6f9; }
        .topbar {
            background: #1e3c72; color: #fff;
            padding: 14px 25px; display: flex;
            justify-content: space-between; align-items: center;
        }
        .topbar h2 { font-size: 17px; }
        .topbar a { color: #ffd; text-decoration: none; font-size: 13px; }
        .content { max-width: 900px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #1e3c72; margin-bottom: 10px; }
        .status-badge {
            display: inline-block; padding: 5px 12px;
            border-radius: 20px; font-size: 13px; color: #fff;
        }
        .bg-orange { background: #e67e22; }
        .info-row { font-size: 14px; color: #555; margin-bottom: 6px; }
        .logout-btn { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>STUDENT PORTAL</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="logout-btn" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="card">
            <h3>Welcome, <%= user.getFullName() %></h3>
            <div class="info-row">
                Student No: <%= user.getStudentNumber() != null ? user.getStudentNumber() : "-" %>
            </div>
            <div class="info-row">
                Program: <%= user.getProgramCode() != null ? user.getProgramCode() : "-" %> |
                Year Level: <%= user.getYearLevel() != null ? user.getYearLevel() : "-" %>
            </div>
            <div class="info-row">
                Academic Year 2026-2027 &middot; First Semester
            </div>
        </div>

        <div class="card">
            <h3>Overall Status</h3>
            <span class="status-badge bg-orange">In Progress</span>
            <div class="info-row" style="margin-top:10px;">
                Academic Year 2026-2027 &middot; First Semester
            </div>
        </div>

        <div class="card">
            <h3>Clearance</h3>
            <p style="font-size:14px; color:#555;">View your clearance requirements and respond to office requests.</p>
            <p style="margin-top:12px;">
                <a href="${pageContext.request.contextPath}/student/clearance"
                   style="background:#1e3c72; color:#fff; padding:9px 18px; border-radius:5px;
                          text-decoration:none; font-size:14px; display:inline-block; margin-right:8px;">
                    My Clearance
                </a>
                <a href="${pageContext.request.contextPath}/student/notifications"
                   style="background:#1e3c72; color:#fff; padding:9px 18px; border-radius:5px;
                          text-decoration:none; font-size:14px; display:inline-block;">
                    Notifications
                </a>
            </p>
        </div>

        <div class="card">
            <h3>Clearance Requirements</h3>
            <p style="font-size:14px; color:#555;">Your required offices and their current status will appear here.</p>
        </div>
    </div>
</body>
</html>
