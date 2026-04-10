package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.JobDAO;
import model.Job;
import util.Auth;

public class UpdateJobServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireLogin(req, res)) return;

        String ctx = req.getContextPath();
        int jobId;
        try { jobId = Integer.parseInt(req.getParameter("id")); }
        catch (Exception e) { res.sendRedirect(ctx + "/viewJobs"); return; }

        try {
            JobDAO dao      = new JobDAO();
            Job    existing = dao.getJobById(jobId);

            if (existing == null || (!Auth.isAdmin(req) && existing.getUserId() != Auth.getUserId(req))) {
                res.sendRedirect(ctx + "/viewJobs?error=Unauthorized");
                return;
            }

            existing.setCompany(req.getParameter("company"));
            existing.setRole(req.getParameter("role"));
            existing.setStatus(req.getParameter("status"));
            existing.setNotes(req.getParameter("notes"));
            existing.setAppliedDate(req.getParameter("date"));
            dao.updateJob(existing);

            res.sendRedirect(ctx + "/viewJobs?success=Job+updated+successfully");

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/viewJobs?error=Error+updating+job");
        }
    }
}
