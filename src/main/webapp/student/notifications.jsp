<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na estudyante mula sa session
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga notifications na pinadala ng NotificationServlet
    List<String[]> notifications = (List<String[]>) request.getAttribute("notifications");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications - Clearance System</title>
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
        .topbar .back { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
        .content { max-width: 900px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #1e3c72; margin-bottom: 15px; }
        .logout-btn { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }

        /* ===== Listahan ng notifications ===== */
        .notif-item { padding: 12px 0; border-bottom: 1px solid #eee; font-size: 13px; }
        .notif-item:last-child { border-bottom: none; }
        .notif-title { font-weight: bold; color: #1e3c72; }
        .notif-title.unread { color: #1e3c72; }
        .notif-date { color: #999; font-size: 11px; }
        .notif-msg { color: #555; margin-top: 3px; }
        .btn {
            display: inline-block; padding: 8px 16px;
            background: #1e3c72; color: #fff;
            border: none; border-radius: 5px;
            font-size: 13px; cursor: pointer; text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>STUDENT PORTAL</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="back" href="${pageContext.request.contextPath}/student/student-dashboard.jsp">Dashboard</a>
            <a class="back" href="${pageContext.request.contextPath}/student/clearance">My Clearance</a>
            <a class="back" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="card">
            <h3>Notifications</h3>

            <!-- ===== MARK ALL READ BUTTON ===== -->
            <form action="${pageContext.request.contextPath}/student/notifications" method="POST" style="margin-bottom:12px;">
                <button class="btn" type="submit">Mark All Read</button>
            </form>

            <% if (notifications == null || notifications.isEmpty()) { %>
                <p style="font-size:14px; color:#555;">Wala pang notifications.</p>
            <% } else { %>
                <% for (String[] n : notifications) { %>
                    <div class="notif-item">
                        <span class="notif-title <%= "0".equals(n[2]) ? "unread" : "" %>">
                            <%= n[0] %>
                        </span>
                        <span class="notif-date"> &middot; <%= n[3] %></span>
                        <div class="notif-msg"><%= n[1] %></div>
                    </div>
                <% } %>
            <% } %>
        </div>
    </div>
</body>
</html>
