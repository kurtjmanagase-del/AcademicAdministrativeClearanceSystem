package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditDAO {

    public void log(int actorUserId, String action, String entityType,
                    int entityId, String details) {
        String sql = "INSERT INTO audit_logs "
                   + "(actor_user_id, action, entity_type, entity_id, details) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, actorUserId > 0 ? actorUserId : null);
            ps.setString(2, action);
            ps.setString(3, entityType);
            ps.setObject(4, entityId > 0 ? entityId : null);
            ps.setString(5, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AuditDAO] log error: " + e.getMessage());
        }
    }

    public List<String[]> listAll() {
        String sql = "SELECT a.log_id, u.username AS actor_username, "
                   + "u.full_name AS actor_full_name, a.action, a.entity_type, "
                   + "a.entity_id, a.details, a.created_at "
                   + "FROM audit_logs a "
                   + "LEFT JOIN users u ON u.user_id = a.actor_user_id "
                   + "ORDER BY a.log_id DESC LIMIT 200";
        return runQuery(sql);
    }

    public List<String[]> listByActor(int actorUserId) {
        String sql = "SELECT a.log_id, u.username AS actor_username, "
                   + "u.full_name AS actor_full_name, a.action, a.entity_type, "
                   + "a.entity_id, a.details, a.created_at "
                   + "FROM audit_logs a "
                   + "LEFT JOIN users u ON u.user_id = a.actor_user_id "
                   + "WHERE a.actor_user_id = ? "
                   + "ORDER BY a.log_id DESC LIMIT 200";
        return runQuery(sql, actorUserId);
    }

    public List<String[]> listByAction(String action) {
        String sql = "SELECT a.log_id, u.username AS actor_username, "
                   + "u.full_name AS actor_full_name, a.action, a.entity_type, "
                   + "a.entity_id, a.details, a.created_at "
                   + "FROM audit_logs a "
                   + "LEFT JOIN users u ON u.user_id = a.actor_user_id "
                   + "WHERE a.action = ? "
                   + "ORDER BY a.log_id DESC LIMIT 200";
        return runQuery(sql, action);
    }

    private List<String[]> runQuery(String sql, Object... params) {
        List<String[]> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(new String[]{rs.getString("log_id"),
                            rs.getString("actor_username"),
                            rs.getString("actor_full_name"),
                            rs.getString("action"),
                            rs.getString("entity_type"),
                            rs.getString("entity_id"),
                            rs.getString("details"),
                            rs.getString("created_at")});
                }
            }
        } catch (SQLException e) {
            System.err.println("[AuditDAO] runQuery error: " + e.getMessage());
        }
        return logs;
    }
}
