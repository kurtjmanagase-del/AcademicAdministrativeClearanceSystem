package com.clearancesystem.servlet;

import com.clearancesystem.dao.AuditDAO;
import com.clearancesystem.dao.UserDAO;
import com.clearancesystem.model.User;
import com.clearancesystem.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final AuditDAO auditDAO = new AuditDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "create";
        }

        switch (action) {
            case "create":
                handleCreate(request, response);
                break;
            case "edit":
                handleEdit(request, response);
                break;
            case "resetpass":
                handleResetPassword(request, response);
                break;
            case "disable":
                handleSetStatus(request, response, "DISABLED");
                break;
            case "activate":
                handleSetStatus(request, response, "ACTIVE");
                break;
            default:
                loadPage(request, response);
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        int roleId = parseInt(request.getParameter("roleId"));

        if (username == null || username.trim().isEmpty()
                || password == null || password.length() < 4
                || fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Kulang ang impormasyon. Password dapat 4+ characters.");
            loadPage(request, response);
            return;
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        int userId = userDAO.createUser(username.trim(), passwordHash,
                fullName.trim(), email != null ? email.trim() : "", roleId);

        if (userId == -1) {
            request.setAttribute("error", "Hindi nagawa ang account. Baka paulit-ulit ang username.");
            loadPage(request, response);
            return;
        }

        try {
            User loggedIn = (User) request.getSession().getAttribute("loggedUser");
            String roleCode = getRoleCode(roleId);
            auditDAO.log(loggedIn != null ? loggedIn.getUserId() : 0,
                    "USER_CREATE", "USER", userId,
                    "Username: " + username + ", Role: " + roleCode);
        } catch (Exception e) {
            System.err.println("[UserServlet] audit log (USER_CREATE) error: " + e.getMessage());
        }

        String roleCode = getRoleCode(roleId);
        if ("STUDENT".equals(roleCode)) {
            String studentNumber = request.getParameter("studentNumber");
            int programId = parseInt(request.getParameter("programId"));
            int yearLevel = parseInt(request.getParameter("yearLevel"));
            if (studentNumber == null || studentNumber.trim().isEmpty() || programId <= 0) {
                request.setAttribute("error", "Kailangan ang Student Number at Program para sa student account.");
                loadPage(request, response);
                return;
            }
            if (!userDAO.createStudent(userId, studentNumber.trim(), programId, yearLevel)) {
                request.setAttribute("error", "Nagawa ang account pero may error sa student profile.");
            }
        }

        if ("OFFICER".equals(roleCode)) {
            int officeId = parseInt(request.getParameter("officeId"));
            if (officeId > 0) {
                userDAO.assignOffice(userId, officeId);
            }
        }

        request.setAttribute("success", "Account '" + username + "' ay nagawa na!");
        loadPage(request, response);
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = parseInt(request.getParameter("userId"));
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String roleCode = request.getParameter("roleCode");

        if (userId <= 0 || fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Kulang ang impormasyon para sa edit.");
            loadPage(request, response);
            return;
        }

        userDAO.updateProfile(userId, fullName.trim(), email != null ? email.trim() : "");

        if ("STUDENT".equals(roleCode)) {
            String studentNumber = request.getParameter("studentNumber");
            int programId = parseInt(request.getParameter("programId"));
            int yearLevel = parseInt(request.getParameter("yearLevel"));
            if (studentNumber != null && !studentNumber.trim().isEmpty() && programId > 0) {
                userDAO.updateStudent(userId, studentNumber.trim(), programId, yearLevel);
            }
        }

        if ("OFFICER".equals(roleCode)) {
            int officeId = parseInt(request.getParameter("officeId"));
            if (officeId > 0) {
                userDAO.updateOfficeAssignment(userId, officeId);
            }
        }

        request.setAttribute("success", "Account ay na-update na!");
        loadPage(request, response);
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = parseInt(request.getParameter("userId"));
        String newPassword = request.getParameter("newPassword");

        if (userId <= 0 || newPassword == null || newPassword.length() < 4) {
            request.setAttribute("error", "Password dapat 4+ characters.");
            loadPage(request, response);
            return;
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        if (userDAO.resetPassword(userId, newHash)) {
            request.setAttribute("success", "Password ay na-reset na!");
        } else {
            request.setAttribute("error", "Hindi ma-reset ang password.");
        }
        loadPage(request, response);
    }

    private void handleSetStatus(HttpServletRequest request, HttpServletResponse response,
                                 String status) throws ServletException, IOException {

        int userId = parseInt(request.getParameter("userId"));

        User loggedIn = (User) request.getSession().getAttribute("loggedUser");
        if (loggedIn != null && loggedIn.getUserId() == userId && "DISABLED".equals(status)) {
            request.setAttribute("error", "Hindi mo pwedeng i-disable ang sarili mong account.");
            loadPage(request, response);
            return;
        }

        if (userDAO.setAccountStatus(userId, status)) {
            String msg = "DISABLED".equals(status) ? "Account ay na-disable na." : "Account ay na-activate na.";
            request.setAttribute("success", msg);

            try {
                String action = "DISABLED".equals(status) ? "USER_DISABLE" : "USER_ACTIVATE";
                auditDAO.log(loggedIn != null ? loggedIn.getUserId() : 0,
                        action, "USER", userId, "Account status naging: " + status);
            } catch (Exception e) {
                System.err.println("[UserServlet] audit log (set status) error: " + e.getMessage());
            }
        } else {
            request.setAttribute("error", "Hindi naitago ang status.");
        }
        loadPage(request, response);
    }

    private void loadPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> users = userDAO.listAll();
        List<String[]> roles = userDAO.listRoles();
        List<String[]> programs = userDAO.listPrograms();
        List<String[]> offices = userDAO.listOffices();

        request.setAttribute("users", users);
        request.setAttribute("roles", roles);
        request.setAttribute("programs", programs);
        request.setAttribute("offices", offices);

        request.getRequestDispatcher("/admin/user-list.jsp").forward(request, response);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getRoleCode(int roleId) {
        for (String[] role : userDAO.listRoles()) {
            if (Integer.parseInt(role[0]) == roleId) {
                return role[1];
            }
        }
        return "";
    }
}
