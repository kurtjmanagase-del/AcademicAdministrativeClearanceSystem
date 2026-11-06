package com.clearancesystem.servlet;

import com.clearancesystem.dao.TermDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/terms")
public class TermServlet extends HttpServlet {

    private final TermDAO termDAO = new TermDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> terms = termDAO.listTerms();

        request.setAttribute("terms", terms);

        request.getRequestDispatcher("/admin/term-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int id = parseInt(request.getParameter("id"));
        String schoolYear = request.getParameter("schoolYear");
        String semesterName = request.getParameter("semesterName");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        boolean needsFields = "create".equals(action) || "update".equals(action);
        if (needsFields && (schoolYear == null || schoolYear.trim().isEmpty())) {
            request.setAttribute("error", "Hindi ma-save ang term - kailangan ang School Year.");
            doGet(request, response);
            return;
        }
        if (needsFields && (semesterName == null || semesterName.trim().isEmpty())) {
            request.setAttribute("error", "Hindi ma-save ang term - kailangan ang Semester.");
            doGet(request, response);
            return;
        }

        if ("create".equals(action)) {
            boolean ok = termDAO.createTerm(schoolYear.trim(), semesterName.trim(),
                    blankToNull(startDate), blankToNull(endDate));
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Nagawa na ang term '" + schoolYear.trim() + " - " + semesterName.trim() + "'!"
                       : "Hindi nagawa ang term. Baka paulit-ulit ang School Year + Semester.");
        } else if ("update".equals(action)) {
            boolean ok = termDAO.updateTerm(id, schoolYear.trim(), semesterName.trim(),
                    blankToNull(startDate), blankToNull(endDate));
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-update na ang term!"
                       : "Hindi na-update ang term. Baka paulit-ulit ang School Year + Semester.");
        } else if ("close".equals(action)) {
            boolean ok = termDAO.setTermStatus(id, "CLOSED");
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-CLOSE na ang term."
                       : "Hindi na-close ang term. Baka wala ang term.");
        } else if ("reopen".equals(action)) {
            boolean ok = termDAO.setTermStatus(id, "OPEN");
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-REOPEN na ang term."
                       : "Hindi na-reopen ang term. Baka wala ang term.");
        } else {
            request.setAttribute("error", "Hindi kilala ang action na '" + action + "'.");
        }

        doGet(request, response);
    }

    private String blankToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
