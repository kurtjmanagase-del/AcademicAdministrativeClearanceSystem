<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na admin mula sa session
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga data na pinadala ng UserServlet
    List<User> users = (List<User>) request.getAttribute("users");
    List<String[]> roles = (List<String[]>) request.getAttribute("roles");
    List<String[]> programs = (List<String[]>) request.getAttribute("programs");
    List<String[]> offices = (List<String[]>) request.getAttribute("offices");

    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Clearance System</title>
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

        /* ===== Form para sa bagong account ===== */
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
        .bg-blue { background: #2980b9; }
        .bg-gray { background: #7f8c8d; }
        .btn { display:inline-block; padding:5px 10px; border-radius:4px; font-size:12px;
               text-decoration:none; border:none; cursor:pointer; margin:2px 0; }
        .btn-dark { background:#1e293b; color:#fff; }
        .btn-green { background:#27ae60; color:#fff; }
        .btn-red { background:#c0392b; color:#fff; }
        .edit-row { display:none; background:#f8fafc; }
        .edit-row td { padding:12px; }
        .edit-grid { display:grid; grid-template-columns: 1fr 1fr 1fr; gap:10px; }
        .edit-grid label { display:block; font-size:12px; color:#555; margin-bottom:3px; }
        .edit-grid input, .edit-grid select {
            width:100%; padding:7px; border:1px solid #ccc; border-radius:5px; font-size:13px;
        }
    </style>
</head>
<body>
    <div class="topbar">
        <h2>USER MANAGEMENT</h2>
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

        <!-- ===== BAGONG ACCOUNT FORM ===== -->
        <div class="card">
            <h3>Create New Account</h3>
            <form action="${pageContext.request.contextPath}/admin/users" method="POST" class="form-grid">
                <div>
                    <label>Username / Student No.</label>
                    <input type="text" name="username" required>
                </div>
                <div>
                    <label>Password</label>
                    <input type="password" name="password" required>
                </div>
                <div>
                    <label>Full Name</label>
                    <input type="text" name="fullName" required>
                </div>
                <div>
                    <label>Email</label>
                    <input type="email" name="email">
                </div>
                <div>
                    <label>Role</label>
                    <select name="roleId" id="roleSelect" onchange="showExtraFields()">
                        <% for (String[] r : roles) { %>
                            <option value="<%= r[0] %>"><%= r[1] %> - <%= r[2] %></option>
                        <% } %>
                    </select>
                </div>

                <!-- Extra fields: para sa STUDENT -->
                <div id="studentFields" class="hidden full">
                    <hr style="margin:10px 0; border-color:#eee;">
                    <div class="form-grid">
                        <div>
                            <label>Student Number</label>
                            <input type="text" name="studentNumber">
                        </div>
                        <div>
                            <label>Year Level</label>
                            <select name="yearLevel">
                                <option value="1">1st Year</option>
                                <option value="2">2nd Year</option>
                                <option value="3">3rd Year</option>
                                <option value="4">4th Year</option>
                            </select>
                        </div>
                        <div class="full">
                            <label>Program</label>
                            <select name="programId">
                                <% for (String[] p : programs) { %>
                                    <option value="<%= p[0] %>"><%= p[1] %> - <%= p[2] %></option>
                                <% } %>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Extra field: para sa OFFICER -->
                <div id="officerFields" class="hidden full">
                    <hr style="margin:10px 0; border-color:#eee;">
                    <div>
                        <label>Assigned Office</label>
                        <select name="officeId">
                            <% for (String[] o : offices) { %>
                                <option value="<%= o[0] %>"><%= o[1] %> - <%= o[2] %></option>
                            <% } %>
                        </select>
                    </div>
                </div>

                <button type="submit">CREATE ACCOUNT</button>
            </form>
        </div>

        <!-- ===== LISTAHAN NG MGA ACCOUNTS ===== -->
        <div class="card">
            <h3>All Accounts (<%= users.size() %>)</h3>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Role</th>
                    <th>Student No.</th>
                    <th>Program</th>
                    <th>Offices</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <% for (User u : users) { %>
                <tr>
                    <td><%= u.getUserId() %></td>
                    <td><%= u.getUsername() %></td>
                    <td><%= u.getFullName() %></td>
                    <td><%= u.getRoleCode() %></td>
                    <td><%= u.getStudentNumber() != null ? u.getStudentNumber() : "-" %></td>
                    <td><%= u.getProgramCode() != null ? u.getProgramCode() : "-" %></td>
                    <td><%= u.getOfficeCodes() != null ? u.getOfficeCodes() : "-" %></td>
                    <td>
                        <% if ("ACTIVE".equals(u.getAccountStatus())) { %>
                            <span class="badge bg-green">ACTIVE</span>
                        <% } else { %>
                            <span class="badge bg-red">DISABLED</span>
                        <% } %>
                    </td>
                    <td>
                        <button class="btn btn-dark" onclick="toggleEdit('<%= u.getUserId() %>')">Edit</button>
                        <form action="${pageContext.request.contextPath}/admin/users" method="POST" style="display:inline;">
                            <input type="hidden" name="action" value="<%= "ACTIVE".equals(u.getAccountStatus()) ? "disable" : "activate" %>">
                            <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                            <% if ("ACTIVE".equals(u.getAccountStatus())) { %>
                                <button class="btn btn-red" type="submit">Disable</button>
                            <% } else { %>
                                <button class="btn btn-green" type="submit">Activate</button>
                            <% } %>
                        </form>
                        <button class="btn btn-dark" onclick="toggleReset('<%= u.getUserId() %>')">Reset Pass</button>
                    </td>
                </tr>

                <!-- ===== INLINE EDIT ROW (nakatago; pinapakita pag pinindot ang Edit) ===== -->
                <tr id="editRow-<%= u.getUserId() %>" class="edit-row">
                    <td colspan="9">
                        <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                            <input type="hidden" name="roleCode" value="<%= u.getRoleCode() %>">
                            <div class="edit-grid">
                                <div>
                                    <label>Full Name</label>
                                    <input type="text" name="fullName" value="<%= u.getFullName() %>" required>
                                </div>
                                <div>
                                    <label>Email</label>
                                    <input type="email" name="email" value="<%= u.getEmail() != null ? u.getEmail() : "" %>">
                                </div>
                                <% if ("STUDENT".equals(u.getRoleCode())) { %>
                                <div>
                                    <label>Student Number</label>
                                    <input type="text" name="studentNumber" value="<%= u.getStudentNumber() != null ? u.getStudentNumber() : "" %>">
                                </div>
                                <div>
                                    <label>Program</label>
                                    <select name="programId">
                                        <% for (String[] p : programs) { %>
                                            <option value="<%= p[0] %>" <%= (u.getProgramId() != null && Integer.parseInt(p[0]) == u.getProgramId()) ? "selected" : "" %>>
                                                <%= p[1] %> - <%= p[2] %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>
                                <div>
                                    <label>Year Level</label>
                                    <select name="yearLevel">
                                        <% for (int y = 1; y <= 4; y++) { %>
                                            <option value="<%= y %>" <%= (u.getYearLevel() != null && u.getYearLevel() == y) ? "selected" : "" %>><%= y %>st/nd/rd/th Year</option>
                                        <% } %>
                                    </select>
                                </div>
                                <% } else if ("OFFICER".equals(u.getRoleCode())) { %>
                                <div>
                                    <label>Assigned Office</label>
                                    <select name="officeId">
                                        <% for (String[] o : offices) { %>
                                            <option value="<%= o[0] %>"><%= o[1] %> - <%= o[2] %></option>
                                        <% } %>
                                    </select>
                                </div>
                                <% } %>
                            </div>
                            <div style="margin-top:10px;">
                                <button class="btn btn-green" type="submit">Save Changes</button>
                                <button class="btn btn-dark" type="button" onclick="toggleEdit('<%= u.getUserId() %>')">Cancel</button>
                            </div>
                        </form>
                    </td>
                </tr>

                <!-- ===== INLINE RESET PASSWORD ROW ===== -->
                <tr id="resetRow-<%= u.getUserId() %>" class="edit-row">
                    <td colspan="9">
                        <form action="${pageContext.request.contextPath}/admin/users" method="POST">
                            <input type="hidden" name="action" value="resetpass">
                            <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                            <label>New Password (min 4 chars)</label>
                            <input type="password" name="newPassword" required style="padding:7px;border:1px solid #ccc;border-radius:5px;">
                            <button class="btn btn-green" type="submit">Reset Password</button>
                            <button class="btn btn-dark" type="button" onclick="toggleReset('<%= u.getUserId() %>')">Cancel</button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>

    <script>
        // Ipakita/itago ang edit row ng isang user
        function toggleEdit(id) {
            var row = document.getElementById("editRow-" + id);
            var reset = document.getElementById("resetRow-" + id);
            if (row.style.display === "none" || row.style.display === "") {
                row.style.display = "table-row";
                reset.style.display = "none";
            } else {
                row.style.display = "none";
            }
        }

        // Ipakita/itago ang reset password row ng isang user
        function toggleReset(id) {
            var row = document.getElementById("resetRow-" + id);
            var edit = document.getElementById("editRow-" + id);
            if (row.style.display === "none" || row.style.display === "") {
                row.style.display = "table-row";
                edit.style.display = "none";
            } else {
                row.style.display = "none";
            }
        }

        // Ipakita ang extra fields depende sa napiling role
        function showExtraFields() {
            var role = document.getElementById("roleSelect");
            var selected = role.options[role.selectedIndex].text;
            var studentFields = document.getElementById("studentFields");
            var officerFields = document.getElementById("officerFields");

            studentFields.classList.add("hidden");
            officerFields.classList.add("hidden");

            if (selected.startsWith("STUDENT")) {
                studentFields.classList.remove("hidden");
            } else if (selected.startsWith("OFFICER")) {
                officerFields.classList.remove("hidden");
            }
        }
    </script>
</body>
</html>
