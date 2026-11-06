package com.clearancesystem.servlet;

import com.clearancesystem.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("loggedUser");
        String ctx = request.getContextPath();

        if (user.isAdmin()) {
            response.sendRedirect(ctx + "/admin/admin-dashboard.jsp");
        } else if (user.isRegistrar()) {
            response.sendRedirect(ctx + "/registrar/registrar-dashboard.jsp");
        } else if (user.isOfficer()) {
            response.sendRedirect(ctx + "/officer/officer-dashboard.jsp");
        } else if (user.isStudent()) {
            response.sendRedirect(ctx + "/student/student-dashboard.jsp");
        } else {

            response.sendRedirect(ctx + "/logout");
        }
    }
}
