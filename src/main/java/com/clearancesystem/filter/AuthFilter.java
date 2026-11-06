package com.clearancesystem.filter;

import com.clearancesystem.model.User;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("loggedUser") == null) {
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User user = (User) session.getAttribute("loggedUser");
        String path = req.getRequestURI();

        boolean allowed = false;
        if (path.contains("/student/") && user.isStudent()) {
            allowed = true;
        } else if (path.contains("/officer/") && user.isOfficer()) {
            allowed = true;
        } else if (path.contains("/registrar/") && user.isRegistrar()) {
            allowed = true;
        } else if (path.contains("/admin/") && user.isAdmin()) {
            allowed = true;
        }

        if (!allowed) {
            session.invalidate();
            res.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
