<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na estudyante mula sa session
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga data na pinadala ng StudentServlet
    String[] cycle = (String[]) request.getAttribute("cycle");
    Integer requestId = (Integer) request.getAttribute("requestId");
    String[] studentRequest = (String[]) request.getAttribute("studentRequest");
    List<String[]> items = (List<String[]>) request.getAttribute("items");
    List<String[]> notifications = (List<String[]>) request.getAttribute("notifications");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Clearance - Clearance System</title>
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
        .content { max-width: 950px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #1e3c72; margin-bottom: 15px; }
        .info-row { font-size: 14px; color: #555; margin-bottom: 6px; }
        .logout-btn { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }

        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #f1f5f9; text-align: left; padding: 9px 10px; }
        td { padding: 9px 10px; border-bottom: 1px solid #e2e8f0; vertical-align: top; }
        .badge { padding: 3px 9px; border-radius: 12px; font-size: 11px; color: #fff; }
        .bg-orange { background: #e67e22; }
        .bg-red { background: #c0392b; }
        .bg-blue { background: #2980b9; }
        .bg-green { background: #27ae60; }
        .bg-gray { background: #7f8c8d; }
        .bg-dark { background: #34495e; }

        /* ===== Form ng sagot ng estudyante ===== */
        .respond-form textarea {
            width: 100%; padding: 8px; border: 1px solid #ccc;
            border-radius: 5px; font-size: 13px;
            resize: vertical; min-height: 55px;
        }
        .respond-form button {
            margin-top: 8px; padding: 8px 16px;
            background: #1e3c72; color: #fff;
            border: none; border-radius: 5px; font-size: 13px; cursor: pointer;
        }

        /* ===== Listahan ng notifications ===== */
        .notif-item { padding: 10px 0; border-bottom: 1px solid #eee; font-size: 13px; }
        .notif-item:last-child { border-bottom: none; }
        .notif-title { font-weight: bold; color: #1e3c72; }
        .notif-title.unread { font-weight: bold; }
        .notif-date { color: #999; font-size: 11px; }
        .notif-msg { color: #555; margin-top: 3px; }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>STUDENT PORTAL</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="back" href="${pageContext.request.contextPath}/student/student-dashboard.jsp">Dashboard</a>
            <a class="back" href="${pageContext.request.contextPath}/student/notifications">Notifications</a>
            <a class="back" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <% if (cycle == null) { %>
            <!-- ===== WALANG AKTIBONG CYCLE ===== -->
            <div class="card">
                <h3>No Active Clearance Cycle</h3>
                <p style="font-size:14px; color:#555;">
                    Walang OPEN na clearance cycle sa ngayon. Hintayin ang announcemement ng registrar
                    kung kailan magsisimula ang susunod na clearance.
                </p>
            </div>
        <% } else { %>
            <!-- ===== PROFILE / OVERALL STATUS CARD ===== -->
            <div class="card">
                <h3>Clearance Profile</h3>
                <div class="info-row">Student No: <%= studentRequest != null ? studentRequest[0] : "-" %></div>
                <div class="info-row">Name: <%= studentRequest != null ? studentRequest[1] : "-" %></div>
                <div class="info-row">Program: <%= studentRequest != null ? studentRequest[2] : "-" %></div>
                <div class="info-row">
                    Cycle: <%= cycle[1] %> &middot; <%= cycle[2] %> &middot; <%= cycle[3] %>
                </div>
                <div class="info-row" style="margin-top:10px;">
                    Overall Status:
                    <%
                        String overall = studentRequest != null ? studentRequest[3] : "";
                        String badgeClass = "bg-gray";
                        if ("IN_PROGRESS".equals(overall)) { badgeClass = "bg-orange"; }
                        else if ("ACTION_REQUIRED".equals(overall)) { badgeClass = "bg-red"; }
                        else if ("AWAITING_REGISTRAR".equals(overall)) { badgeClass = "bg-blue"; }
                        else if ("CLEARED".equals(overall)) { badgeClass = "bg-green"; }
                    %>
                    <span class="badge <%= badgeClass %>"><%= overall %></span>
                </div>
            </div>

            <!-- ===== CLEARANCE REQUIREMENTS TABLE ===== -->
            <div class="card">
                <h3>Clearance Requirements</h3>
                <% if (items == null || items.isEmpty()) { %>
                    <p style="font-size:14px; color:#555;">Wala pang requirements ang cycle na ito.</p>
                <% } else { %>
                    <table>
                        <tr>
                            <th>Office</th>
                            <th>Status</th>
                            <th>Remarks</th>
                            <th>Action</th>
                        </tr>
                        <% for (String[] item : items) { %>
                        <tr>
                            <td>
                                <strong><%= item[1] %></strong><br>
                                <span style="color:#777; font-size:12px;"><%= item[2] %></span>
                            </td>
                            <td>
                                <%
                                    String status = item[3];
                                    String itemBadge = "bg-gray";
                                    if ("IN_REVIEW".equals(status)) { itemBadge = "bg-blue"; }
                                    else if ("ACTION_REQUIRED".equals(status)) { itemBadge = "bg-red"; }
                                    else if ("CLEARED".equals(status)) { itemBadge = "bg-green"; }
                                    else if ("NOT_APPLICABLE".equals(status)) { itemBadge = "bg-dark"; }
                                %>
                                <span class="badge <%= itemBadge %>"><%= status %></span>
                            </td>
                            <td><%= item[4] != null ? item[4] : "-" %></td>
                            <td>
                                <% if ("ACTION_REQUIRED".equals(status)) { %>
                                    <!-- ===== RESPOND FORM: sasagutan ng estudyante ===== -->
                                    <form class="respond-form"
                                          action="${pageContext.request.contextPath}/student/respond"
                                          method="POST">
                                        <input type="hidden" name="itemId" value="<%= item[0] %>">
                                        <textarea name="response" required
                                                  placeholder="Ilagay ang iyong sagot / dokumento dito..."></textarea>
                                        <button type="submit">Submit Response</button>
                                    </form>
                                <% } else { %>
                                    <span style="color:#999; font-size:12px;">-</span>
                                <% } %>
                            </td>
                        </tr>
                        <% } %>
                    </table>
                <% } %>
            </div>
        <% } %>

        <!-- ===== NOTIFICATIONS CARD ===== -->
        <div class="card">
            <h3>Notifications</h3>
            <% if (notifications == null || notifications.isEmpty()) { %>
                <p style="font-size:14px; color:#555;">Wala pang notifications.</p>
            <% } else { %>
                <% for (String[] n : notifications) { %>
                    <div class="notif-item">
                        <span class="notif-title <%= "0".equals(n[2]) ? "unread" : "" %>"><%= n[0] %></span>
                        <span class="notif-date"> &middot; <%= n[3] %></span>
                        <div class="notif-msg"><%= n[1] %></div>
                    </div>
                <% } %>
            <% } %>
            <p style="margin-top:12px;">
                <a href="${pageContext.request.contextPath}/student/notifications"
                   style="background:#1e3c72; color:#fff; padding:8px 16px; border-radius:5px;
                          text-decoration:none; font-size:13px; display:inline-block;">
                    View All Notifications
                </a>
            </p>
        </div>
    </div>
</body>
</html>
