package com.clearancesystem.servlet;

import com.clearancesystem.dao.RegistrarDAO;
import com.clearancesystem.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/registrar/verification")
public class RegistrarServlet extends HttpServlet {

    private final RegistrarDAO registrarDAO = new RegistrarDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> queue = registrarDAO.getRegistrarQueue();
        request.setAttribute("queue", queue);

        List<String[]> completed = registrarDAO.listCompleted();
        request.setAttribute("completed", completed);

        int requestId = parseInt(request.getParameter("requestId"));
        request.setAttribute("selectedRequestId", requestId);
        if (requestId > 0) {
            List<String[]> items = registrarDAO.getRequestItems(requestId);
            request.setAttribute("items", items);

            for (String[] row : queue) {
                if (Integer.parseInt(row[0]) == requestId) {
                    request.setAttribute("detailStudent", row);
                    break;
                }
            }
        }

        request.getRequestDispatcher("/registrar/verification.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int requestId = parseInt(request.getParameter("requestId"));
        String reason = request.getParameter("reason");

        User loggedUser = (User) request.getSession().getAttribute("loggedUser");

        if (requestId > 0) {
            if ("approve".equals(action)) {
                registrarDAO.finalApprove(requestId, loggedUser.getUserId());
            } else if ("return".equals(action)) {

                if (reason == null || reason.trim().isEmpty()) {
                    reason = "Kailangan ng karagdagang aksyon bago ma-approve.";
                }
                registrarDAO.returnForAction(requestId, loggedUser.getUserId(), reason.trim());
            }
        }

        response.sendRedirect(request.getContextPath() + "/registrar/verification");
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
