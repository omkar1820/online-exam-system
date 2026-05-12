package dao;

import config.DBConnection;
import model.Exam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {

    /** Get all active exams (for students) */
    public List<Exam> getActiveExams() {
        List<Exam> list = new ArrayList<>();
        String sql = """
            SELECT e.*, s.name AS subject_name
            FROM exams e
            JOIN subjects s ON e.subject_id = s.id
            WHERE e.is_active = TRUE
            ORDER BY e.title
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ExamDAO] getActiveExams error: " + e.getMessage());
        }
        return list;
    }

    /** Get all exams (for admin) */
    public List<Exam> getAllExams() {
        List<Exam> list = new ArrayList<>();
        String sql = """
            SELECT e.*, s.name AS subject_name
            FROM exams e
            JOIN subjects s ON e.subject_id = s.id
            ORDER BY e.created_at DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ExamDAO] getAllExams error: " + e.getMessage());
        }
        return list;
    }

    /** Create a new exam */
    public int createExam(Exam exam) {
        String sql = "INSERT INTO exams (title, subject_id, total_marks, duration_mins, pass_marks, is_active, created_by) VALUES (?,?,?,?,?,TRUE,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, exam.getTitle());
            ps.setInt(2,    exam.getSubjectId());
            ps.setInt(3,    exam.getTotalMarks());
            ps.setInt(4,    exam.getDurationMins());
            ps.setInt(5,    exam.getPassMarks());
            ps.setInt(6,    exam.getCreatedBy());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ExamDAO] createExam error: " + e.getMessage());
        }
        return -1;
    }

    /** Toggle exam active status */
    public boolean toggleExamStatus(int examId, boolean active) {
        String sql = "UPDATE exams SET is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setInt(2, examId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ExamDAO] toggleStatus error: " + e.getMessage());
            return false;
        }
    }

    /** Delete exam (cascades to questions) */
    public boolean deleteExam(int examId) {
        String sql = "DELETE FROM exams WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, examId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[ExamDAO] deleteExam error: " + e.getMessage());
            return false;
        }
    }

    /** Check if student already attempted this exam */
    public boolean hasAttempted(int studentId, int examId) {
        String sql = "SELECT COUNT(*) FROM results WHERE student_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("[ExamDAO] hasAttempted error: " + e.getMessage());
        }
        return false;
    }

    /** Get exam by ID */
    public Exam getExamById(int examId) {
        String sql = """
            SELECT e.*, s.name AS subject_name
            FROM exams e
            JOIN subjects s ON e.subject_id = s.id
            WHERE e.id = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            System.err.println("[ExamDAO] getExamById error: " + e.getMessage());
        }
        return null;
    }

    private Exam mapRow(ResultSet rs) throws SQLException {
        Exam e = new Exam();
        e.setId(rs.getInt("id"));
        e.setTitle(rs.getString("title"));
        e.setSubjectId(rs.getInt("subject_id"));
        e.setSubjectName(rs.getString("subject_name"));
        e.setTotalMarks(rs.getInt("total_marks"));
        e.setDurationMins(rs.getInt("duration_mins"));
        e.setPassMarks(rs.getInt("pass_marks"));
        e.setActive(rs.getBoolean("is_active"));
        e.setCreatedBy(rs.getInt("created_by"));
        return e;
    }
}
