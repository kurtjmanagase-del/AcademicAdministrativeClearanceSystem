<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.clearancesystem.model.User" %>
<%@ page import="java.util.List" %>
<%
    // Kunin ang naka-login na admin mula sa session
    User user = (User) session.getAttribute("loggedUser");

    // Kunin ang mga data na pinadala ng CycleServlet
    List<String[]> cycles = (List<String[]>) request.getAttribute("cycles");
    List<String[]> terms = (List<String[]>) request.getAttribute("terms");
    List<String[]> activeOffices = (List<String[]>) request.getAttribute("activeOffices");
    List<List<String[]>> cycleOfficesList = (List<List<String[]>>) request.getAttribute("cycleOfficesList");

    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clearance Cycles - Clearance System</title>
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

        /* ===== Form para sa bagong cycle ===== */
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

        /* ===== Checkbox group para sa offices ===== */
        .office-checkboxes {
            display: grid; grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
            gap: 6px; margin-top: 6px;
        }
        .office-checkboxes label {
            font-size: 13px; color: #333; margin: 0; display: flex; align-items: center; gap: 6px;
        }
        .note { font-size: 12px; color: #888; margin-top: 6px; }

        /* ===== Table para sa listahan ===== */
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { background: #f1f5f9; text-align: left; padding: 9px 10px; }
        td { padding: 9px 10px; border-bottom: 1px solid #e2e8f0; }
        .badge { padding: 3px 9px; border-radius: 12px; font-size: 11px; color: #fff; }
        .bg-green { background: #27ae60; }
        .bg-red { background: #c0392b; }
        .bg-gray { background: #7f8c8d; }

        /* ===== Inline edit row ===== */
        .edit-row td { background: #f8fafc; padding: 12px; }
        .edit-row .form-grid { grid-template-columns: 1fr 1fr 1fr 1fr; }
        .edit-row .btn-row { grid-column: 1 / -1; display: flex; gap: 8px; margin-top: 8px; }
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
        <h2>CLEARANCE CYCLES</h2>
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

        <!-- ===== CREATE CLEARANCE CYCLE ===== -->
        <div class="card">
            <h3>Create Clearance Cycle</h3>

            <form action="${pageContext.request.contextPath}/admin/cycles" method="POST" class="form-grid">
                <input type="hidden" name="action" value="create">
                <div>
                    <label>Academic Term</label>
                    <select name="termId" required>
                        <option value="">-- Pumili ng Term --</option>
                        <% for (String[] t : terms) { %>
                            <option value="<%= t[0] %>"><%= t[1] %> - <%= t[2] %></option>
                        <% } %>
                    </select>
                </div>
                <div>
                    <label>Cycle Name</label>
                    <input type="text" name="cycleName" placeholder="hal. End-of-Semester Clearance" required>
                </div>
                <div>
                    <label>Opens</label>
                    <input type="date" name="opensAt">
                </div>
                <div>
                    <label>Closes</label>
                    <input type="date" name="closesAt">
                </div>
                <div class="full">
                    <label>Required Offices (checkbox = kailangang mag-clear dito)</label>
                    <div class="office-checkboxes">
                        <% for (String[] o : activeOffices) { %>
                            <label>
                                <input type="checkbox" name="values" value="<%= o[0] %>">
                                <%= o[1] %> - <%= o[2] %>
                            </label>
                        <% } %>
                    </div>
                    <p class="note">Note: Ang mga opisina na ito ang magiging required offices ng cycle sa clearance_requirements.</p>
                </div>
                <button type="submit">CREATE CYCLE</button>
            </form>
        </div>

        <!-- ===== ALL CLEARANCE CYCLES ===== -->
        <div class="card">
            <h3>All Clearance Cycles (<%= cycles.size() %>)</h3>

            <table>
                <tr>
                    <th>ID</th>
                    <th>Cycle Name</th>
                    <th>Term</th>
                    <th>Opens</th>
                    <th>Closes</th>
                    <th>Offices</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                <% for (int i = 0; i < cycles.size(); i++) {
                    String[] c = cycles.get(i);
                    int cycleId = Integer.parseInt(c[0]);
                    String status = c[7];
                    List<String[]> cycleOffices = cycleOfficesList.get(i);

                    // Bumuo ng comma-separated list ng office codes
                    StringBuilder officesText = new StringBuilder();
                    for (String[] of : cycleOffices) {
                        if (officesText.length() > 0) officesText.append(", ");
                        officesText.append(of[1]);
                    }
                %>
                <tr>
                    <td><%= c[0] %></td>
                    <td><%= c[2] %></td>
                    <td><%= c[3] %> - <%= c[4] %></td>
                    <td><%= c[5] != null ? c[5] : "-" %></td>
                    <td><%= c[6] != null ? c[6] : "-" %></td>
                    <td><%= officesText.length() > 0 ? officesText.toString() : "-" %></td>
                    <td>
                        <% if ("OPEN".equals(status)) { %>
                            <span class="badge bg-green">OPEN</span>
                        <% } else if ("CLOSED".equals(status)) { %>
                            <span class="badge bg-red">CLOSED</span>
                        <% } else { %>
                            <span class="badge bg-gray">DRAFT</span>
                        <% } %>
                    </td>
                    <td>
                        <a href="javascript:void(0)" class="link" onclick="toggleEdit('editCycle<%= cycleId %>')">Edit</a>
                        <% if ("OPEN".equals(status)) { %>
                            <form action="${pageContext.request.contextPath}/admin/cycles" method="POST" style="display:inline;">
                                <input type="hidden" name="action" value="close">
                                <input type="hidden" name="id" value="<%= cycleId %>">
                                <button type="submit" class="link link-danger" style="background:none;border:none;cursor:pointer;">Close</button>
                            </form>
                        <% } else { %>
                            <form action="${pageContext.request.contextPath}/admin/cycles" method="POST" style="display:inline;">
                                <input type="hidden" name="action" value="open">
                                <input type="hidden" name="id" value="<%= cycleId %>">
                                <button type="submit" class="link link-green" style="background:none;border:none;cursor:pointer;">Open</button>
                            </form>
                        <% } %>
                    </td>
                </tr>
                <!-- Inline edit form para sa cycle na ito -->
                <tr class="edit-row hidden" id="editCycle<%= cycleId %>">
                    <td colspan="8">
                        <form action="${pageContext.request.contextPath}/admin/cycles" method="POST" class="form-grid">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="id" value="<%= cycleId %>">
                            <div>
                                <label>Academic Term</label>
                                <select name="termId">
                                    <% for (String[] t : terms) { %>
                                        <option value="<%= t[0] %>" <%= t[0].equals(c[1]) ? "selected" : "" %>><%= t[1] %> - <%= t[2] %></option>
                                    <% } %>
                                </select>
                            </div>
                            <div>
                                <label>Cycle Name</label>
                                <input type="text" name="cycleName" value="<%= c[2] %>" required>
                            </div>
                            <div>
                                <label>Opens</label>
                                <input type="date" name="opensAt" value="<%= c[5] != null ? c[5] : "" %>">
                            </div>
                            <div>
                                <label>Closes</label>
                                <input type="date" name="closesAt" value="<%= c[6] != null ? c[6] : "" %>">
                            </div>
                            <div class="full">
                                <label>Required Offices (checkbox = kailangang mag-clear dito)</label>
                                <div class="office-checkboxes" id="editCheckboxes<%= cycleId %>">
                                    <% for (String[] o : activeOffices) { %>
                                        <label>
                                            <input type="checkbox" name="values" value="<%= o[0] %>">
                                            <%= o[1] %> - <%= o[2] %>
                                        </label>
                                    <% } %>
                                </div>
                            </div>
                            <div class="btn-row">
                                <button type="submit" class="btn btn-save">Save</button>
                                <button type="button" class="btn btn-cancel" onclick="toggleEdit('editCycle<%= cycleId %>')">Cancel</button>
                            </div>
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

        // I-check ang mga checkbox ng edit form base sa kasalukuyang assigned offices
        function checkEditOffices(cycleId, officeIds) {
            var checkboxes = document.querySelectorAll('#editCheckboxes' + cycleId + ' input[type="checkbox"]');
            for (var i = 0; i < checkboxes.length; i++) {
                if (officeIds.indexOf(checkboxes[i].value) !== -1) {
                    checkboxes[i].checked = true;
                }
            }
        }

        // I-pre-check ang mga office ng bawat cycle kapag binubuksan ang edit row
        <%
            // Bumuo ng JS array: cycleId -> [office_id, ...]
            for (int i = 0; i < cycles.size(); i++) {
                int cycleId = Integer.parseInt(cycles.get(i)[0]);
                List<String[]> cycleOffices = cycleOfficesList.get(i);
                StringBuilder jsIds = new StringBuilder("[");
                for (int j = 0; j < cycleOffices.size(); j++) {
                    if (j > 0) jsIds.append(",");
                    jsIds.append(cycleOffices.get(j)[0]);
                }
                jsIds.append("]");
        %>
        checkEditOffices(<%= cycleId %>, <%= jsIds.toString() %>);
        <% } %>
    </script>
</body>
</html>
