<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na admin mula sa session
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga data na pinadala ng TermServlet
    List<String[]> terms = (List<String[]>) request.getAttribute("terms");

    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Academic Terms - Clearance System</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, Helvetica, sans-serif; background: #f4f6f9; }
        .topbar {
            background: #1e293b; color: #fff;
            padding: 14px 25px; display: flex;
            justify-content: space-between; align-items: center;
        }
        .topbar a { color: #cbd5e1; text-decoration: none; font-size: 13px; }
        .topbar .back { background: rgba(255,255,255,0.15); padding: 7px 14px; border-radius: 5px; }
        .content { max-width: 1000px; margin: 30px auto; padding: 0 15px; }
        .card {
            background: #fff; border-radius: 8px;
            padding: 22px; margin-bottom: 18px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.08);
        }
        .card h3 { color: #1e293b; margin-bottom: 15px; }
        .msg { padding: 10px 14px; border-radius: 5px; font-size: 13px; margin-bottom: 15px; }
        .msg-error { background: #fdecea; color: #c0392b; }
        .msg-success { background: #eafaf1; color: #27ae60; }

        /* ===== Form para sa bagong academic term ===== */
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
        .form-grid label { display: block; font-size: 13px; color: #555; margin-bottom: 4px; }
        .form-grid input, .form-grid select {
            width: 100%; padding: 8px; border: 1px solid #ccc;
            border-radius: 5px; font-size: 14px;
        }
        .form-grid .full { grid-column: 1 / -1; }
        .form-grid button {
            grid-column: 1 / -1;
            padding: 10px; background: #1e293b; color: #fff;
            border: none; border-radius: 5px; font-size: 14px; cursor: pointer;
        }
        .hidden { display: none; }

        /* ===== Table para sa listahan ===== */
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #f1f5f9; text-align: left; padding: 9px 10px; }
        td { padding: 9px 10px; border-bottom: 1px solid #e2e8f0; }
        .badge { padding: 3px 9px; border-radius: 12px; font-size: 11px; color: #fff; }
        .bg-green { background: #27ae60; }
        .bg-red { background: #c0392b; }

        /* ===== Inline edit row ===== */
        .edit-row td { background: #f8fafc; padding: 10px; }
        .edit-row form { display: grid; grid-template-columns: 1fr 1fr 1fr 1fr auto auto; gap: 8px; align-items: end; }
        .edit-row label { display: block; font-size: 12px; color: #555; margin-bottom: 3px; }
        .edit-row input, .edit-row select {
            width: 100%; padding: 6px; border: 1px solid #ccc;
            border-radius: 4px; font-size: 13px;
        }
        .btn { padding: 7px 12px; border: none; border-radius: 4px; font-size: 12px; cursor: pointer; text-decoration: none; }
        .btn-save { background: #1e293b; color: #fff; }
        .btn-cancel { background: #e2e8f0; color: #333; }
        .link { color: #2980b9; text-decoration: none; font-size: 12px; margin-right: 8px; }
        .link-danger { color: #c0392b; }
        .link-green { color: #27ae60; }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>ACADEMIC TERMS</h2>
        <div>
            <span style="font-size:13px;">Hi, <%= user.getFullName() %></span>
            <a class="back" href="${pageContext.request.contextPath}/admin/admin-dashboard.jsp">Dashboard</a>
            <a class="back" href="${pageContext.request.contextPath}/logout">Logout</a>
        </div>
    </div>

    <div class="content">
        <% if (error != null) { %>
            <div class="msg msg-error"><%= error %></div>
        <% } %>
        <% if (success != null) { %>
            <div class="msg msg-success"><%= success %></div>
        <% } %>

        <!-- ===== CREATE ACADEMIC TERM ===== -->
        <div class="card">
            <h3>Create Academic Term</h3>

            <!-- Form para sa bagong academic term -->
            <form action="${pageContext.request.contextPath}/admin/terms" method="POST" class="form-grid">
                <input type="hidden" name="action" value="create">
                <div>
                    <label>School Year</label>
                    <input type="text" name="schoolYear" placeholder="hal. 2026-2027" required>
                </div>
                <div>
                    <label>Semester</label>
                    <select name="semesterName" required>
                        <option value="">-- Pumili --</option>
                        <option value="First Semester">First Semester</option>
                        <option value="Second Semester">Second Semester</option>
                        <option value="Summer">Summer</option>
                    </select>
                </div>
                <div>
                    <label>Start Date</label>
                    <input type="date" name="startDate">
                </div>
                <div>
                    <label>End Date</label>
                    <input type="date" name="endDate">
                </div>
                <button type="submit">ADD ACADEMIC TERM</button>
            </form>
        </div>

        <!-- ===== ALL ACADEMIC TERMS ===== -->
        <div class="card">
            <h3>All Academic Terms (<%= terms.size() %>)</h3>

            <table>
                <tr>
                    <th>ID</th>
                    <th>School Year</th>
                    <th>Semester</th>
                    <th>Start</th>
                    <th>End</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <% for (String[] t : terms) {
                    int termId = Integer.parseInt(t[0]);
                    boolean isOpen = "OPEN".equals(t[5]);
                %>
                <tr>
                    <td><%= t[0] %></td>
                    <td><%= t[1] %></td>
                    <td><%= t[2] %></td>
                    <td><%= t[3] != null ? t[3] : "-" %></td>
                    <td><%= t[4] != null ? t[4] : "-" %></td>
                    <td>
                        <% if (isOpen) { %>
                            <span class="badge bg-green">OPEN</span>
                        <% } else { %>
                            <span class="badge bg-red">CLOSED</span>
                        <% } %>
                    </td>
                    <td>
                        <a href="javascript:void(0)" class="link" onclick="toggleEdit('editTerm<%= termId %>')">Edit</a>
                        <% if (isOpen) { %>
                            <form action="${pageContext.request.contextPath}/admin/terms" method="POST" style="display:inline;">
                                <input type="hidden" name="action" value="close">
                                <input type="hidden" name="id" value="<%= termId %>">
                                <button type="submit" class="link link-danger" style="background:none;border:none;cursor:pointer;">Close</button>
                            </form>
                        <% } else { %>
                            <form action="${pageContext.request.contextPath}/admin/terms" method="POST" style="display:inline;">
                                <input type="hidden" name="action" value="reopen">
                                <input type="hidden" name="id" value="<%= termId %>">
                                <button type="submit" class="link link-green" style="background:none;border:none;cursor:pointer;">Reopen</button>
                            </form>
                        <% } %>
                    </td>
                </tr>
                <!-- Inline edit form para sa term na ito -->
                <tr class="edit-row hidden" id="editTerm<%= termId %>">
                    <td colspan="7">
                        <form action="${pageContext.request.contextPath}/admin/terms" method="POST">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="<%= termId %>">
                            <div>
                                <label>School Year</label>
                                <input type="text" name="schoolYear" value="<%= t[1] %>" required>
                            </div>
                            <div>
                                <label>Semester</label>
                                <select name="semesterName" required>
                                    <option value="First Semester" <%= "First Semester".equals(t[2]) ? "selected" : "" %>>First Semester</option>
                                    <option value="Second Semester" <%= "Second Semester".equals(t[2]) ? "selected" : "" %>>Second Semester</option>
                                    <option value="Summer" <%= "Summer".equals(t[2]) ? "selected" : "" %>>Summer</option>
                                </select>
                            </div>
                            <div>
                                <label>Start Date</label>
                                <input type="date" name="startDate" value="<%= t[3] != null ? t[3] : "" %>">
                            </div>
                            <div>
                                <label>End Date</label>
                                <input type="date" name="endDate" value="<%= t[4] != null ? t[4] : "" %>">
                            </div>
                            <button type="submit" class="btn btn-save">Save</button>
                            <button type="button" class="btn btn-cancel" onclick="toggleEdit('editTerm<%= termId %>')">Cancel</button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>

    <script>
        // Ipakita o itago ang inline edit form ng isang row
        function toggleEdit(id) {
            var row = document.getElementById(id);
            row.classList.toggle("hidden");
        }
    </script>
</body>
</html>
