package controller;

import java.io.*;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.UserDAO;
import dao.JobDAO;
import model.User;
import model.Job;
import util.Auth;

public class AdminServlet extends HttpServlet {

    // ── GET: show admin panel ─────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireAdmin(req, res)) return;

        String action = req.getParameter("action");
        if ("editUser".equals(action)) { showEditUserForm(req, res); return; }

        String ctx = req.getContextPath();
        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.print(Auth.pageHead("Admin Panel", req));
        out.print(Auth.sidebar(req));
        out.print("<div class='main'>");
        out.print("<h1 style='font-size:26px;font-weight:700;'>⚙ Admin Panel</h1>");
        out.print("<p style='color:#6b7280;margin-top:6px;'>Manage users and all job applications.</p>");

        // Banners
        String err = req.getParameter("error");
        String ok  = req.getParameter("success");
        if (err != null) out.print("<div class='alert alert-error' style='margin-top:16px;'>"   + Auth.esc(err) + "</div>");
        if (ok  != null) out.print("<div class='alert alert-success' style='margin-top:16px;'>" + Auth.esc(ok)  + "</div>");

        try {
            UserDAO uDao  = new UserDAO();
            JobDAO  jDao  = new JobDAO();
            List<User> users = uDao.getAllUsers();
            List<Job>  jobs  = jDao.getAllJobs();

            // Stats
            out.print("<div class='stat-row'>");
            out.printf("<div class='stat-tile' style='background:#e0f2fe;'><p style='color:#0369a1;'>Total Users</p><h2>%d</h2></div>", users.size());
            out.printf("<div class='stat-tile' style='background:#dcfce7;'><p style='color:#15803d;'>Total Jobs</p><h2>%d</h2></div>",  jobs.size());
            out.print("</div>");

            // ── Add User form ─────────────────────────────────
            out.print("<div class='card' style='margin-top:28px;max-width:620px;'>");
            out.print("<h3 style='margin-bottom:16px;'>Add New User</h3>");
            out.printf("<form action='%s/adminAction' method='post'>", ctx);
            out.print("<input type='hidden' name='action' value='addUser'>");
            out.print("<div style='display:grid;grid-template-columns:1fr 1fr;gap:12px;'>");
            out.print("<div class='form-group'><label>Full Name</label><input name='name' placeholder='Full name' required></div>");
            out.print("<div class='form-group'><label>Email</label><input type='email' name='email' placeholder='Email' required></div>");
            out.print("<div class='form-group'><label>Password</label><input type='password' name='password' placeholder='Password' required></div>");
            out.print("<div class='form-group'><label>Role</label><select name='role'><option value='user'>User</option><option value='admin'>Admin</option></select></div>");
            out.print("</div>");
            out.print("<button type='submit' class='btn btn-dark btn-full' style='margin-top:6px;'>Add User</button>");
            out.print("</form></div>");

            // ── Users table ───────────────────────────────────
            out.print("<div class='card' style='margin-top:24px;'>");
            out.print("<h3 style='margin-bottom:4px;'>All Users</h3>");
            out.print("<table class='data-table'><thead><tr>");
            out.print("<th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Actions</th>");
            out.print("</tr></thead><tbody>");

            for (User u : users) {
                out.print("<tr>");
                out.printf("<td>%d</td>", u.getId());
                out.printf("<td>%s</td>", Auth.esc(u.getName()));
                out.printf("<td style='color:#6b7280;'>%s</td>", Auth.esc(u.getEmail()));
                out.printf("<td><span class='badge %s'>%s</span></td>",
                           "admin".equals(u.getRole()) ? "badge-admin" : "badge-user",
                           Auth.esc(u.getRole()));
                out.print("<td style='display:flex;gap:8px;'>");
                out.printf("<a href='%s/admin?action=editUser&id=%d' class='btn btn-warn' style='font-size:12px;padding:5px 11px;'>Edit</a>", ctx, u.getId());
                out.printf("<form action='%s/adminAction' method='post' style='display:inline;'>", ctx);
                out.print("<input type='hidden' name='action' value='deleteUser'>");
                out.printf("<input type='hidden' name='userId' value='%d'>", u.getId());
                out.print("<button type='submit' class='btn btn-danger' style='font-size:12px;padding:5px 11px;' " +
                          "onclick='return confirm(\"Delete this user and all their jobs?\")'>Delete</button>");
                out.print("</form></td></tr>");
            }
            out.print("</tbody></table></div>");

            // ── All Jobs table ────────────────────────────────
            out.print("<div class='card' style='margin-top:24px;'>");
            out.print("<h3 style='margin-bottom:4px;'>All Job Applications</h3>");
            out.print("<table class='data-table'><thead><tr>");
            out.print("<th>ID</th><th>Role</th><th>Company</th><th>Status</th><th>User ID</th><th>Date</th><th>Actions</th>");
            out.print("</tr></thead><tbody>");

            for (Job j : jobs) {
                out.print("<tr>");
                out.printf("<td>%d</td>", j.getId());
                out.printf("<td>%s</td>", Auth.esc(j.getRole()));
                out.printf("<td>%s</td>", Auth.esc(j.getCompany()));
                out.printf("<td><span class='badge %s'>%s</span></td>",
                           Auth.badgeFor(j.getStatus()), Auth.esc(j.getStatus()));
                out.printf("<td style='color:#6b7280;'>%d</td>", j.getUserId());
                out.printf("<td style='color:#6b7280;font-size:13px;'>%s</td>",
                           j.getAppliedDate().isEmpty() ? "–" : j.getAppliedDate());
                out.print("<td style='display:flex;gap:6px;'>");
                out.printf("<a href='%s/editJob?id=%d' class='btn btn-warn' style='font-size:12px;padding:5px 11px;'>Edit</a>", ctx, j.getId());
                out.printf("<a href='%s/deleteJob?id=%d' class='btn btn-danger' style='font-size:12px;padding:5px 11px;' " +
                           "onclick='return confirm(\"Delete this job?\")'>Delete</a>", ctx, j.getId());
                out.print("</td></tr>");
            }
            out.print("</tbody></table></div></div></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("<div class='alert alert-error'>Error: " + Auth.esc(e.getMessage()) + "</div></div></body></html>");
        }
    }

