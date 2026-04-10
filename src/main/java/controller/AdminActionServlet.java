package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.UserDAO;
import model.User;
import util.Auth;

/** Handles all admin POST mutations: addUser, updateUser, deleteUser */
public class AdminActionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireAdmin(req, res)) return;

        String action = req.getParameter("action");
        String ctx    = req.getContextPath();

        try {
            UserDAO dao = new UserDAO();

            switch (action == null ? "" : action) {

                case "addUser": {
                    String name  = req.getParameter("name");
                    String email = req.getParameter("email");
                    String pass  = req.getParameter("password");
                    String role  = req.getParameter("role");
                    if (blank(name) || blank(email) || blank(pass)) {
                        res.sendRedirect(ctx + "/admin?error=All+fields+required+for+adding+user");
                        return;
                    }
                    User u = new User(0, name.trim(), email.trim(), pass, role);
                    if (dao.register(u)) {
                        res.sendRedirect(ctx + "/admin?success=User+added+successfully");
                    } else {
                        res.sendRedirect(ctx + "/admin?error=Email+already+exists");
                    }
                    break;
                }

                case "updateUser": {
                    int    uid   = Integer.parseInt(req.getParameter("userId"));
                    String name  = req.getParameter("name");
                    String email = req.getParameter("email");
                    String role  = req.getParameter("role");
                    dao.updateUser(uid, name, email, role);
                    res.sendRedirect(ctx + "/admin?success=User+updated+successfully");
                    break;
                }

                case "deleteUser": {
                    int uid = Integer.parseInt(req.getParameter("userId"));
                    dao.deleteUser(uid);
                    res.sendRedirect(ctx + "/admin?success=User+deleted+successfully");
                    break;
                }

                default:
                    res.sendRedirect(ctx + "/admin");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/admin?error=Server+error:+" + Auth.esc(e.getMessage()));
        }
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }
}
