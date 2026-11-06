package com.clearancesystem.servlet;

import com.clearancesystem.dao.OfficerDAO;
import com.clearancesystem.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/officer/queue")
public class OfficerServlet extends HttpServlet {

    private final OfficerDAO officerDAO = new OfficerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User loggedUser = (User) request.getSession().getAttribute("loggedUser");

        List<String[]> offices = officerDAO.getAssignedOffices(loggedUser.getUserId());
        request.setAttribute("offices", offices);

        int officeId = parseInt(request.getParameter("officeId"));
        int cycleId = parseInt(request.getParameter("cycleId"));
        request.setAttribute("selectedOfficeId", officeId);
        request.setAttribute("selectedCycleId", cycleId);

        if (officeId > 0) {
            List<String[]> queue = officerDAO.getQueue(officeId, cycleId);
            request.setAttribute("queue", queue);
        }

        int itemId = parseInt(request.getParameter("itemId"));
        if (itemId > 0) {
            Map<String, Object> itemDetail = officerDAO.getItemDetail(itemId);
            request.setAttribute("itemDetail", itemDetail);
        }

        request.getRequestDispatcher("/officer/queue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int itemId = parseInt(request.getParameter("itemId"));
        int officeId = parseInt(request.getParameter("officeId"));
        String remarks = request.getParameter("remarks");

        String newStatus = null;
        if ("cleared".equals(action)) {
            newStatus = "CLEARED";
        } else if ("actionrequired".equals(action)) {
            newStatus = "ACTION_REQUIRED";

            if (remarks == null || remarks.trim().isEmpty()) {
                remarks = "Action required ng opisina.";
            }
        }

        if (itemId > 0 && newStatus != null) {
            User loggedUser = (User) request.getSession().getAttribute("loggedUser");
            officerDAO.decide(itemId, newStatus, remarks, loggedUser.getUserId());
        }

        String redirect = request.getContextPath() + "/officer/queue";
        if (officeId > 0) {
            redirect += "?officeId=" + officeId;
        }
        response.sendRedirect(redirect);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
