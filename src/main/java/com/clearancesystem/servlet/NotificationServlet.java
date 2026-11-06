package com.clearancesystem.servlet;

import com.clearancesystem.dao.ClearanceDAO;
import com.clearancesystem.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/student/notifications")
public class NotificationServlet extends HttpServlet {

    private final ClearanceDAO clearanceDAO = new ClearanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("loggedUser");
        String ctx = request.getContextPath();

        if (user == null || !user.isStudent()) {
            response.sendRedirect(ctx + "/index.jsp");
            return;
        }

        List<String[]> notifications = clearanceDAO.listNotifications(user.getUserId());
        request.setAttribute("notifications", notifications);

        request.getRequestDispatcher("/student/notifications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("loggedUser");
        String ctx = request.getContextPath();

        if (user == null || !user.isStudent()) {
            response.sendRedirect(ctx + "/index.jsp");
            return;
        }

        clearanceDAO.markAllRead(user.getUserId());
        response.sendRedirect(ctx + "/student/notifications");
    }
}
