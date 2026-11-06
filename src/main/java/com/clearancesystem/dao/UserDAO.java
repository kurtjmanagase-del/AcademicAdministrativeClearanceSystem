package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import com.clearancesystem.model.User;
import com.clearancesystem.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByUsername(String username) {
        String sql = "SELECT u.user_id, u.role_id, u.username, u.password_hash, "
                   + "u.full_name, u.email, u.account_status, "
                   + "r.role_code, "
                   + "s.student_id, s.student_number, s.year_level, p.program_code, s.program_id "
                   + "FROM users u "
                   + "JOIN roles r ON r.role_id = u.role_id "
                   + "LEFT JOIN students s ON s.user_id = u.user_id "
                   + "LEFT JOIN programs p ON p.program_id = s.program_id "
                   + "WHERE u.username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapRow(rs);
                    u.setProgramId((Integer) rs.getObject("program_id"));
                    return u;
                }
            }
            return null;

        } catch (SQLException e) {

            System.err.println("[UserDAO] findByUsername error: " + e.getMessage());
            return null;
        }
    }

    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            return null;
        }
        if (!user.isActive()) {
            return null;
        }
        return user;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setRoleId(rs.getInt("role_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setAccountStatus(rs.getString("account_status"));
        u.setRoleCode(rs.getString("role_code"));

        u.setStudentId((Integer) rs.getObject("student_id"));
        u.setStudentNumber(rs.getString("student_number"));
        u.setYearLevel((Integer) rs.getObject("year_level"));
        u.setProgramCode(rs.getString("program_code"));

        return u;
    }

    public List<User> listAll() {
        String sql = "SELECT u.user_id, u.role_id, u.username, u.full_name, u.email, u.account_status, "
                   + "r.role_code, "
                   + "s.student_number, s.year_level, p.program_code, "
                   + "(SELECT GROUP_CONCAT(o.office_code SEPARATOR ', ') "
                   + "   FROM user_offices uo JOIN offices o ON o.office_id = uo.office_id "
                   + "   WHERE uo.user_id = u.user_id) AS offices "
                   + "FROM users u "
                   + "JOIN roles r ON r.role_id = u.role_id "
                   + "LEFT JOIN students s ON s.user_id = u.user_id "
                   + "LEFT JOIN programs p ON p.program_id = s.program_id "
                   + "ORDER BY u.user_id";

        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setRoleId(rs.getInt("role_id"));
                u.setUsername(rs.getString("username"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setAccountStatus(rs.getString("account_status"));
                u.setRoleCode(rs.getString("role_code"));
                u.setStudentNumber(rs.getString("student_number"));
                u.setYearLevel((Integer) rs.getObject("year_level"));
                u.setProgramCode(rs.getString("program_code"));
                u.setOfficeCodes(rs.getString("offices"));
                users.add(u);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] listAll error: " + e.getMessage());
        }
        return users;
    }

    public User findById(int userId) {
        String sql = "SELECT u.user_id, u.role_id, u.username, u.password_hash, "
                   + "u.full_name, u.email, u.account_status, "
                   + "r.role_code, "
                   + "s.student_id, s.student_number, s.year_level, p.program_code, "
                   + "s.program_id "
                   + "FROM users u "
                   + "JOIN roles r ON r.role_id = u.role_id "
                   + "LEFT JOIN students s ON s.user_id = u.user_id "
                   + "LEFT JOIN programs p ON p.program_id = s.program_id "
                   + "WHERE u.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = mapRow(rs);

                    u.setProgramId((Integer) rs.getObject("program_id"));
                    return u;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("[UserDAO] findById error: " + e.getMessage());
            return null;
        }
    }

    public List<String[]> listRoles() {
        String sql = "SELECT role_id, role_code, role_name FROM roles ORDER BY role_id";
        List<String[]> roles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.add(new String[]{rs.getString("role_id"),
                        rs.getString("role_code"), rs.getString("role_name")});
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] listRoles error: " + e.getMessage());
        }
        return roles;
    }

    public List<String[]> listPrograms() {
        String sql = "SELECT p.program_id, p.program_code, p.program_name "
                   + "FROM programs p ORDER BY p.program_code";
        List<String[]> programs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                programs.add(new String[]{rs.getString("program_id"),
                        rs.getString("program_code"), rs.getString("program_name")});
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] listPrograms error: " + e.getMessage());
        }
        return programs;
    }

    public List<String[]> listOffices() {
        String sql = "SELECT office_id, office_code, office_name FROM offices "
                   + "WHERE is_active = 1 ORDER BY office_code";
        List<String[]> offices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                offices.add(new String[]{rs.getString("office_id"),
                        rs.getString("office_code"), rs.getString("office_name")});
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] listOffices error: " + e.getMessage());
        }
        return offices;
    }

    public int createUser(String username, String passwordHash, String fullName,
                          String email, int roleId) {
        String sql = "INSERT INTO users (role_id, username, password_hash, full_name, email, account_status) "
                   + "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, roleId);
            ps.setString(2, username);
            ps.setString(3, passwordHash);
            ps.setString(4, fullName);
            ps.setString(5, email);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("[UserDAO] createUser error: " + e.getMessage());
            return -1;
        }
    }

    public boolean createStudent(int userId, String studentNumber, int programId, int yearLevel) {
        String sql = "INSERT INTO students (user_id, student_number, program_id, year_level, enrollment_status) "
                   + "VALUES (?, ?, ?, ?, 'ENROLLED')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, studentNumber);
            ps.setInt(3, programId);
            ps.setInt(4, yearLevel);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[UserDAO] createStudent error: " + e.getMessage());
            return false;
        }
    }

    public boolean assignOffice(int userId, int officeId) {
        String sql = "INSERT INTO user_offices (user_id, office_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, officeId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[UserDAO] assignOffice error: " + e.getMessage());
            return false;
        }
    }

    public boolean setAccountStatus(int userId, String status) {
        String sql = "UPDATE users SET account_status = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[UserDAO] setAccountStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean resetPassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[UserDAO] resetPassword error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProfile(int userId, String fullName, String email) {
        String sql = "UPDATE users SET full_name = ?, email = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, userId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[UserDAO] updateProfile error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStudent(int userId, String studentNumber, int programId, int yearLevel) {
        String sql = "UPDATE students SET student_number = ?, program_id = ?, year_level = ? "
                   + "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentNumber);
            ps.setInt(2, programId);
            ps.setInt(3, yearLevel);
            ps.setInt(4, userId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("[UserDAO] updateStudent error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOfficeAssignment(int userId, int officeId) {
        String deleteSql = "DELETE FROM user_offices WHERE user_id = ?";
        String insertSql = "INSERT INTO user_offices (user_id, office_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, officeId);
                ps.executeUpdate();
            }
            return true;

        } catch (SQLException e) {
            System.err.println("[UserDAO] updateOfficeAssignment error: " + e.getMessage());
            return false;
        }
    }
}
