<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na admin mula sa session
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga audit logs na pinadala ng AuditServlet
    List<String[]> logs = (List<String[]>) request.getAttribute("logs");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Audit Logs - Clearance System</title>
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
        .topbar .back { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
        .content { max-width: 1100px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #1e293b; margin-bottom: 15px; }
        .empty-msg { text-align: center; color: #888; font-size: 14px; padding: 30px 0; }

        /* ===== Table para sa listahan ng logs ===== */
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #f1f5f9; text-align: left; padding: 9px 10px; }
        td { padding: 9px 10px; border-bottom: 1px solid #e2e8f0; }
        .badge { padding: 3px 9px; border-radius: 12px; font-size: 11px; color: #fff; }
        .bg-blue { background: #2980b9; }
        .bg-green { background: #27ae60; }
        .bg-red { background: #c0392b; }
        .bg-gray { background: #7f8c8d; }
        .bg-purple { background: #8e44ad; }
        .details { max-width: 320px; word-break: break-word; }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>SYSTEM ADMIN</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="back" href="${pageContext.request.contextPath}/admin/admin-dashboard.jsp">Dashboard</a>
            <a class="back" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="card">
            <h3>Audit Logs (<%= logs != null ? logs.size() : 0 %>)</h3>

            <% if (logs == null || logs.isEmpty()) { %>
                <p class="empty-msg">Wala pang audit logs. Lalabas dito ang mga decision, approval, at user actions.</p>
            <% } else { %>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Actor</th>
                        <th>Action</th>
                        <th>Entity</th>
                        <th>Details</th>
                        <th>Date/Time</th>
                    </tr>
                    <% for (String[] log : logs) { %>
                    <tr>
                        <td><%= log[0] %></td>
                        <td>
                            <%= log[1] != null ? log[1] : "-" %>
                            <% if (log[2] != null) { %>
                                <br><span style="color:#888; font-size:11px;"><%= log[2] %></span>
                            <% } %>
                        </td>
                        <td><%= badgeForAction(log[3]) %></td>
                        <td>
                            <%= log[4] != null ? log[4] : "-" %>
                            <% if (log[5] != null) { %>
                                <span style="color:#888;">#<%= log[5] %></span>
                            <% } %>
                        </td>
                        <td class="details"><%= log[6] != null ? log[6] : "-" %></td>
                        <td><%= log[7] != null ? log[7] : "-" %></td>
                    </tr>
                    <% } %>
                </table>
            <% } %>
        </div>
    </div>

    <%!
        // Kulay ng badge depende sa action
        private String badgeForAction(String action) {
            if (action == null) {
                return "<span class=\"badge bg-gray\">-</span>";
            }
            switch (action) {
                case "FINAL_APPROVE":
                    return "<span class=\"badge bg-green\">FINAL_APPROVE</span>";
                case "RETURN_FOR_ACTION":
                    return "<span class=\"badge bg-red\">RETURN_FOR_ACTION</span>";
                case "DECISION":
                    return "<span class=\"badge bg-blue\">DECISION</span>";
                case "USER_CREATE":
                    return "<span class=\"badge bg-purple\">USER_CREATE</span>";
                case "USER_DISABLE":
                    return "<span class=\"badge bg-red\">USER_DISABLE</span>";
                case "USER_ACTIVATE":
                    return "<span class=\"badge bg-green\">USER_ACTIVATE</span>";
                default:
                    return "<span class=\"badge bg-gray\">" + action + "</span>";
            }
        }
    %>
</body>
</html>
