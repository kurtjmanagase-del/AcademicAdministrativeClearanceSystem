package com.clearancesystem.servlet;

import com.clearancesystem.dao.CycleDAO;
import com.clearancesystem.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/cycles")
public class CycleServlet extends HttpServlet {

    private final CycleDAO cycleDAO = new CycleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> cycles = cycleDAO.listCycles();
        List<String[]> terms = cycleDAO.listTerms();
        List<String[]> activeOffices = cycleDAO.listActiveOffices();

        List<List<String[]>> cycleOfficesList = new ArrayList<>();
        for (String[] cycle : cycles) {
            int cycleId = Integer.parseInt(cycle[0]);
            cycleOfficesList.add(cycleDAO.getCycleOffices(cycleId));
        }

        request.setAttribute("cycles", cycles);
        request.setAttribute("terms", terms);
        request.setAttribute("activeOffices", activeOffices);
        request.setAttribute("cycleOfficesList", cycleOfficesList);

        request.getRequestDispatcher("/admin/cycle-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            handleCreate(request);
        } else if ("update".equals(action)) {
            handleUpdate(request);
        } else if ("open".equals(action)) {
            handleStatusChange(request, "OPEN");
        } else if ("close".equals(action)) {
            handleStatusChange(request, "CLOSED");
        } else {
            request.setAttribute("error", "Hindi kilala ang action na '" + action + "'.");
        }

        doGet(request, response);
    }

    private void handleCreate(HttpServletRequest request) {
        String termId = request.getParameter("termId");
        String cycleName = request.getParameter("cycleName");
        String opensAt = request.getParameter("opensAt");
        String closesAt = request.getParameter("closesAt");

        if (termId == null || termId.trim().isEmpty()
                || cycleName == null || cycleName.trim().isEmpty()) {
            request.setAttribute("error", "Hindi nagawa ang cycle - kailangan ang Term at Cycle Name.");
            return;
        }

        User loggedUser = (User) request.getSession().getAttribute("loggedUser");
        int createdBy = (loggedUser != null) ? loggedUser.getUserId() : 0;

        int newCycleId = cycleDAO.createCycle(
                parseInt(termId), cycleName.trim(),
                emptyToNull(opensAt), emptyToNull(closesAt), createdBy);

        if (newCycleId > 0) {

            List<Integer> officeIds = parseOfficeIds(request.getParameterValues("values"));
            cycleDAO.setCycleOffices(newCycleId, officeIds);
            request.setAttribute("success", "Nagawa na ang cycle '" + cycleName.trim() + "'!");
        } else {
            request.setAttribute("error", "Hindi nagawa ang cycle. Pakisubukan muli.");
        }
    }

    private void handleUpdate(HttpServletRequest request) {
        int id = parseInt(request.getParameter("id"));
        String termId = request.getParameter("termId");
        String cycleName = request.getParameter("cycleName");
        String opensAt = request.getParameter("opensAt");
        String closesAt = request.getParameter("closesAt");

        if (id <= 0 || termId == null || termId.trim().isEmpty()
                || cycleName == null || cycleName.trim().isEmpty()) {
            request.setAttribute("error", "Hindi na-update ang cycle - kailangan ang Term at Cycle Name.");
            return;
        }

        boolean ok = cycleDAO.updateCycle(
                id, parseInt(termId), cycleName.trim(),
                emptyToNull(opensAt), emptyToNull(closesAt));

        if (ok) {

            List<Integer> officeIds = parseOfficeIds(request.getParameterValues("values"));
            cycleDAO.setCycleOffices(id, officeIds);
            request.setAttribute("success", "Na-update na ang cycle!");
        } else {
            request.setAttribute("error", "Hindi na-update ang cycle. Pakisubukan muli.");
        }
    }

    private void handleStatusChange(HttpServletRequest request, String status) {
        int id = parseInt(request.getParameter("id"));
        boolean ok = cycleDAO.setCycleStatus(id, status);
        request.setAttribute(ok ? "success" : "error",
                ok ? ("Na-" + status.toLowerCase() + " na ang cycle.")
                   : "Hindi nabago ang status ng cycle.");
    }

    private String emptyToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    private List<Integer> parseOfficeIds(String[] values) {
        List<Integer> ids = new ArrayList<>();
        if (values != null) {
            for (String v : values) {
                int id = parseInt(v);
                if (id > 0 && !ids.contains(id)) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