    // ── Edit user form ────────────────────────────────────────
    private void showEditUserForm(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String ctx = req.getContextPath();
        int uid;
        try { uid = Integer.parseInt(req.getParameter("id")); }
        catch (Exception e) { res.sendRedirect(ctx + "/admin"); return; }

        try {
            User u = new UserDAO().getUserById(uid);
            if (u == null) { res.sendRedirect(ctx + "/admin"); return; }

            res.setContentType("text/html;charset=UTF-8");
            PrintWriter out = res.getWriter();
            out.print(Auth.pageHead("Edit User", req));
            out.print(Auth.sidebar(req));
            out.print("<div class='main'>");
            out.print("<h1 style='font-size:26px;font-weight:700;'>Edit User</h1>");
            out.print("<div class='card' style='max-width:480px;'>");
            out.printf("<form action='%s/adminAction' method='post'>", ctx);
            out.print("<input type='hidden' name='action' value='updateUser'>");
            out.printf("<input type='hidden' name='userId' value='%d'>", u.getId());

            out.print("<div class='form-group'><label>Full Name</label>");
            out.printf("<input name='name' value='%s' required></div>", Auth.esc(u.getName()));

            out.print("<div class='form-group'><label>Email</label>");
            out.printf("<input type='email' name='email' value='%s' required></div>", Auth.esc(u.getEmail()));

            out.print("<div class='form-group'><label>Role</label><select name='role'>");
            out.printf("<option value='user'%s>User</option>",  "user".equals(u.getRole())  ? " selected" : "");
            out.printf("<option value='admin'%s>Admin</option>","admin".equals(u.getRole()) ? " selected" : "");
            out.print("</select></div>");

            out.print("<div style='display:flex;gap:10px;margin-top:16px;'>");
            out.print("<button type='submit' class='btn btn-dark'>Save Changes</button>");
            out.printf("<a href='%s/admin' class='btn btn-light'>Cancel</a>", ctx);
            out.print("</div></form></div></div></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/admin?error=Error+loading+user");
        }
    }
}
