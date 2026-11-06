package com.clearancesystem.servlet;

import com.clearancesystem.dao.AuditDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/audit")
public class AuditServlet extends HttpServlet {

    private final AuditDAO auditDAO = new AuditDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<String[]> logs = auditDAO.listAll();
        request.setAttribute("logs", logs);
        request.getRequestDispatcher("/admin/audit-log.jsp").forward(request, response);
    }
}
