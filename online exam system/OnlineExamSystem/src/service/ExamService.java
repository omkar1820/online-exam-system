package service;

import dao.*;
import model.*;

import java.util.*;

/**
 * ExamService — business logic for taking exams and scoring.
 */
public class ExamService {

    private final ExamDAO     examDAO     = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO   resultDAO   = new ResultDAO();

    /**
     * Core method: process submitted answers and persist result.
     *
     * @param studentId  ID of the student
     * @param examId     ID of the exam
     * @param answers    Map of questionId -> chosen letter ('A','B','C','D', or '\0' for skipped)
     * @param timeMins   Time taken in minutes
     * @return Result object with score, percentage, grade
     */
    public Result submitExam(int studentId, int examId,
                             Map<Integer, Character> answers, int timeMins) {

        Exam exam = examDAO.getExamById(examId);
        if (exam == null) return null;

        List<Question> questions = questionDAO.getQuestionsByExam(examId);

        int score = 0;
        for (Question q : questions) {
            char chosen = answers.getOrDefault(q.getId(), '\0');
            if (chosen != '\0' && Character.toUpperCase(chosen) == q.getCorrectAns()) {
                score += q.getMarks();
            }
        }

        double pct    = (exam.getTotalMarks() == 0) ? 0.0
                       : (score * 100.0 / exam.getTotalMarks());
        boolean passed = score >= exam.getPassMarks();

        Result result = new Result();
        result.setStudentId(studentId);
        result.setExamId(examId);
        result.setScore(score);
        result.setTotalMarks(exam.getTotalMarks());
        result.setPercentage(Math.round(pct * 100.0) / 100.0);
        result.setPassed(passed);
        result.setTimeTakenMins(timeMins);
        result.setExamTitle(exam.getTitle());

        // Persist
        int resultId = resultDAO.saveResult(result);
        if (resultId > 0) {
            for (Question q : questions) {
                char chosen   = answers.getOrDefault(q.getId(), '\0');
                boolean correct = chosen != '\0' && Character.toUpperCase(chosen) == q.getCorrectAns();
                resultDAO.saveAnswers(resultId, q.getId(), chosen == '\0' ? '-' : chosen, correct);
            }
        }
        result.setId(resultId);
        return result;
    }

    /** Returns list of exams not yet attempted by student */
    public List<Exam> getAvailableExams(int studentId) {
        List<Exam> active  = examDAO.getActiveExams();
        List<Exam> available = new ArrayList<>();
        for (Exam e : active) {
            if (!examDAO.hasAttempted(studentId, e.getId())) {
                available.add(e);
            }
        }
        return available;
    }

    /** Returns list of exams already attempted by student */
    public List<Exam> getAttemptedExams(int studentId) {
        List<Exam> active  = examDAO.getActiveExams();
        List<Exam> attempted = new ArrayList<>();
        for (Exam e : active) {
            if (examDAO.hasAttempted(studentId, e.getId())) {
                attempted.add(e);
            }
        }
        return attempted;
    }
}
