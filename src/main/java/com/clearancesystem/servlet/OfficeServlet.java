package com.clearancesystem.servlet;

import com.clearancesystem.dao.OfficeDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/offices")
public class OfficeServlet extends HttpServlet {

    private final OfficeDAO officeDAO = new OfficeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> offices = officeDAO.listOffices();
        List<String[]> programs = officeDAO.listPrograms();
        List<String[]> departments = officeDAO.listDepartments();

        request.setAttribute("offices", offices);
        request.setAttribute("programs", programs);
        request.setAttribute("departments", departments);

        request.getRequestDispatcher("/admin/office-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String entity = request.getParameter("entity");
        String action = request.getParameter("action");

        if ("office".equals(entity)) {
            handleOffice(request, action);
        } else if ("program".equals(entity)) {
            handleProgram(request, action);
        } else {
            request.setAttribute("error", "Hindi kilala ang entity na pinadala.");
        }

        doGet(request, response);
    }

    private void handleOffice(HttpServletRequest request, String action) {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        int id = parseInt(request.getParameter("id"));

        boolean needsCode = "create".equals(action) || "update".equals(action);
        if (needsCode && (code == null || code.trim().isEmpty())) {
            request.setAttribute("error", "Hindi ma-save ang office - kailangan ang Office Code.");
            return;
        }

        if ("create".equals(action)) {
            boolean ok = officeDAO.createOffice(code.trim(), name != null ? name.trim() : "");
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Nagawa na ang office '" + code.trim() + "'!"
                       : "Hindi nagawa ang office. Baka paulit-ulit ang code.");
        } else if ("update".equals(action)) {
            boolean ok = officeDAO.updateOffice(id, code.trim(), name != null ? name.trim() : "");
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-update na ang office!" : "Hindi na-update ang office. Baka paulit-ulit ang code.");
        } else if ("activate".equals(action)) {
            boolean ok = officeDAO.setOfficeActive(id, true);
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-activate na ang office." : "Hindi na-activate ang office.");
        } else if ("deactivate".equals(action)) {
            boolean ok = officeDAO.setOfficeActive(id, false);
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-deactivate na ang office." : "Hindi na-deactivate ang office.");
        } else {
            request.setAttribute("error", "Hindi kilala ang action na '" + action + "'.");
        }
    }

    private void handleProgram(HttpServletRequest request, String action) {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        int deptId = parseInt(request.getParameter("deptId"));
        int id = parseInt(request.getParameter("id"));

        boolean needsCode = "create".equals(action) || "update".equals(action);
        if (needsCode && (code == null || code.trim().isEmpty())) {
            request.setAttribute("error", "Hindi ma-save ang program - kailangan ang Program Code.");
            return;
        }

        if (deptId <= 0 && ("create".equals(action) || "update".equals(action))) {
            request.setAttribute("error", "Hindi ma-save ang program - kailangang pumili ng Department.");
            return;
        }

        if ("create".equals(action)) {
            boolean ok = officeDAO.createProgram(deptId, code.trim(), name != null ? name.trim() : "");
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Nagawa na ang program '" + code.trim() + "'!"
                       : "Hindi nagawa ang program. Baka paulit-ulit ang code.");
        } else if ("update".equals(action)) {
            boolean ok = officeDAO.updateProgram(id, deptId, code.trim(), name != null ? name.trim() : "");
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-update na ang program!" : "Hindi na-update ang program. Baka paulit-ulit ang code.");
        } else if ("activate".equals(action)) {
            boolean ok = officeDAO.setProgramActive(id, true);
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-activate na ang program." : "Hindi na-activate ang program.");
        } else if ("deactivate".equals(action)) {
            boolean ok = officeDAO.setProgramActive(id, false);
            request.setAttribute(ok ? "success" : "error",
                    ok ? "Na-deactivate na ang program." : "Hindi na-deactivate ang program.");
        } else {
            request.setAttribute("error", "Hindi kilala ang action na '" + action + "'.");
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
