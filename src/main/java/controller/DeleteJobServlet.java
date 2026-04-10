package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import dao.JobDAO;
import model.Job;
import util.Auth;

public class DeleteJobServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        if (Auth.requireLogin(req, res)) return;

        String ctx = req.getContextPath();
        int jobId;
        try { jobId = Integer.parseInt(req.getParameter("id")); }
        catch (Exception e) { res.sendRedirect(ctx + "/viewJobs"); return; }

        try {
            JobDAO dao = new JobDAO();
            Job    job = dao.getJobById(jobId);

            if (job != null && (Auth.isAdmin(req) || job.getUserId() == Auth.getUserId(req))) {
                dao.deleteJob(jobId);
                res.sendRedirect(ctx + "/viewJobs?success=Job+deleted");
            } else {
                res.sendRedirect(ctx + "/viewJobs?error=Job+not+found+or+unauthorized");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.sendRedirect(ctx + "/viewJobs?error=Error+deleting+job");
        }
    }
}
