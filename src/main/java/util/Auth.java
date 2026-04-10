package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Auth — central session helper.
 * userId is always stored as Integer in the session.
 */
public class Auth {

    // ── Session getters ───────────────────────────────────────

    public static boolean isLoggedIn(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("userId") != null;
    }

    public static boolean isAdmin(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && "admin".equals(s.getAttribute("userRole"));
    }

    public static int getUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return -1;
        Integer id = (Integer) s.getAttribute("userId");
        return id != null ? id : -1;
    }

    public static String getUserName(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return "";
        String n = (String) s.getAttribute("userName");
        return n != null ? n : "";
    }

    // ── Guards ───────────────────────────────────────────────

    /** Redirect to login if not logged in. Returns true = caller should stop. */
    public static boolean requireLogin(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (!isLoggedIn(req)) {
            res.sendRedirect(req.getContextPath() + "/login.html?error=Please+log+in+to+continue");
            return true;
        }
        return false;
    }

    /** Redirect to login if not admin. Returns true = caller should stop. */
    public static boolean requireAdmin(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (!isLoggedIn(req)) {
            res.sendRedirect(req.getContextPath() + "/login.html?error=Please+log+in+to+continue");
            return true;
        }
        if (!isAdmin(req)) {
            res.sendRedirect(req.getContextPath() + "/dashboard?error=Admin+access+only");
            return true;
        }
        return false;
    }

    // ── HTML helpers ─────────────────────────────────────────

    /** Full page head + open body tag */
    public static String pageHead(String title, HttpServletRequest req) {
        String ctx = req.getContextPath();
        return "<!DOCTYPE html><html lang='en'><head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
               "<title>" + esc(title) + " – JobTrackerPro</title>" +
               "<link rel='stylesheet' href='" + ctx + "/css/app.css'>" +
               "</head><body>";
    }

    /** Renders the sidebar — shows nav only for logged-in users */
    public static String sidebar(HttpServletRequest req) {
        String ctx      = req.getContextPath();
        boolean loggedIn = isLoggedIn(req);
        boolean admin    = isAdmin(req);
        String  name     = getUserName(req);

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='sidebar'>");
        sb.append("<h2><a href='").append(ctx).append("/index.html'>🚀 JobTrackerPro</a></h2>");

        if (loggedIn) {
            sb.append("<p class='user-greeting'>Hi, ").append(esc(name)).append(" 👋</p>");
            sb.append("<a href='").append(ctx).append("/dashboard'>Dashboard</a>");
            sb.append("<a href='").append(ctx).append("/viewJobs'>My Jobs</a>");
            sb.append("<a href='").append(ctx).append("/addJob'>Add Job</a>");
            if (admin) {
                sb.append("<hr>");
                sb.append("<a href='").append(ctx).append("/admin'>⚙ Admin Panel</a>");
            }
            sb.append("<hr>");
            sb.append("<a href='").append(ctx).append("/logout' style='color:#f87171;'>Logout</a>");
        } else {
            sb.append("<a href='").append(ctx).append("/login.html'>Login</a>");
            sb.append("<a href='").append(ctx).append("/register.html'>Register</a>");
        }

        sb.append("</div>");
        return sb.toString();
    }

    public static String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }

    public static String badgeFor(String status) {
        switch (status == null ? "" : status) {
            case "Interview": return "badge-interview";
            case "Offer":     return "badge-offer";
            case "Rejected":  return "badge-rejected";
            default:          return "badge-applied";
        }
    }
}
