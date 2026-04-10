package dao;

import java.sql.*;
import java.util.*;
import model.Job;
import util.DBConnection;

public class JobDAO {

    private Job map(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setUserId(rs.getInt("user_id"));
        j.setCompany(rs.getString("company"));
        j.setRole(rs.getString("role"));
        j.setStatus(rs.getString("status"));
        j.setNotes(rs.getString("notes"));
        Object d = rs.getObject("applied_date");
        j.setAppliedDate(d != null ? d.toString() : "");
        return j;
    }

    public void addJob(Job job) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO jobs (user_id, company, role, status, notes, applied_date) " +
                "VALUES (?,?,?,?,?,?)");
            ps.setInt(1, job.getUserId());
            ps.setString(2, job.getCompany());
            ps.setString(3, job.getRole());
            ps.setString(4, job.getStatus());
            ps.setString(5, job.getNotes());
            String d = job.getAppliedDate();
            ps.setDate(6, (d != null && !d.isEmpty()) ? java.sql.Date.valueOf(d) : null);
            ps.executeUpdate();
        } finally { DBConnection.close(con); }
    }

    /** All jobs for a specific user */
    public List<Job> getJobsByUser(int userId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM jobs WHERE user_id = ? ORDER BY id DESC");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            List<Job> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } finally { DBConnection.close(con); }
    }

    /** All jobs — admin only */
    public List<Job> getAllJobs() throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            ResultSet rs = con.createStatement()
                .executeQuery("SELECT * FROM jobs ORDER BY id DESC");
            List<Job> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } finally { DBConnection.close(con); }
    }

    public Job getJobById(int id) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM jobs WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } finally { DBConnection.close(con); }
    }

    public void updateJob(Job job) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE jobs SET company=?, role=?, status=?, notes=?, applied_date=? WHERE id=?");
            ps.setString(1, job.getCompany());
            ps.setString(2, job.getRole());
            ps.setString(3, job.getStatus());
            ps.setString(4, job.getNotes());
            String d = job.getAppliedDate();
            ps.setDate(5, (d != null && !d.isEmpty()) ? java.sql.Date.valueOf(d) : null);
            ps.setInt(6, job.getId());
            ps.executeUpdate();
        } finally { DBConnection.close(con); }
    }

    public void deleteJob(int id) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM jobs WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally { DBConnection.close(con); }
    }

    public int countByStatus(String status, int userId) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM jobs WHERE status=? AND user_id=?");
            ps.setString(1, status);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally { DBConnection.close(con); }
    }

    public int countAll() throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            ResultSet rs = con.createStatement()
                .executeQuery("SELECT COUNT(*) FROM jobs");
            return rs.next() ? rs.getInt(1) : 0;
        } finally { DBConnection.close(con); }
    }
}
