package dao;

import config.DBConnection;
import model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Subject(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("code"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[SubjectDAO] getAllSubjects error: " + e.getMessage());
        }
        return list;
    }

    public boolean addSubject(Subject s) {
        String sql = "INSERT INTO subjects (name, code, description) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getCode());
            ps.setString(3, s.getDescription());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[SubjectDAO] addSubject error: " + e.getMessage());
            return false;
        }
    }
}
