package controller;

import java.io.*;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.JobDAO;
import model.Job;
import util.Auth;

public class ViewJobsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireLogin(req, res)) return;

        int     uid   = Auth.getUserId(req);
        boolean admin = Auth.isAdmin(req);
        String  ctx   = req.getContextPath();

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.print(Auth.pageHead("My Jobs", req));
        out.print(Auth.sidebar(req));
        out.print("<div class='main'>");
        out.print("<div style='display:flex;justify-content:space-between;align-items:center;'>");
        out.print("<h1 style='font-size:26px;font-weight:700;'>Job Applications</h1>");
        out.printf("<a href='%s/addJob' class='btn btn-dark'>+ Add Job</a>", ctx);
        out.print("</div>");

        // Error/success banner
        String err = req.getParameter("error");
        String ok  = req.getParameter("success");
        if (err != null) out.print("<div class='alert alert-error' style='margin-top:16px;'>" + Auth.esc(err) + "</div>");
        if (ok  != null) out.print("<div class='alert alert-success' style='margin-top:16px;'>" + Auth.esc(ok) + "</div>");

        try {
            JobDAO    dao  = new JobDAO();
            List<Job> jobs = admin ? dao.getAllJobs() : dao.getJobsByUser(uid);

            out.print("<div class='card'>");

            if (jobs.isEmpty()) {
                out.print("<p style='color:#6b7280;text-align:center;padding:40px;'>" +
                          "No jobs yet. <a href='" + ctx + "/addJob'>Add your first one!</a></p>");
            } else {
                out.print("<table class='data-table'><thead><tr>");
                out.print("<th>Role</th><th>Company</th><th>Date</th><th>Status</th><th>Actions</th>");
                out.print("</tr></thead><tbody>");

                for (Job j : jobs) {
                    out.print("<tr>");
                    out.printf("<td>%s</td>", Auth.esc(j.getRole()));
                    out.printf("<td>%s</td>", Auth.esc(j.getCompany()));
                    out.printf("<td style='color:#6b7280;font-size:13px;'>%s</td>",
                               j.getAppliedDate().isEmpty() ? "–" : j.getAppliedDate());
                    out.printf("<td><span class='badge %s'>%s</span></td>",
                               Auth.badgeFor(j.getStatus()), Auth.esc(j.getStatus()));
                    out.print("<td>");
                    out.printf("<a href='%s/editJob?id=%d' class='btn btn-warn' style='margin-right:6px;font-size:12px;padding:6px 12px;'>Edit</a>",
                               ctx, j.getId());
                    out.printf("<a href='%s/deleteJob?id=%d' class='btn btn-danger' style='font-size:12px;padding:6px 12px;' " +
                               "onclick='return confirm(\"Delete this job?\")'>Delete</a>",
                               ctx, j.getId());
                    out.print("</td></tr>");
                }
                out.print("</tbody></table>");
            }

            out.print("</div></div></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("<div class='alert alert-error'>Error: " + Auth.esc(e.getMessage()) + "</div></div></body></html>");
        }
    }
}
