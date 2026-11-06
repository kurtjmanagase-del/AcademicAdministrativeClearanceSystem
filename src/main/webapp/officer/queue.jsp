<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    // Kunin ang naka-login na officer mula sa session
    // (Siguraduhin nang dumaan sa AuthFilter bago makarating dito)
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga data na pinadala ng OfficerServlet
    List<String[]> offices = (List<String[]>) request.getAttribute("offices");
    List<String[]> queue = (List<String[]>) request.getAttribute("queue");
    Map<String, Object> itemDetail = (Map<String, Object>) request.getAttribute("itemDetail");
    Integer selectedOfficeId = (Integer) request.getAttribute("selectedOfficeId");
    Integer selectedCycleId = (Integer) request.getAttribute("selectedCycleId");

    if (offices == null) offices = new java.util.ArrayList<>();
    if (selectedOfficeId == null) selectedOfficeId = 0;
    if (selectedCycleId == null) selectedCycleId = 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Queue - Clearance System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, Helvetica, sans-serif; background: #f4f6f9; }
        .topbar {
            background: #14532d; color: #fff;
            padding: 14px 25px; display: flex;
            justify-content: space-between; align-items: center;
        }
        .topbar h2 { font-size: 17px; }
        .topbar a { color: #d9f99d; text-decoration: none; font-size: 13px; }
        .topbar .btn { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
        .content { max-width: 1000px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #14532d; margin-bottom: 15px; }

        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #f1f5f9; text-align: left; padding: 9px 10px; }
        td { padding: 9px 10px; border-bottom: 1px solid #e2e8f0; }

        .badge { display: inline-block; padding: 3px 9px; border-radius: 12px; font-size: 11px; color: #fff; }
        .bg-orange { background: #e67e22; }
        .bg-blue { background: #2980b9; }
        .bg-red { background: #c0392b; }
        .bg-green { background: #27ae60; }
        .bg-gray { background: #7f8c8d; }

        select, textarea, input {
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
    </style>
</head>
<body>
    <div class="topbar">
        <h2>OFFICE STAFF PORTAL - REVIEW QUEUE</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="btn" href="${pageContext.request.contextPath}/officer/officer-dashboard.jsp">Dashboard</a>
            <a class="btn" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">

        <!-- ===== PUMILI NG OPISINA ===== -->
        <div class="card">
            <h3>Select Office</h3>
            <form action="${pageContext.request.contextPath}/officer/queue" method="GET">
                <select name="officeId" onchange="this.form.submit()" style="max-width:420px;">
                    <option value="">-- Pumili ng assigned office --</option>
                    <% for (String[] o : offices) {
                        boolean selected = selectedOfficeId == Integer.parseInt(o[0]);
                    %>
                        <option value="<%= o[0] %>" <%= selected ? "selected" : "" %>>
                            <%= o[1] %> - <%= o[2] %>
                        </option>
                    <% } %>
                </select>
                <noscript><button type="submit" class="btn btn-gray" style="margin-top:8px;">Load Queue</button></noscript>
            </form>
            <% if (offices.isEmpty()) { %>
                <p class="empty">Wala ka pang assigned office. Makipag-ugnayan sa admin para mag-assign ng opisina.</p>
            <% } %>
        </div>

        <% if (selectedOfficeId > 0) { %>
        <!-- ===== QUEUE TABLE ===== -->
        <div class="card">
            <h3>Review Queue
                <% for (String[] o : offices) {
                    if (selectedOfficeId == Integer.parseInt(o[0])) { %>
                        (<%= o[1] %> - <%= o[2] %>)
                <%  } } %>
            </h3>
            <% if (queue == null || queue.isEmpty()) { %>
                <p class="empty">Walang pending items sa opisina na ito. Lahat ay maayos!</p>
            <% } else { %>
            <table>
                <tr>
                    <th>Student No</th>
                    <th>Name</th>
                    <th>Program</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                <% for (String[] row : queue) {
                    String itemStatus = row[5];
                %>
                <tr>
                    <td><%= row[1] %></td>
                    <td><%= row[2] %></td>
                    <td><%= row[3] %></td>
                    <td>
                        <% if ("PENDING".equals(itemStatus)) { %>
                            <span class="badge bg-gray">PENDING</span>
                        <% } else if ("IN_REVIEW".equals(itemStatus)) { %>
                            <span class="badge bg-blue">IN REVIEW</span>
                        <% } else { %>
                            <span class="badge bg-red">ACTION REQUIRED</span>
                        <% } %>
                    </td>
                    <td>
                        <a class="link" href="${pageContext.request.contextPath}/officer/queue?officeId=<%= selectedOfficeId %>&itemId=<%= row[4] %>">View</a>
                    </td>
                </tr>
                <% } %>
            </table>
            <% } %>
        </div>
        <% } %>

        <% if (itemDetail != null) { %>
        <!-- ===== ITEM DETAIL ===== -->
        <div class="card">
            <h3>Item Review - <%= itemDetail.get("student_number") %> (<%= itemDetail.get("full_name") %>)</h3>

            <!-- Student info -->
            <div class="info-row"><b>Student:</b> <%= itemDetail.get("full_name") %> (<%= itemDetail.get("student_number") %>)</div>
            <div class="info-row"><b>Program:</b> <%= itemDetail.get("program_code") %> | <b>Year Level:</b> <%= itemDetail.get("year_level") %></div>
            <div class="info-row"><b>Clearance Cycle:</b> <%= itemDetail.get("cycle_name") %></div>
            <div class="info-row"><b>Office:</b> <%= itemDetail.get("office_code") %> - <%= itemDetail.get("office_name") %></div>
            <div class="info-row"><b>Item Status:</b>
                <% String detStatus = (String) itemDetail.get("status"); %>
                <% if ("PENDING".equals(detStatus)) { %>
                    <span class="badge bg-gray">PENDING</span>
                <% } else if ("IN_REVIEW".equals(detStatus)) { %>
                    <span class="badge bg-blue">IN REVIEW</span>
                <% } else if ("ACTION_REQUIRED".equals(detStatus)) { %>
                    <span class="badge bg-red">ACTION REQUIRED</span>
                <% } else if ("CLEARED".equals(detStatus)) { %>
                    <span class="badge bg-green">CLEARED</span>
                <% } else { %>
                    <span class="badge bg-gray"><%= detStatus %></span>
                <% } %>
            </div>
            <div class="info-row"><b>Remarks:</b>
                <%= itemDetail.get("remarks") != null ? itemDetail.get("remarks") : "Wala" %>
            </div>
            <div class="info-row"><b>Submitted:</b> <%= itemDetail.get("submitted_at") %></div>

            <!-- History -->
            <h3 style="margin-top:18px;">Review History</h3>
            <% List<String[]> history = (List<String[]>) itemDetail.get("history"); %>
            <% if (history == null || history.isEmpty()) { %>
                <p class="empty">Wala pang history ang item na ito.</p>
            <% } else { %>
            <table>
                <tr>
                    <th>Changed By</th>
                    <th>Old Status</th>
                    <th>New Status</th>
                    <th>Remarks</th>
                    <th>Date</th>
                </tr>
                <% for (String[] h : history) { %>
                <tr>
                    <td><%= h[4] != null ? h[4] : "-" %></td>
                    <td><%= h[0] != null ? h[0] : "-" %></td>
                    <td><%= h[1] %></td>
                    <td><%= h[2] != null ? h[2] : "-" %></td>
                    <td><%= h[3] %></td>
                </tr>
                <% } %>
            </table>
            <% } %>

            <!-- Decision form -->
            <h3 style="margin-top:18px;">Make Decision</h3>
            <% if (!"CLEARED".equals(itemDetail.get("status")) && !"ACTION_REQUIRED".equals(itemDetail.get("status"))) { %>
            <form action="${pageContext.request.contextPath}/officer/queue" method="POST">
                <input type="hidden" name="itemId" value="<%= itemDetail.get("item_id") %>">
                <input type="hidden" name="officeId" value="<%= selectedOfficeId %>">
                <input type="hidden" name="itemId" value="<%= itemDetail.get("item_id") %>">
                <input type="hidden" name="officeId" value="<%= selectedOfficeId %>">
                <div style="margin-bottom:10px;">
                    <label style="font-size:13px; color:#555;">Decision:</label>
                    <select name="action" id="decisionSelect" required>
                        <option value="">-- Pumili --</option>
                        <option value="cleared">CLEARED - Maayos na ang lahat</option>
                        <option value="actionrequired">ACTION REQUIRED - Kailangan ng aksyon</option>
                    </select>
                </div>
                <div style="margin-bottom:10px;">
                    <label style="font-size:13px; color:#555;">Remarks (kailangan kapag ACTION REQUIRED):</label>
                    <textarea name="remarks" id="remarksBox" rows="3" placeholder="I-type ang iyong remarks dito..."></textarea>
                </div>
                <button type="submit" class="btn btn-green">SUBMIT DECISION</button>
                <a class="btn btn-gray" href="${pageContext.request.contextPath}/officer/queue?officeId=<%= selectedOfficeId %>">Back to Queue</a>
            </form>
            <script>
                // I-require ang remarks kapag ACTION REQUIRED ang napili
                var select = document.getElementById("decisionSelect");
                var remarks = document.getElementById("remarksBox");
                select.addEventListener("change", function() {
                    remarks.required = (this.value === "actionrequired");
                });
            </script>
            <% } else { %>
                <p class="empty">Na-review na ang item na ito (status: <%= itemDetail.get("status") %>).</p>
                <a class="btn btn-gray" href="${pageContext.request.contextPath}/officer/queue?officeId=<%= selectedOfficeId %>">Back to Queue</a>
            <% } %>
        </div>
        <% } %>

    </div>
</body>
</html>
