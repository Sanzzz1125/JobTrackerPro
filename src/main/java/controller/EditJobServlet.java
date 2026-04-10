package controller;

import java.io.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.JobDAO;
import model.Job;
import util.Auth;

public class EditJobServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireLogin(req, res)) return;

        String ctx = req.getContextPath();
        int jobId;
        try { jobId = Integer.parseInt(req.getParameter("id")); }
        catch (Exception e) { res.sendRedirect(ctx + "/viewJobs"); return; }

        try {
            Job j = new JobDAO().getJobById(jobId);

            // Security: user can only edit their own job (admin can edit any)
            if (j == null || (!Auth.isAdmin(req) && j.getUserId() != Auth.getUserId(req))) {
                res.sendRedirect(ctx + "/viewJobs?error=Job+not+found");
                return;
            }

            res.setContentType("text/html;charset=UTF-8");
            PrintWriter out = res.getWriter();
            out.print(Auth.pageHead("Edit Job", req));
            out.print(Auth.sidebar(req));
            out.print("<div class='main'>");
            out.print("<h1 style='font-size:26px;font-weight:700;'>Edit Job</h1>");
            out.print("<div class='card' style='max-width:520px;'>");
            out.printf("<form action='%s/updateJob' method='post'>", ctx);
            out.printf("<input type='hidden' name='id' value='%d'>", j.getId());

            field(out, "Company *",    "text", "company",  Auth.esc(j.getCompany()));
            field(out, "Role *",       "text", "role",     Auth.esc(j.getRole()));

            out.print("<div class='form-group'><label>Status</label><select name='status'>");
            for (String s : new String[]{"Applied","Interview","Offer","Rejected"}) {
                String sel = s.equals(j.getStatus()) ? " selected" : "";
                out.printf("<option%s>%s</option>", sel, s);
            }
            out.print("</select></div>");

            field(out, "Applied Date", "date", "date",  j.getAppliedDate());
            field(out, "Notes",        "text", "notes", Auth.esc(j.getNotes()));

            out.print("<div style='margin-top:20px;display:flex;gap:10px;'>");
            out.print("<button type='submit' class='btn btn-dark'>Update Job</button>");
            out.printf("<a href='%s/viewJobs' class='btn btn-light'>Cancel</a>", ctx);
            out.print("</div></form></div></div></body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/viewJobs?error=Error+loading+job");
        }
    }

    private void field(PrintWriter out, String label, String type, String name, String value) {
        out.printf("<div class='form-group'><label>%s</label>" +
                   "<input type='%s' name='%s' value='%s'></div>",
                   label, type, name, value == null ? "" : value);
    }
}
