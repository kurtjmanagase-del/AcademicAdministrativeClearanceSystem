package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClearanceDAO {

    public String[] getActiveCycle() {
        String sql = "SELECT c.cycle_id, c.cycle_name, t.school_year, t.semester_name "
                   + "FROM clearance_cycles c "
                   + "JOIN academic_terms t ON t.term_id = c.term_id "
                   + "WHERE c.status = 'OPEN' LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new String[]{rs.getString("cycle_id"),
                        rs.getString("cycle_name"),
                        rs.getString("school_year"),
                        rs.getString("semester_name")};
            }
        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] getActiveCycle error: " + e.getMessage());
        }
        return null;
    }

    public int findOrCreateRequest(int studentId, int cycleId) {
        String selectSql = "SELECT request_id FROM clearance_requests "
                         + "WHERE student_id = ? AND cycle_id = ?";
        String insertSql = "INSERT INTO clearance_requests (student_id, cycle_id, overall_status) "
                         + "VALUES (?, ?, 'IN_PROGRESS')";

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, cycleId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("request_id");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, studentId);
                ps.setInt(2, cycleId);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newRequestId = rs.getInt(1);

                        createItemsForRequest(conn, newRequestId, cycleId);
                        return newRequestId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] findOrCreateRequest error: " + e.getMessage());
        }
        return 0;
    }

    private void createItemsForRequest(Connection conn, int requestId, int cycleId) throws SQLException {
        String sql = "INSERT INTO clearance_items (request_id, requirement_id, status) "
                   + "SELECT ?, requirement_id, 'PENDING' FROM clearance_requirements "
                   + "WHERE cycle_id = ? AND is_required = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.setInt(2, cycleId);
            ps.executeUpdate();
        }
    }

    public String[] getStudentRequest(int requestId) {
        String sql = "SELECT s.student_number, u.full_name, p.program_code, r.overall_status "
                   + "FROM clearance_requests r "
                   + "JOIN students s ON s.student_id = r.student_id "
                   + "JOIN users u ON u.user_id = s.user_id "
                   + "JOIN programs p ON p.program_id = s.program_id "
                   + "WHERE r.request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("student_number"),
                            rs.getString("full_name"),
                            rs.getString("program_code"),
                            rs.getString("overall_status")};
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] getStudentRequest error: " + e.getMessage());
        }
        return null;
    }

    public List<String[]> listItemsWithOffices(int requestId) {
        String sql = "SELECT ci.item_id, o.office_code, o.office_name, ci.status, ci.remarks, cr.display_order "
                   + "FROM clearance_items ci "
                   + "JOIN clearance_requirements cr ON cr.requirement_id = ci.requirement_id "
                   + "JOIN offices o ON o.office_id = cr.office_id "
                   + "WHERE ci.request_id = ? ORDER BY cr.display_order";
        List<String[]> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new String[]{rs.getString("item_id"),
                            rs.getString("office_code"),
                            rs.getString("office_name"),
                            rs.getString("status"),
                            rs.getString("remarks"),
                            rs.getString("display_order")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] listItemsWithOffices error: " + e.getMessage());
        }
        return items;
    }

    public boolean respondItem(int itemId, String responseText, int userId) {
        String selectSql = "SELECT status, request_id FROM clearance_items WHERE item_id = ?";
        String updateSql = "UPDATE clearance_items SET status = 'ACTION_REQUIRED', remarks = ?, "
                         + "reviewed_by = ?, reviewed_at = NOW() WHERE item_id = ?";
        String historySql = "INSERT INTO clearance_history "
                          + "(item_id, changed_by, old_status, new_status, remarks, created_at) "
                          + "VALUES (?, ?, ?, 'ACTION_REQUIRED', ?, NOW())";
        String notifySql = "INSERT INTO notifications (user_id, title, message, is_read, created_at) "
                         + "VALUES (?, 'Action Submitted', ?, 0, NOW())";

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            int requestId = 0;
            String oldStatus = "";

            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        oldStatus = rs.getString("status");
                        requestId = rs.getInt("request_id");
                    }
                }
            }
            if (requestId == 0) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, responseText);
                ps.setInt(2, userId);
                ps.setInt(3, itemId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(historySql)) {
                ps.setInt(1, itemId);
                ps.setInt(2, userId);
                ps.setString(3, oldStatus);
                ps.setString(4, responseText);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(notifySql)) {
                ps.setInt(1, userId);
                ps.setString(2, responseText);
                ps.executeUpdate();
            }

            conn.commit();

            recalcOverall(requestId);
            return true;

        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] respondItem error: " + e.getMessage());
            return false;
        }
    }

    public void recalcOverall(int requestId) {
        String countSql = "SELECT status, COUNT(*) AS cnt FROM clearance_items "
                        + "WHERE request_id = ? GROUP BY status";
        String updateSql = "UPDATE clearance_requests SET overall_status = ? WHERE request_id = ?";

        boolean hasActionRequired = false;
        boolean hasPending = false;

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setInt(1, requestId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String status = rs.getString("status");
                        if ("ACTION_REQUIRED".equals(status)) {
                            hasActionRequired = true;
                        } else if ("PENDING".equals(status) || "IN_REVIEW".equals(status)) {
                            hasPending = true;
                        }
                    }
                }
            }

            String newStatus;
            if (hasActionRequired) {
                newStatus = "ACTION_REQUIRED";
            } else if (hasPending) {
                newStatus = "IN_PROGRESS";
            } else {
                newStatus = "AWAITING_REGISTRAR";
            }

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, requestId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] recalcOverall error: " + e.getMessage());
        }
    }

    public List<String[]> listNotifications(int userId) {
        String sql = "SELECT title, message, is_read, created_at FROM notifications "
                   + "WHERE user_id = ? ORDER BY created_at DESC LIMIT 20";
        List<String[]> notifications = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new String[]{rs.getString("title"),
                            rs.getString("message"),
                            rs.getString("is_read"),
                            rs.getString("created_at")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] listNotifications error: " + e.getMessage());
        }
        return notifications;
    }

    public boolean markAllRead(int userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[ClearanceDAO] markAllRead error: " + e.getMessage());
            return false;
        }
    }
}
