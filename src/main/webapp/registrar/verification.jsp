<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na registrar mula sa session
    // (Siguraduhin nang dumaan sa AuthFilter bago makarating dito)
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga data na pinadala ng RegistrarServlet
    List<String[]> queue = (List<String[]>) request.getAttribute("queue");
    List<String[]> completed = (List<String[]>) request.getAttribute("completed");
    List<String[]> items = (List<String[]>) request.getAttribute("items");
    String[] detailStudent = (String[]) request.getAttribute("detailStudent");
    Integer selectedRequestId = (Integer) request.getAttribute("selectedRequestId");

    if (queue == null) queue = new java.util.ArrayList<>();
    if (completed == null) completed = new java.util.ArrayList<>();
    if (selectedRequestId == null) selectedRequestId = 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Final Verification - Clearance System</title>
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
        .topbar .btn { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
        .content { max-width: 1000px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #7c2d12; margin-bottom: 15px; }

        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #f1f5f9; text-align: left; padding: 9px 10px; }
        td { padding: 9px 10px; border-bottom: 1px solid #e2e8f0; }

        .badge { display: inline-block; padding: 3px 9px; border-radius: 12px; font-size: 11px; color: #fff; }
        .bg-orange { background: #e67e22; }
        .bg-red { background: #c0392b; }
        .bg-green { background: #27ae60; }
        .bg-gray { background: #7f8c8d; }
        .bg-blue { background: #2980b9; }

        textarea {
            width: 100%; padding: 8px; border: 1px solid #ccc;
            border-radius: 5px; font-size: 14px;
        }
        .btn {
            display: inline-block; padding: 8px 14px; border: none;
            border-radius: 5px; font-size: 13px; cursor: pointer; text-decoration: none;
        }
        .btn-green { background: #27ae60; color: #fff; }
        .btn-red { background: #c0392b; color: #fff; }
        .btn-gray { background: #e2e8f0; color: #333; }
        .link { color: #2980b9; text-decoration: none; font-size: 12px; }
        .info-row { font-size: 14px; color: #555; margin-bottom: 6px; }
        .info-row b { color: #333; }
        .empty { color: #888; font-size: 14px; padding: 14px 0; text-align: center; }
        .hidden { display: none; }
        .reason-box { margin-top: 10px; }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>REGISTRAR PORTAL - FINAL VERIFICATION</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="btn" href="${pageContext.request.contextPath}/registrar/registrar-dashboard.jsp">Dashboard</a>
            <a class="btn" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">

        <!-- ===== VERIFICATION QUEUE ===== -->
        <div class="card">
            <h3>Final Verification Queue (<%= queue.size() %>)</h3>
            <% if (queue.isEmpty()) { %>
                <p class="empty">Walang clearance na naghihintay ng final verification.</p>
            <% } else { %>
            <table>
                <tr>
                    <th>Student No</th>
                    <th>Name</th>
                    <th>Program</th>
                    <th>Submitted</th>
                    <th>Action</th>
                </tr>
                <% for (String[] row : queue) { %>
                <tr>
                    <td><%= row[1] %></td>
                    <td><%= row[2] %></td>
                    <td><%= row[3] %></td>
                    <td><%= row[4] %></td>
                    <td>
                        <a class="link" href="${pageContext.request.contextPath}/registrar/verification?requestId=<%= row[0] %>">View</a>
                    </td>
                </tr>
                <% } %>
            </table>
            <% } %>
        </div>

        <% if (selectedRequestId > 0) { %>
        <!-- ===== DETAIL CARD ===== -->
        <div class="card">
            <h3>Verify Clearance - <%= detailStudent != null ? detailStudent[1] + " (" + detailStudent[2] + ")" : "Request #" + selectedRequestId %></h3>

            <% if (detailStudent != null) { %>
            <div class="info-row"><b>Student:</b> <%= detailStudent[2] %> (<%= detailStudent[1] %>)</div>
            <div class="info-row"><b>Program:</b> <%= detailStudent[3] %></div>
            <div class="info-row"><b>Submitted:</b> <%= detailStudent[4] %></div>
            <% } %>

            <h3 style="margin-top:16px;">Office Items</h3>
            <% if (items == null || items.isEmpty()) { %>
                <p class="empty">Walang items ang request na ito.</p>
            <% } else { %>
            <table>
                <tr>
                    <th>Office</th>
                    <th>Status</th>
                    <th>Remarks</th>
                </tr>
                <% for (String[] it : items) { %>
                <tr>
                    <td><%= it[1] %> - <%= it[2] %></td>
                    <td>
                        <% if ("PENDING".equals(it[3])) { %>
                            <span class="badge bg-gray">PENDING</span>
                        <% } else if ("IN_REVIEW".equals(it[3])) { %>
                            <span class="badge bg-blue">IN REVIEW</span>
                        <% } else if ("ACTION_REQUIRED".equals(it[3])) { %>
                            <span class="badge bg-red">ACTION REQUIRED</span>
                        <% } else if ("CLEARED".equals(it[3])) { %>
                            <span class="badge bg-green">CLEARED</span>
                        <% } else { %>
                            <span class="badge bg-gray"><%= it[3] %></span>
                        <% } %>
                    </td>
                    <td><%= it[4] != null ? it[4] : "-" %></td>
                </tr>
                <% } %>
            </table>
            <% } %>

            <h3 style="margin-top:18px;">Final Decision</h3>
            <form action="${pageContext.request.contextPath}/registrar/final" method="POST" style="margin-bottom:10px;">
                <input type="hidden" name="action" value="approve">
                <input type="hidden" name="requestId" value="<%= selectedRequestId %>">
                <button type="submit" class="btn btn-green">APPROVE FINAL CLEARANCE</button>
            </form>
            <form action="${pageContext.request.contextPath}/registrar/final" method="POST">
                <input type="hidden" name="action" value="return">
                <input type="hidden" name="requestId" value="<%= selectedRequestId %>">
                <div class="reason-box">
                    <label style="font-size:13px; color:#555;">Reason (kapag ibabalik sa estudyante):</label>
                    <textarea name="reason" rows="2" placeholder="I-type ang dahilan kung bakit ibabalik..."></textarea>
                </div>
                <button type="submit" class="btn btn-red" style="margin-top:8px;">RETURN FOR ACTION</button>
            </form>
        </div>
        <% } %>

        <!-- ===== COMPLETED CLEARANCES ===== -->
        <div class="card">
            <h3>Completed Clearances (<%= completed.size() %>)</h3>
            <% if (completed.isEmpty()) { %>
                <p class="empty">Wala pang kumpletong clearance.</p>
            <% } else { %>
            <table>
                <tr>
                    <th>Student No</th>
                    <th>Name</th>
                    <th>Program</th>
                    <th>Completed At</th>
                    <th>Status</th>
                </tr>
                <% for (String[] row : completed) { %>
                <tr>
                    <td><%= row[1] %></td>
                    <td><%= row[2] %></td>
                    <td><%= row[3] %></td>
                    <td><%= row[4] %></td>
                    <td><span class="badge bg-green">CLEARED</span></td>
                </tr>
                <% } %>
            </table>
            <% } %>
        </div>

    </div>
</body>
</html>
