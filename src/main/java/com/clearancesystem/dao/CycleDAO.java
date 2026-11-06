package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CycleDAO {

    public List<String[]> listCycles() {
        String sql = "SELECT c.cycle_id, c.term_id, c.cycle_name, c.opens_at, c.closes_at, c.status, "
                   + "t.school_year, t.semester_name "
                   + "FROM clearance_cycles c "
                   + "JOIN academic_terms t ON t.term_id = c.term_id "
                   + "ORDER BY c.cycle_id DESC";
        List<String[]> cycles = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cycles.add(new String[]{rs.getString("cycle_id"),
                        rs.getString("term_id"), rs.getString("cycle_name"),
                        rs.getString("school_year"), rs.getString("semester_name"),
                        rs.getString("opens_at"), rs.getString("closes_at"),
                        rs.getString("status")});
            }
        } catch (SQLException e) {
            System.err.println("[CycleDAO] listCycles error: " + e.getMessage());
        }
        return cycles;
    }

    public int createCycle(int termId, String cycleName, String opensAt,
                           String closesAt, int createdByUserId) {
        String sql = "INSERT INTO clearance_cycles (term_id, cycle_name, opens_at, closes_at, status, created_by) "
                   + "VALUES (?, ?, ?, ?, 'DRAFT', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, termId);
            ps.setString(2, cycleName);
            ps.setString(3, opensAt);
            ps.setString(4, closesAt);
            ps.setInt(5, createdByUserId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CycleDAO] createCycle error: " + e.getMessage());
        }
        return 0;
    }

    public boolean updateCycle(int cycleId, int termId, String cycleName,
                               String opensAt, String closesAt) {
        String sql = "UPDATE clearance_cycles SET term_id = ?, cycle_name = ?, "
                   + "opens_at = ?, closes_at = ? WHERE cycle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, termId);
            ps.setString(2, cycleName);
            ps.setString(3, opensAt);
            ps.setString(4, closesAt);
            ps.setInt(5, cycleId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[CycleDAO] updateCycle error: " + e.getMessage());
            return false;
        }
    }

    public boolean setCycleStatus(int cycleId, String status) {
        String sql = "UPDATE clearance_cycles SET status = ? WHERE cycle_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, cycleId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[CycleDAO] setCycleStatus error: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> getCycleOffices(int cycleId) {
        String sql = "SELECT r.office_id, o.office_code "
                   + "FROM clearance_requirements r "
                   + "JOIN offices o ON o.office_id = r.office_id "
                   + "WHERE r.cycle_id = ? ORDER BY o.office_code";
        List<String[]> offices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cycleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    offices.add(new String[]{rs.getString("office_id"),
                            rs.getString("office_code")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[CycleDAO] getCycleOffices error: " + e.getMessage());
        }
        return offices;
    }

    public boolean setCycleOffices(int cycleId, List<Integer> officeIds) {
        String deleteSql = "DELETE FROM clearance_requirements WHERE cycle_id = ?";
        String insertSql = "INSERT INTO clearance_requirements (cycle_id, office_id, is_required, display_order) "
                         + "VALUES (?, ?, 1, ?)";
        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, cycleId);
                psDelete.executeUpdate();
            }
            if (officeIds != null && !officeIds.isEmpty()) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                    int order = 1;
                    for (Integer officeId : officeIds) {
                        psInsert.setInt(1, cycleId);
                        psInsert.setInt(2, officeId);
                        psInsert.setInt(3, order++);
                        psInsert.addBatch();
                    }
                    psInsert.executeBatch();
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[CycleDAO] setCycleOffices error: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> listTerms() {
        String sql = "SELECT term_id, school_year, semester_name "
                   + "FROM academic_terms ORDER BY school_year DESC, term_id ASC";
        List<String[]> terms = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                terms.add(new String[]{rs.getString("term_id"),
                        rs.getString("school_year"), rs.getString("semester_name")});
            }
        } catch (SQLException e) {
            System.err.println("[CycleDAO] listTerms error: " + e.getMessage());
        }
        return terms;
    }

    public List<String[]> listActiveOffices() {
        String sql = "SELECT office_id, office_code, office_name "
                   + "FROM offices WHERE is_active = 1 ORDER BY office_code";
        List<String[]> offices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                offices.add(new String[]{rs.getString("office_id"),
                        rs.getString("office_code"), rs.getString("office_name")});
            }
        } catch (SQLException e) {
            System.err.println("[CycleDAO] listActiveOffices error: " + e.getMessage());
        }
        return offices;
    }
}
