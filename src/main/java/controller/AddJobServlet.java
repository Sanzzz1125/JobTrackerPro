package controller;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.JobDAO;
import model.Job;
import util.Auth;

public class AddJobServlet extends HttpServlet {

    /** GET — show the add job form */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireLogin(req, res)) return;

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        out.print(Auth.pageHead("Add Job", req));
        out.print(Auth.sidebar(req));
        out.print("<div class='main'>");
        out.print("<h1 style='font-size:26px;font-weight:700;'>Add Job Application</h1>");

        String err = req.getParameter("error");
        if (err != null)
            out.print("<div class='alert alert-error' style='margin-top:16px;'>" + Auth.esc(err) + "</div>");

        out.print("<div class='card' style='max-width:520px;'>");
        out.print("<form action='addJob' method='post'>");

        formField(out, "Company *",    "text",     "company",      "e.g. Google");
        formField(out, "Role *",       "text",     "role",         "e.g. Software Engineer");

        out.print("<div class='form-group'>");
        out.print("<label>Status</label>");
        out.print("<select name='status'>");
        for (String s : new String[]{"Applied","Interview","Offer","Rejected"})
            out.printf("<option>%s</option>", s);
        out.print("</select></div>");

        formField(out, "Applied Date", "date",     "date",         "");
        formField(out, "Notes",        "text",     "notes",        "Any notes...");

        out.print("<div style='margin-top:20px;'>");
        out.print("<button type='submit' class='btn btn-dark btn-full'>Save Job</button>");
        out.print("</div></form></div></div></body></html>");
    }

    /** POST — save the job */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireLogin(req, res)) return;

        String ctx = req.getContextPath();
        String company = req.getParameter("company");
        String role    = req.getParameter("role");

        if (blank(company) || blank(role)) {
            res.sendRedirect(ctx + "/addJob?error=Company+and+Role+are+required");
            return;
        }

        Job job = new Job();
        job.setUserId(Auth.getUserId(req));
        job.setCompany(company.trim());
        job.setRole(role.trim());
        job.setStatus(req.getParameter("status"));
        job.setNotes(req.getParameter("notes"));
        job.setAppliedDate(req.getParameter("date"));

        try {
            new JobDAO().addJob(job);
            res.sendRedirect(ctx + "/viewJobs?success=Job+added+successfully");
        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/addJob?error=Server+error:+" + Auth.esc(e.getMessage()));
        }
    }

    private void formField(PrintWriter out, String label, String type, String name, String placeholder) {
        out.printf("<div class='form-group'><label>%s</label>" +
                   "<input type='%s' name='%s' placeholder='%s'></div>",
                   label, type, name, placeholder);
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }
}
