package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfficeDAO {

    public List<String[]> listOffices() {
        String sql = "SELECT office_id, office_code, office_name, is_active "
                   + "FROM offices ORDER BY office_code";
        List<String[]> offices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                offices.add(new String[]{rs.getString("office_id"),
                        rs.getString("office_code"), rs.getString("office_name"),
                        rs.getString("is_active")});
            }
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] listOffices error: " + e.getMessage());
        }
        return offices;
    }

    public List<String[]> listActiveOffices() {
        String sql = "SELECT office_id, office_code, office_name, is_active "
                   + "FROM offices WHERE is_active = 1 ORDER BY office_code";
        List<String[]> offices = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                offices.add(new String[]{rs.getString("office_id"),
                        rs.getString("office_code"), rs.getString("office_name"),
                        rs.getString("is_active")});
            }
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] listActiveOffices error: " + e.getMessage());
        }
        return offices;
    }

    public boolean createOffice(String code, String name) {
        String sql = "INSERT INTO offices (office_code, office_name, is_active) "
                   + "VALUES (?, ?, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] createOffice error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOffice(int officeId, String code, String name) {
        String sql = "UPDATE offices SET office_code = ?, office_name = ? WHERE office_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, officeId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] updateOffice error: " + e.getMessage());
            return false;
        }
    }

    public boolean setOfficeActive(int officeId, boolean active) {
        String sql = "UPDATE offices SET is_active = ? WHERE office_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, officeId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] setOfficeActive error: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> listPrograms() {
        String sql = "SELECT p.program_id, p.department_id, p.program_code, p.program_name, "
                   + "p.is_active, d.department_code, d.department_name "
                   + "FROM programs p "
                   + "JOIN departments d ON d.department_id = p.department_id "
                   + "ORDER BY p.program_code";
        List<String[]> programs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                programs.add(new String[]{rs.getString("program_id"),
                        rs.getString("department_id"), rs.getString("program_code"),
                        rs.getString("program_name"), rs.getString("is_active"),
                        rs.getString("department_code"), rs.getString("department_name")});
            }
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] listPrograms error: " + e.getMessage());
        }
        return programs;
    }

    public boolean createProgram(int deptId, String code, String name) {
        String sql = "INSERT INTO programs (department_id, program_code, program_name, is_active) "
                   + "VALUES (?, ?, ?, 1)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ps.setString(2, code);
            ps.setString(3, name);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] createProgram error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProgram(int programId, int deptId, String code, String name) {
        String sql = "UPDATE programs SET department_id = ?, program_code = ?, program_name = ? "
                   + "WHERE program_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ps.setString(2, code);
            ps.setString(3, name);
            ps.setInt(4, programId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] updateProgram error: " + e.getMessage());
            return false;
        }
    }

    public boolean setProgramActive(int programId, boolean active) {
        String sql = "UPDATE programs SET is_active = ? WHERE program_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, programId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] setProgramActive error: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> listDepartments() {
        String sql = "SELECT department_id, department_code, department_name "
                   + "FROM departments ORDER BY department_code";
        List<String[]> departments = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                departments.add(new String[]{rs.getString("department_id"),
                        rs.getString("department_code"), rs.getString("department_name")});
            }
        } catch (SQLException e) {
            System.err.println("[OfficeDAO] listDepartments error: " + e.getMessage());
        }
        return departments;
    }
}
