package dao;

import config.DBConnection;
import model.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    /** Save exam result and return generated result ID */
    public int saveResult(Result result) {
        String sql = """
            INSERT INTO results (student_id, exam_id, score, total_marks, percentage, passed, time_taken_mins)
            VALUES (?,?,?,?,?,?,?)
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,     result.getStudentId());
            ps.setInt(2,     result.getExamId());
            ps.setInt(3,     result.getScore());
            ps.setInt(4,     result.getTotalMarks());
            ps.setDouble(5,  result.getPercentage());
            ps.setBoolean(6, result.isPassed());
            ps.setInt(7,     result.getTimeTakenMins());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ResultDAO] saveResult error: " + e.getMessage());
        }
        return -1;
    }

    /** Save individual answers linked to a result */
    public void saveAnswers(int resultId, int questionId, char chosen, boolean isCorrect) {
        String sql = "INSERT INTO student_answers (result_id, question_id, chosen_ans, is_correct) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1,     resultId);
            ps.setInt(2,     questionId);
            ps.setString(3,  String.valueOf(chosen));
            ps.setBoolean(4, isCorrect);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[ResultDAO] saveAnswers error: " + e.getMessage());
        }
    }

    /** Get results for a specific student */
    public List<Result> getResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.full_name AS student_name, e.title AS exam_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN exams e ON r.exam_id   = e.id
            WHERE r.student_id = ?
            ORDER BY r.attempted_at DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ResultDAO] getResultsByStudent error: " + e.getMessage());
        }
        return list;
    }

    /** Get ALL results (for admin analytics) */
    public List<Result> getAllResults() {
        List<Result> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.full_name AS student_name, e.title AS exam_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN exams e ON r.exam_id   = e.id
            ORDER BY r.attempted_at DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ResultDAO] getAllResults error: " + e.getMessage());
        }
        return list;
    }

    /** Get results by exam ID (for admin) */
    public List<Result> getResultsByExam(int examId) {
        List<Result> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.full_name AS student_name, e.title AS exam_title
            FROM results r
            JOIN users u ON r.student_id = u.id
            JOIN exams e ON r.exam_id   = e.id
            WHERE r.exam_id = ?
            ORDER BY r.score DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[ResultDAO] getResultsByExam error: " + e.getMessage());
        }
        return list;
    }

    /** Get pass/fail count for an exam */
    public int[] getPassFailCount(int examId) {
        String sql = "SELECT passed, COUNT(*) FROM results WHERE exam_id = ? GROUP BY passed";
        int pass = 0, fail = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getBoolean(1)) pass = rs.getInt(2);
                else fail = rs.getInt(2);
            }
        } catch (SQLException e) {
            System.err.println("[ResultDAO] passFailCount error: " + e.getMessage());
        }
        return new int[]{pass, fail};
    }

    private Result mapRow(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setId(rs.getInt("id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStudentName(rs.getString("student_name"));
        r.setExamId(rs.getInt("exam_id"));
        r.setExamTitle(rs.getString("exam_title"));
        r.setScore(rs.getInt("score"));
        r.setTotalMarks(rs.getInt("total_marks"));
        r.setPercentage(rs.getDouble("percentage"));
        r.setPassed(rs.getBoolean("passed"));
        r.setTimeTakenMins(rs.getInt("time_taken_mins"));
        r.setAttemptedAt(rs.getString("attempted_at"));
        return r;
    }
}
