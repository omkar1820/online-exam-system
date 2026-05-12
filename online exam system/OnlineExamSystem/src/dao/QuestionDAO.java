package dao;

import config.DBConnection;
import model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    /** Fetch all questions for an exam */
    public List<Question> getQuestionsByExam(int examId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE exam_id = ? ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("[QuestionDAO] getQuestions error: " + e.getMessage());
        }
        return list;
    }

    /** Count questions for an exam */
    public int countQuestions(int examId) {
        String sql = "SELECT COUNT(*) FROM questions WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("[QuestionDAO] count error: " + e.getMessage());
        }
        return 0;
    }

    /** Add a new question */
    public boolean addQuestion(Question q) {
        String sql = "INSERT INTO questions (exam_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1,    q.getExamId());
            ps.setString(2, q.getQuestion());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, String.valueOf(q.getCorrectAns()));
            ps.setInt(8,    q.getMarks());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[QuestionDAO] addQuestion error: " + e.getMessage());
            return false;
        }
    }

    /** Delete question by ID */
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[QuestionDAO] delete error: " + e.getMessage());
            return false;
        }
    }

    /** Update question */
    public boolean updateQuestion(Question q) {
        String sql = "UPDATE questions SET question=?, option_a=?, option_b=?, option_c=?, option_d=?, correct_ans=?, marks=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, q.getQuestion());
            ps.setString(2, q.getOptionA());
            ps.setString(3, q.getOptionB());
            ps.setString(4, q.getOptionC());
            ps.setString(5, q.getOptionD());
            ps.setString(6, String.valueOf(q.getCorrectAns()));
            ps.setInt(7,    q.getMarks());
            ps.setInt(8,    q.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[QuestionDAO] update error: " + e.getMessage());
            return false;
        }
    }

    private Question mapRow(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setId(rs.getInt("id"));
        q.setExamId(rs.getInt("exam_id"));
        q.setQuestion(rs.getString("question"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setCorrectAns(rs.getString("correct_ans").charAt(0));
        q.setMarks(rs.getInt("marks"));
        return q;
    }
}
