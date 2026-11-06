package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistrarDAO {

    public List<String[]> getRegistrarQueue() {
        String sql = "SELECT r.request_id, s.student_number, u.full_name, "
                   + "p.program_code, r.submitted_at "
                   + "FROM clearance_requests r "
                   + "JOIN students s ON s.student_id = r.student_id "
                   + "JOIN users u ON u.user_id = s.user_id "
                   + "JOIN programs p ON p.program_id = s.program_id "
                   + "WHERE r.overall_status = 'AWAITING_REGISTRAR' "
                   + "ORDER BY r.submitted_at";
        List<String[]> queue = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                queue.add(new String[]{rs.getString("request_id"),
                        rs.getString("student_number"), rs.getString("full_name"),
                        rs.getString("program_code"), rs.getString("submitted_at")});
            }
        } catch (SQLException e) {
            System.err.println("[RegistrarDAO] getRegistrarQueue error: " + e.getMessage());
        }
        return queue;
    }

    public List<String[]> getRequestItems(int requestId) {
        String sql = "SELECT i.item_id, o.office_code, o.office_name, i.status, i.remarks "
                   + "FROM clearance_items i "
                   + "JOIN clearance_requirements req ON req.requirement_id = i.requirement_id "
                   + "JOIN offices o ON o.office_id = req.office_id "
                   + "WHERE i.request_id = ? ORDER BY o.office_code";
        List<String[]> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new String[]{rs.getString("item_id"),
                            rs.getString("office_code"), rs.getString("office_name"),
                            rs.getString("status"), rs.getString("remarks")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[RegistrarDAO] getRequestItems error: " + e.getMessage());
        }
        return items;
    }

    public boolean finalApprove(int requestId, int userId) {
        try (Connection conn = DBConnection.getConnection()) {

            int studentUserId = 0;
            String reqInfoSql = "SELECT s.user_id FROM clearance_requests r "
                              + "JOIN students s ON s.student_id = r.student_id "
                              + "WHERE r.request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(reqInfoSql)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        studentUserId = rs.getInt("user_id");
                    }
                }
            }
            if (studentUserId == 0) {
                return false;
            }

            String updateSql = "UPDATE clearance_requests "
                             + "SET overall_status = 'CLEARED', completed_at = NOW(), "
                             + "final_checked_by = ?, final_checked_at = NOW() "
                             + "WHERE request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, requestId);
                ps.executeUpdate();
            }

            String notifSql = "INSERT INTO notifications (user_id, title, message) "
                            + "VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(notifSql)) {
                ps.setInt(1, studentUserId);
                ps.setString(2, "Clearance Approved");
                ps.setString(3, "Congratulations! Ang iyong clearance ay na-approve na ng registrar.");
                ps.executeUpdate();
            }

            String auditSql = "INSERT INTO audit_logs "
                            + "(actor_user_id, action, entity_type, entity_id, details) "
                            + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, userId);
                ps.setString(2, "FINAL_APPROVE");
                ps.setString(3, "CLEARANCE_REQUEST");
                ps.setInt(4, requestId);
                ps.setString(5, "Final approval ng registrar");
                ps.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("[RegistrarDAO] finalApprove error: " + e.getMessage());
            return false;
        }
    }

    public boolean returnForAction(int requestId, int userId, String reason) {
        try (Connection conn = DBConnection.getConnection()) {

            int studentUserId = 0;
            String reqInfoSql = "SELECT s.user_id FROM clearance_requests r "
                              + "JOIN students s ON s.student_id = r.student_id "
                              + "WHERE r.request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(reqInfoSql)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        studentUserId = rs.getInt("user_id");
                    }
                }
            }
            if (studentUserId == 0) {
                return false;
            }

            String updateSql = "UPDATE clearance_requests SET overall_status = 'IN_PROGRESS' "
                             + "WHERE request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, requestId);
                ps.executeUpdate();
            }

            String notifSql = "INSERT INTO notifications (user_id, title, message) "
                            + "VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(notifSql)) {
                ps.setInt(1, studentUserId);
                ps.setString(2, "Clearance Returned");
                ps.setString(3, "Ang iyong clearance ay ibinalik para sa aksyon. Dahilan: " + reason);
                ps.executeUpdate();
            }

            String auditSql = "INSERT INTO audit_logs "
                            + "(actor_user_id, action, entity_type, entity_id, details) "
                            + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, userId);
                ps.setString(2, "RETURN_FOR_ACTION");
                ps.setString(3, "CLEARANCE_REQUEST");
                ps.setInt(4, requestId);
                ps.setString(5, "Return for action ng registrar. Dahilan: " + reason);
                ps.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("[RegistrarDAO] returnForAction error: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> listCompleted() {
        String sql = "SELECT r.request_id, s.student_number, u.full_name, "
                   + "p.program_code, r.completed_at "
                   + "FROM clearance_requests r "
                   + "JOIN students s ON s.student_id = r.student_id "
                   + "JOIN users u ON u.user_id = s.user_id "
                   + "JOIN programs p ON p.program_id = s.program_id "
                   + "WHERE r.overall_status = 'CLEARED' "
                   + "ORDER BY r.completed_at DESC";
        List<String[]> completed = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                completed.add(new String[]{rs.getString("request_id"),
                        rs.getString("student_number"), rs.getString("full_name"),
                        rs.getString("program_code"), rs.getString("completed_at")});
            }
        } catch (SQLException e) {
            System.err.println("[RegistrarDAO] listCompleted error: " + e.getMessage());
        }
        return completed;
    }
}
