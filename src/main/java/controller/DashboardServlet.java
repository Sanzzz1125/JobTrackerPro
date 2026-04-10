package controller;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.JobDAO;
import util.Auth;

public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireLogin(req, res)) return;

        int    uid  = Auth.getUserId(req);
        String name = Auth.getUserName(req);

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.print(Auth.pageHead("Dashboard", req));
        out.print(Auth.sidebar(req));
        out.print("<div class='main'>");
        out.printf("<h1 style='font-size:26px;font-weight:700;'>Welcome back, %s 👋</h1>%n",
                   Auth.esc(name));
        out.print("<p style='color:#6b7280;margin-top:6px;'>Here's a summary of your job applications.</p>");

        try {
            JobDAO dao = new JobDAO();
            int applied   = dao.countByStatus("Applied",   uid);
            int interview = dao.countByStatus("Interview", uid);
            int offer     = dao.countByStatus("Offer",     uid);
            int rejected  = dao.countByStatus("Rejected",  uid);
            int total     = applied + interview + offer + rejected;

            out.print("<div class='stat-row'>");
            statTile(out, "#e0f2fe", "#0369a1", "Total",     total);
            statTile(out, "#dbeafe", "#1d4ed8", "Applied",   applied);
            statTile(out, "#fef3c7", "#b45309", "Interview", interview);
            statTile(out, "#dcfce7", "#15803d", "Offers",    offer);
            statTile(out, "#fee2e2", "#b91c1c", "Rejected",  rejected);
            out.print("</div>");

            // Quick actions card
            out.print("<div class='card' style='margin-top:28px;'>");
            out.print("<h3 style='margin-bottom:14px;'>Quick Actions</h3>");
            out.print("<div style='display:flex;gap:12px;flex-wrap:wrap;'>");
            out.printf("<a href='%s/addJob' class='btn btn-dark'>+ Add Job</a>", req.getContextPath());
            out.printf("<a href='%s/viewJobs' class='btn btn-light'>View All Jobs</a>", req.getContextPath());
            out.print("</div></div>");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("<div class='alert alert-error'>Error loading dashboard: " + Auth.esc(e.getMessage()) + "</div>");
        }

        out.print("</div></body></html>");
    }

    private void statTile(PrintWriter out, String bg, String color, String label, int val) {
        out.printf("<div class='stat-tile' style='background:%s;'>" +
                   "<p style='color:%s;'>%s</p><h2>%d</h2></div>",
                   bg, color, label, val);
    }
}
