package dao;

import java.sql.*;
import java.util.*;
import model.User;
import util.DBConnection;

public class UserDAO {

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        return u;
    }

    /** Returns the User if credentials match, null otherwise */
    public User login(String email, String password) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE email = ? AND password = ?");
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } finally { DBConnection.close(con); }
    }

    /** Returns true if registered, false if email already exists */
    public boolean register(User user) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement check = con.prepareStatement(
                "SELECT id FROM users WHERE email = ?");
            check.setString(1, user.getEmail());
            if (check.executeQuery().next()) return false; // duplicate email

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users (username, email, password, role) VALUES (?,?,?,?)");
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole() != null ? user.getRole() : "user");
            ps.executeUpdate();
            return true;
        } finally { DBConnection.close(con); }
    }

    public List<User> getAllUsers() throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            ResultSet rs = con.createStatement()
                .executeQuery("SELECT * FROM users ORDER BY id DESC");
            List<User> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        } finally { DBConnection.close(con); }
    }

    public User getUserById(int id) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? map(rs) : null;
        } finally { DBConnection.close(con); }
    }

    public void updateUser(int id, String name, String email, String role) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET username=?, email=?, role=? WHERE id=?");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, role);
            ps.setInt(4, id);
            ps.executeUpdate();
        } finally { DBConnection.close(con); }
    }

    public void deleteUser(int id) throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally { DBConnection.close(con); }
    }

    public int countUsers() throws Exception {
        Connection con = DBConnection.getConnection();
        try {
            ResultSet rs = con.createStatement()
                .executeQuery("SELECT COUNT(*) FROM users");
            return rs.next() ? rs.getInt(1) : 0;
        } finally { DBConnection.close(con); }
    }
}
