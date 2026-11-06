package com.clearancesystem.dao;

import com.clearancesystem.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TermDAO {

    public List<String[]> listTerms() {
        String sql = "SELECT term_id, school_year, semester_name, start_date, end_date, status "
                   + "FROM academic_terms ORDER BY term_id DESC";
        List<String[]> terms = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                terms.add(new String[]{rs.getString("term_id"),
                        rs.getString("school_year"), rs.getString("semester_name"),
                        rs.getString("start_date"), rs.getString("end_date"),
                        rs.getString("status")});
            }
        } catch (SQLException e) {
            System.err.println("[TermDAO] listTerms error: " + e.getMessage());
        }
        return terms;
    }

    public boolean createTerm(String schoolYear, String semesterName, String startDate, String endDate) {
        String sql = "INSERT INTO academic_terms (school_year, semester_name, start_date, end_date, status) "
                   + "VALUES (?, ?, ?, ?, 'OPEN')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, schoolYear);
            ps.setString(2, semesterName);
            ps.setString(3, startDate);
            ps.setString(4, endDate);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[TermDAO] createTerm error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTerm(int termId, String schoolYear, String semesterName, String startDate, String endDate) {
        String sql = "UPDATE academic_terms SET school_year = ?, semester_name = ?, "
                   + "start_date = ?, end_date = ? WHERE term_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, schoolYear);
            ps.setString(2, semesterName);
            ps.setString(3, startDate);
            ps.setString(4, endDate);
            ps.setInt(5, termId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[TermDAO] updateTerm error: " + e.getMessage());
            return false;
        }
    }

    public boolean setTermStatus(int termId, String status) {
        String sql = "UPDATE academic_terms SET status = ? WHERE term_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, termId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[TermDAO] setTermStatus error: " + e.getMessage());
            return false;
        }
    }
}
