package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.UserDAO;
import model.User;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Already logged in → skip the login page
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("userId") != null) {
            res.sendRedirect(req.getContextPath() +
                ("admin".equals(s.getAttribute("userRole")) ? "/admin" : "/dashboard"));
            return;
        }
        res.sendRedirect(req.getContextPath() + "/login.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String pass  = req.getParameter("password");
        String ctx   = req.getContextPath();

        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            res.sendRedirect(ctx + "/login.html?error=Email+and+password+are+required");
            return;
        }

        try {
            User user = new UserDAO().login(email.trim(), pass.trim());

            if (user == null) {
                res.sendRedirect(ctx + "/login.html?error=Invalid+email+or+password");
                return;
            }

            // Destroy old session, create fresh one
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = req.getSession(true);

            // ★ Store userId as Integer — never as String
            session.setAttribute("userId",   user.getId());      // int
            session.setAttribute("userName", user.getName());    // String
            session.setAttribute("userRole", user.getRole());    // String

            if ("admin".equals(user.getRole())) {
                res.sendRedirect(ctx + "/admin");
            } else {
                res.sendRedirect(ctx + "/dashboard");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/login.html?error=Server+error,+please+try+again");
        }
    }
}
