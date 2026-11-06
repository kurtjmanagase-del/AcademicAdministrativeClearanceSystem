package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficerDAO {

    public List<String[]> getAssignedOffices(int userId) {
        String sql = "SELECT o.office_id, o.office_code, o.office_name "
                   + "FROM user_offices uo "
                   + "JOIN offices o ON o.office_id = uo.office_id "
                   + "WHERE uo.user_id = ? ORDER BY o.office_code";
        List<String[]> offices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    offices.add(new String[]{rs.getString("office_id"),
                            rs.getString("office_code"), rs.getString("office_name")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[OfficerDAO] getAssignedOffices error: " + e.getMessage());
        }
        return offices;
    }

    public List<String[]> getQueue(int officeId, int cycleId) {
        String sql = "SELECT r.request_id, s.student_number, u.full_name, p.program_code, "
                   + "i.item_id, i.status, i.remarks "
                   + "FROM clearance_items i "
                   + "JOIN clearance_requests r ON r.request_id = i.request_id "
                   + "JOIN students s ON s.student_id = r.student_id "
                   + "JOIN users u ON u.user_id = s.user_id "
                   + "JOIN programs p ON p.program_id = s.program_id "
                   + "JOIN clearance_requirements req ON req.requirement_id = i.requirement_id "
                   + "WHERE req.office_id = ? AND (? = 0 OR r.cycle_id = ?) "
                   + "AND i.status IN ('PENDING','IN_REVIEW','ACTION_REQUIRED') "
                   + "ORDER BY s.student_number, i.item_id";
        List<String[]> queue = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, officeId);
            ps.setInt(2, cycleId);
            ps.setInt(3, cycleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    queue.add(new String[]{rs.getString("request_id"),
                            rs.getString("student_number"), rs.getString("full_name"),
                            rs.getString("program_code"), rs.getString("item_id"),
                            rs.getString("status"), rs.getString("remarks")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[OfficerDAO] getQueue error: " + e.getMessage());
        }
        return queue;
    }

    public Map<String, Object> getItemDetail(int itemId) {
        String sql = "SELECT i.item_id, i.status, i.remarks, i.reviewed_at, "
                   + "o.office_code, o.office_name, "
                   + "r.request_id, r.overall_status, r.submitted_at, r.completed_at, "
                   + "c.cycle_name, "
                   + "s.student_id, s.student_number, s.year_level, "
                   + "u.full_name, p.program_code "
                   + "FROM clearance_items i "
                   + "JOIN clearance_requirements req ON req.requirement_id = i.requirement_id "
                   + "JOIN offices o ON o.office_id = req.office_id "
                   + "JOIN clearance_requests r ON r.request_id = i.request_id "
                   + "JOIN clearance_cycles c ON c.cycle_id = r.cycle_id "
                   + "JOIN students s ON s.student_id = r.student_id "
                   + "JOIN users u ON u.user_id = s.user_id "
                   + "JOIN programs p ON p.program_id = s.program_id "
                   + "WHERE i.item_id = ?";

        Map<String, Object> detail = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detail = new HashMap<>();
                    detail.put("item_id", rs.getString("item_id"));
                    detail.put("status", rs.getString("status"));
                    detail.put("remarks", rs.getString("remarks"));
                    detail.put("reviewed_at", rs.getString("reviewed_at"));
                    detail.put("office_code", rs.getString("office_code"));
                    detail.put("office_name", rs.getString("office_name"));
                    detail.put("request_id", rs.getString("request_id"));
                    detail.put("overall_status", rs.getString("overall_status"));
                    detail.put("submitted_at", rs.getString("submitted_at"));
                    detail.put("completed_at", rs.getString("completed_at"));
                    detail.put("cycle_name", rs.getString("cycle_name"));
                    detail.put("student_id", rs.getString("student_id"));
                    detail.put("student_number", rs.getString("student_number"));
                    detail.put("year_level", rs.getString("year_level"));
                    detail.put("full_name", rs.getString("full_name"));
                    detail.put("program_code", rs.getString("program_code"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[OfficerDAO] getItemDetail error: " + e.getMessage());
        }

        if (detail != null) {
            detail.put("history", getItemHistory(itemId));
        }
        return detail;
    }

    private List<String[]> getItemHistory(int itemId) {
        String sql = "SELECT h.old_status, h.new_status, h.remarks, h.created_at, u.full_name "
                   + "FROM clearance_history h "
                   + "LEFT JOIN users u ON u.user_id = h.changed_by "
                   + "WHERE h.item_id = ? ORDER BY h.created_at DESC";
        List<String[]> history = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new String[]{rs.getString("old_status"),
                            rs.getString("new_status"), rs.getString("remarks"),
                            rs.getString("created_at"), rs.getString("full_name")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[OfficerDAO] getItemHistory error: " + e.getMessage());
        }
        return history;
    }

    public boolean decide(int itemId, String newStatus, String remarks, int userId) {
        try (Connection conn = DBConnection.getConnection()) {

            String infoSql = "SELECT i.status AS old_status, i.request_id, "
                           + "s.user_id AS student_user_id, o.office_code, o.office_name "
                           + "FROM clearance_items i "
                           + "JOIN clearance_requirements req ON req.requirement_id = i.requirement_id "
                           + "JOIN offices o ON o.office_id = req.office_id "
                           + "JOIN clearance_requests r ON r.request_id = i.request_id "
                           + "JOIN students s ON s.student_id = r.student_id "
                           + "WHERE i.item_id = ?";
            String oldStatus = null;
            int requestId = 0;
            int studentUserId = 0;
            String officeCode = "";
            String officeName = "";
            try (PreparedStatement ps = conn.prepareStatement(infoSql)) {
                ps.setInt(1, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        oldStatus = rs.getString("old_status");
                        requestId = rs.getInt("request_id");
                        studentUserId = rs.getInt("student_user_id");
                        officeCode = rs.getString("office_code");
                        officeName = rs.getString("office_name");
                    }
                }
            }
            if (requestId == 0) {
                return false;
            }

            String updateSql = "UPDATE clearance_items SET status = ?, remarks = ?, "
                             + "reviewed_by = ?, reviewed_at = NOW() WHERE item_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newStatus);
                ps.setString(2, remarks);
                ps.setInt(3, userId);
                ps.setInt(4, itemId);
                ps.executeUpdate();
            }

            String histSql = "INSERT INTO clearance_history "
                           + "(item_id, changed_by, old_status, new_status, remarks) "
                           + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(histSql)) {
                ps.setInt(1, itemId);
                ps.setInt(2, userId);
                ps.setString(3, oldStatus);
                ps.setString(4, newStatus);
                ps.setString(5, remarks);
                ps.executeUpdate();
            }

            String notifSql = "INSERT INTO notifications (user_id, title, message) "
                            + "VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(notifSql)) {
                String readable = newStatus.replace('_', ' ');
                ps.setInt(1, studentUserId);
                ps.setString(2, "Clearance Update");
                ps.setString(3, "Ang iyong clearance item para sa " + officeCode
                        + " (" + officeName + ") ay naka-" + readable + " na.");
                ps.executeUpdate();
            }

            String newOverall = "AWAITING_REGISTRAR";
            if (hasItemsWithStatus(conn, requestId, "ACTION_REQUIRED")) {
                newOverall = "ACTION_REQUIRED";
            } else if (hasItemsWithStatus(conn, requestId, "PENDING", "IN_REVIEW")) {
                newOverall = "IN_PROGRESS";
            }
            String reqSql = "UPDATE clearance_requests SET overall_status = ? "
                          + "WHERE request_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(reqSql)) {
                ps.setString(1, newOverall);
                ps.setInt(2, requestId);
                ps.executeUpdate();
            }

            String auditSql = "INSERT INTO audit_logs "
                            + "(actor_user_id, action, entity_type, entity_id, details) "
                            + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, userId);
                ps.setString(2, "DECISION");
                ps.setString(3, "CLEARANCE_ITEM");
                ps.setInt(4, itemId);
                ps.setString(5, "Office item " + newStatus + " ni officer");
                ps.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.err.println("[OfficerDAO] decide error: " + e.getMessage());
            return false;
        }
    }

    private boolean hasItemsWithStatus(Connection conn, int requestId, String... statuses)
            throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS cnt FROM clearance_items "
                + "WHERE request_id = ? AND status IN (");
        for (int i = 0; i < statuses.length; i++) {
            sql.append(i > 0 ? ", ?" : "?");
        }
        sql.append(")");
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, requestId);
            for (int i = 0; i < statuses.length; i++) {
                ps.setString(i + 2, statuses[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("cnt") > 0;
            }
        }
    }

    public List<String[]> listAllStudents() {
        String sql = "SELECT s.student_id, s.student_number, u.full_name, "
                   + "p.program_code, s.year_level "
                   + "FROM students s "
                   + "JOIN users u ON u.user_id = s.user_id "
                   + "JOIN programs p ON p.program_id = s.program_id "
                   + "ORDER BY s.student_number";
        List<String[]> students = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(new String[]{rs.getString("student_id"),
                        rs.getString("student_number"), rs.getString("full_name"),
                        rs.getString("program_code"), rs.getString("year_level")});
            }
        } catch (SQLException e) {
            System.err.println("[OfficerDAO] listAllStudents error: " + e.getMessage());
        }
        return students;
    }

    public List<String[]> listStudentRequests(int studentId) {
        String sql = "SELECT r.request_id, c.cycle_name, r.overall_status, "
                   + "r.submitted_at, r.completed_at "
                   + "FROM clearance_requests r "
                   + "JOIN clearance_cycles c ON c.cycle_id = r.cycle_id "
                   + "WHERE r.student_id = ? ORDER BY r.submitted_at DESC";
        List<String[]> requests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(new String[]{rs.getString("request_id"),
                            rs.getString("cycle_name"), rs.getString("overall_status"),
                            rs.getString("submitted_at"), rs.getString("completed_at")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[OfficerDAO] listStudentRequests error: " + e.getMessage());
        }
        return requests;
    }
}
