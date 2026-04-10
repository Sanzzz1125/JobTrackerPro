package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.UserDAO;
import model.User;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.sendRedirect(req.getContextPath() + "/register.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String name    = req.getParameter("name");
        String email   = req.getParameter("email");
        String pass    = req.getParameter("password");
        String confirm = req.getParameter("confirm_password");
        String ctx     = req.getContextPath();

        // Validation
        if (blank(name) || blank(email) || blank(pass) || blank(confirm)) {
            res.sendRedirect(ctx + "/register.html?error=All+fields+are+required");
            return;
        }
        if (!pass.equals(confirm)) {
            res.sendRedirect(ctx + "/register.html?error=Passwords+do+not+match");
            return;
        }
        if (pass.length() < 6) {
            res.sendRedirect(ctx + "/register.html?error=Password+must+be+at+least+6+characters");
            return;
        }

        try {
            User u = new User(0, name.trim(), email.trim(), pass, "user");
            boolean ok = new UserDAO().register(u);
            if (ok) {
                res.sendRedirect(ctx + "/login.html?success=Account+created!+Please+log+in.");
            } else {
                res.sendRedirect(ctx + "/register.html?error=This+email+is+already+registered");
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/register.html?error=Server+error,+please+try+again");
        }
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }
}
