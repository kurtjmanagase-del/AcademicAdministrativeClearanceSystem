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

@WebServlet({"/student/clearance", "/student/respond"})
public class StudentServlet extends HttpServlet {

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

        String[] cycle = clearanceDAO.getActiveCycle();

        if (cycle != null) {
            int cycleId = Integer.parseInt(cycle[0]);

            int requestId = clearanceDAO.findOrCreateRequest(user.getStudentId(), cycleId);

            request.setAttribute("cycle", cycle);
            request.setAttribute("requestId", requestId);
            request.setAttribute("studentRequest", clearanceDAO.getStudentRequest(requestId));
            request.setAttribute("items", clearanceDAO.listItemsWithOffices(requestId));
        }

        request.setAttribute("notifications", clearanceDAO.listNotifications(user.getUserId()));

        request.getRequestDispatcher("/student/my-clearance.jsp").forward(request, response);
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

        int itemId = parseInt(request.getParameter("itemId"));
        String responseText = request.getParameter("response");

        if (itemId > 0 && responseText != null && !responseText.trim().isEmpty()) {
            clearanceDAO.respondItem(itemId, responseText.trim(), user.getUserId());
        }

        response.sendRedirect(ctx + "/student/clearance");
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
