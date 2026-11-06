<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na admin mula sa session
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga data na pinadala ng OfficeServlet
    List<String[]> offices = (List<String[]>) request.getAttribute("offices");
    List<String[]> programs = (List<String[]>) request.getAttribute("programs");
    List<String[]> departments = (List<String[]>) request.getAttribute("departments");

    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Offices &amp; Programs - Clearance System</title>
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

        /* ===== Form para sa bagong office/program ===== */
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
        .edit-row form { display: grid; grid-template-columns: 1fr 1fr 1fr auto auto; gap: 8px; align-items: end; }
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
        <h2>OFFICES &amp; PROGRAMS</h2>
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

        <!-- ===== MANAGE OFFICES ===== -->
        <div class="card">
            <h3>Manage Offices (<%= offices.size() %>)</h3>

            <!-- Form para sa bagong office -->
            <form action="${pageContext.request.contextPath}/admin/offices" method="POST" class="form-grid" style="margin-bottom:18px;">
                <input type="hidden" name="entity" value="office">
                <input type="hidden" name="action" value="create">
                <div>
                    <label>Office Code</label>
                    <input type="text" name="code" placeholder="hal. OAS" required>
                </div>
                <div>
                    <label>Office Name</label>
                    <input type="text" name="name" placeholder="hal. Office of the Academic Secretariat" required>
                </div>
                <button type="submit">ADD OFFICE</button>
            </form>

            <table>
                <tr>
                    <th>ID</th>
                    <th>Code</th>
                    <th>Office Name</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <% for (String[] o : offices) {
                    int officeId = Integer.parseInt(o[0]);
                    boolean isActive = "1".equals(o[3]);
                %>
                <tr>
                    <td><%= o[0] %></td>
                    <td><%= o[1] %></td>
                    <td><%= o[2] %></td>
                    <td>
                        <% if (isActive) { %>
                            <span class="badge bg-green">ACTIVE</span>
                        <% } else { %>
                            <span class="badge bg-red">INACTIVE</span>
                        <% } %>
                    </td>
                    <td>
                        <a href="javascript:void(0)" class="link" onclick="toggleEdit('editOffice<%= officeId %>')">Edit</a>
                        <% if (isActive) { %>
                            <form action="${pageContext.request.contextPath}/admin/offices" method="POST" style="display:inline;">
                                <input type="hidden" name="entity" value="office">
                                <input type="hidden" name="action" value="deactivate">
                                <input type="hidden" name="id" value="<%= officeId %>">
                                <button type="submit" class="link link-danger" style="background:none;border:none;cursor:pointer;">Deactivate</button>
                            </form>
                        <% } else { %>
                            <form action="${pageContext.request.contextPath}/admin/offices" method="POST" style="display:inline;">
                                <input type="hidden" name="entity" value="office">
                                <input type="hidden" name="action" value="activate">
                                <input type="hidden" name="id" value="<%= officeId %>">
                                <button type="submit" class="link link-green" style="background:none;border:none;cursor:pointer;">Activate</button>
                            </form>
                        <% } %>
                    </td>
                </tr>
                <!-- Inline edit form para sa office na ito -->
                <tr class="edit-row hidden" id="editOffice<%= officeId %>">
                    <td colspan="5">
                        <form action="${pageContext.request.contextPath}/admin/offices" method="POST">
                            <input type="hidden" name="entity" value="office">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="<%= officeId %>">
                            <div>
                                <label>Office Code</label>
                                <input type="text" name="code" value="<%= o[1] %>" required>
                            </div>
                            <div>
                                <label>Office Name</label>
                                <input type="text" name="name" value="<%= o[2] %>" required>
                            </div>
                            <button type="submit" class="btn btn-save">Save</button>
                            <button type="button" class="btn btn-cancel" onclick="toggleEdit('editOffice<%= officeId %>')">Cancel</button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </table>
        </div>

        <!-- ===== MANAGE PROGRAMS ===== -->
        <div class="card">
            <h3>Manage Programs (<%= programs.size() %>)</h3>

            <!-- Form para sa bagong program -->
            <form action="${pageContext.request.contextPath}/admin/offices" method="POST" class="form-grid" style="margin-bottom:18px;">
                <input type="hidden" name="entity" value="program">
                <input type="hidden" name="action" value="create">
                <div>
                    <label>Department</label>
                    <select name="deptId" required>
                        <% for (String[] d : departments) { %>
                            <option value="<%= d[0] %>"><%= d[1] %> - <%= d[2] %></option>
                        <% } %>
                    </select>
                </div>
                <div>
                    <label>Program Code</label>
                    <input type="text" name="code" placeholder="hal. BSCS" required>
                </div>
                <div class="full">
                    <label>Program Name</label>
                    <input type="text" name="name" placeholder="hal. Bachelor of Science in Computer Science" required>
                </div>
                <button type="submit">ADD PROGRAM</button>
            </form>

            <table>
                <tr>
                    <th>ID</th>
                    <th>Code</th>
                    <th>Program Name</th>
                    <th>Department</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <% for (String[] p : programs) {
                    int programId = Integer.parseInt(p[0]);
                    boolean isActive = "1".equals(p[4]);
                %>
                <tr>
                    <td><%= p[0] %></td>
                    <td><%= p[2] %></td>
                    <td><%= p[3] %></td>
                    <td><%= p[6] %></td>
                    <td>
                        <% if (isActive) { %>
                            <span class="badge bg-green">ACTIVE</span>
                        <% } else { %>
                            <span class="badge bg-red">INACTIVE</span>
                        <% } %>
                    </td>
                    <td>
                        <a href="javascript:void(0)" class="link" onclick="toggleEdit('editProgram<%= programId %>')">Edit</a>
                        <% if (isActive) { %>
                            <form action="${pageContext.request.contextPath}/admin/offices" method="POST" style="display:inline;">
                                <input type="hidden" name="entity" value="program">
                                <input type="hidden" name="action" value="deactivate">
                                <input type="hidden" name="id" value="<%= programId %>">
                                <button type="submit" class="link link-danger" style="background:none;border:none;cursor:pointer;">Deactivate</button>
                            </form>
                        <% } else { %>
                            <form action="${pageContext.request.contextPath}/admin/offices" method="POST" style="display:inline;">
                                <input type="hidden" name="entity" value="program">
                                <input type="hidden" name="action" value="activate">
                                <input type="hidden" name="id" value="<%= programId %>">
                                <button type="submit" class="link link-green" style="background:none;border:none;cursor:pointer;">Activate</button>
                            </form>
                        <% } %>
                    </td>
                </tr>
                <!-- Inline edit form para sa program na ito -->
                <tr class="edit-row hidden" id="editProgram<%= programId %>">
                    <td colspan="6">
                        <form action="${pageContext.request.contextPath}/admin/offices" method="POST">
                            <input type="hidden" name="entity" value="program">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="<%= programId %>">
                            <div>
                                <label>Department</label>
                                <select name="deptId">
                                    <% for (String[] d : departments) { %>
                                        <option value="<%= d[0] %>" <%= d[0].equals(p[1]) ? "selected" : "" %>><%= d[1] %> - <%= d[2] %></option>
                                    <% } %>
                                </select>
                            </div>
                            <div>
                                <label>Program Code</label>
                                <input type="text" name="code" value="<%= p[2] %>" required>
                            </div>
                            <div>
                                <label>Program Name</label>
                                <input type="text" name="name" value="<%= p[3] %>" required>
                            </div>
                            <button type="submit" class="btn btn-save">Save</button>
                            <button type="button" class="btn btn-cancel" onclick="toggleEdit('editProgram<%= programId %>')">Cancel</button>
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
