package ui;

import dao.*;
import model.*;
import service.ExamService;
import util.ConsoleUtil;
import util.ExamTimer;

import java.util.*;

/**
 * Student dashboard — browse exams, take exams, view results.
 */
public class StudentUI {

    private final Scanner     sc          = new Scanner(System.in);
    private final ExamService examService = new ExamService();
    private final ExamDAO     examDAO     = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final ResultDAO   resultDAO   = new ResultDAO();
    private final User        student;

    public StudentUI(User student) {
        this.student = student;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            ConsoleUtil.printHeader("STUDENT DASHBOARD");
            System.out.println(ConsoleUtil.BOLD + "  Welcome, " + student.getFullName() + "!" + ConsoleUtil.RESET);
            ConsoleUtil.printLine();
            System.out.println("  1. Available Exams");
            System.out.println("  2. Take an Exam");
            System.out.println("  3. My Results & Performance");
            System.out.println("  4. Attempted Exams");
            System.out.println("  0. Logout");
            ConsoleUtil.printLine();
            ConsoleUtil.printPrompt("Choice");

            int choice = ConsoleUtil.readInt(sc, 0, 4);
            switch (choice) {
                case 1 -> viewAvailableExams();
                case 2 -> takeExam();
                case 3 -> viewMyResults();
                case 4 -> viewAttemptedExams();
                case 0 -> { exit = true; ConsoleUtil.printSuccess("Logged out successfully."); }
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // 1. View available exams
    // ─────────────────────────────────────────────────────────
    private void viewAvailableExams() {
        ConsoleUtil.printHeader("AVAILABLE EXAMS");
        List<Exam> exams = examService.getAvailableExams(student.getId());

        if (exams.isEmpty()) {
            ConsoleUtil.printInfo("No exams available at the moment.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-30s %-15s %-8s %-8s%n",
                "No.", "Exam Title", "Subject", "Marks", "Duration");
        ConsoleUtil.printLine();

        int i = 1;
        for (Exam e : exams) {
            System.out.printf("  %-4d %-30s %-15s %-8d %-8s%n",
                    i++, e.getTitle(), e.getSubjectName(),
                    e.getTotalMarks(), e.getDurationMins() + " min");
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 2. Take an exam
    // ─────────────────────────────────────────────────────────
    private void takeExam() {
        ConsoleUtil.printHeader("TAKE EXAM");
        List<Exam> exams = examService.getAvailableExams(student.getId());

        if (exams.isEmpty()) {
            ConsoleUtil.printInfo("No exams available to take.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-30s %-8s %-8s%n", "No.", "Exam Title", "Marks", "Duration");
        ConsoleUtil.printLine();
        int i = 1;
        for (Exam e : exams) {
            System.out.printf("  %-4d %-30s %-8d %-8s%n",
                    i++, e.getTitle(), e.getTotalMarks(), e.getDurationMins() + " min");
        }

        ConsoleUtil.printLine();
        System.out.println("  Enter 0 to go back.");
        ConsoleUtil.printPrompt("Select Exam No");
        int sel = ConsoleUtil.readInt(sc, 0, exams.size());
        if (sel == 0) return;

        Exam chosen = exams.get(sel - 1);
        startExam(chosen);
    }

    private void startExam(Exam exam) {
        int qCount = questionDAO.countQuestions(exam.getId());
        if (qCount == 0) {
            ConsoleUtil.printError("This exam has no questions yet!");
            ConsoleUtil.pause(sc);
            return;
        }

        ConsoleUtil.printHeader("EXAM: " + exam.getTitle().toUpperCase());
        System.out.println("  Subject   : " + exam.getSubjectName());
        System.out.println("  Questions : " + qCount);
        System.out.println("  Marks     : " + exam.getTotalMarks());
        System.out.println("  Duration  : " + exam.getDurationMins() + " minutes");
        System.out.println("  Pass Marks: " + exam.getPassMarks());
        ConsoleUtil.printLine();
        ConsoleUtil.printInfo("Rules: Each question has ONE correct answer (A/B/C/D).");
        ConsoleUtil.printInfo("Type 'S' to skip a question.");
        ConsoleUtil.printInfo("Type 'Q' at any time to quit and submit what you have.");
        ConsoleUtil.printLine();
        System.out.print("  Type START to begin the exam: ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (!confirm.equals("START")) {
            ConsoleUtil.printInfo("Exam cancelled.");
            return;
        }

        List<Question> questions = questionDAO.getQuestionsByExam(exam.getId());
        Map<Integer, Character> answers = new HashMap<>();

        ExamTimer timer = new ExamTimer(exam.getDurationMins());
        timer.start();

        int qNum = 1;
        for (Question q : questions) {
            if (timer.isTimeUp()) break;

            System.out.println();
            ConsoleUtil.printLine();
            System.out.println("  " + timer.getTimeRemaining() +
                    ConsoleUtil.BOLD + "   Q" + qNum + "/" + questions.size() + ConsoleUtil.RESET);
            ConsoleUtil.printLine();
            System.out.println(ConsoleUtil.BOLD + "  Q" + qNum + ". " + q.getQuestion() + ConsoleUtil.RESET);
            System.out.println();
            System.out.println("    A) " + q.getOptionA());
            System.out.println("    B) " + q.getOptionB());
            System.out.println("    C) " + q.getOptionC());
            System.out.println("    D) " + q.getOptionD());
            System.out.println("  " + ConsoleUtil.YELLOW + "[" + q.getMarks() + " mark(s)]" + ConsoleUtil.RESET);
            ConsoleUtil.printLine();

            ConsoleUtil.printPrompt("Your answer (A/B/C/D | S=skip | Q=quit+submit)");
            String ans = ConsoleUtil.readLine(sc).toUpperCase();

            if (ans.equals("Q")) {
                ConsoleUtil.printInfo("Submitting exam...");
                break;
            }

            if (!ans.equals("S") && ans.matches("[ABCD]")) {
                answers.put(q.getId(), ans.charAt(0));
            }
            qNum++;
        }

        timer.stop();
        int timeTaken = timer.getElapsedMins();

        // Submit
        Result result = examService.submitExam(student.getId(), exam.getId(), answers, timeTaken);
        showResultCard(result, exam);
    }

    // ─────────────────────────────────────────────────────────
    // Result card display
    // ─────────────────────────────────────────────────────────
    private void showResultCard(Result result, Exam exam) {
        ConsoleUtil.printHeader("RESULT CARD");

        if (result == null) {
            ConsoleUtil.printError("Could not save result. Please contact admin.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.println();
        System.out.println("  " + ConsoleUtil.BOLD + "Student : " + ConsoleUtil.RESET + student.getFullName());
        System.out.println("  " + ConsoleUtil.BOLD + "Exam    : " + ConsoleUtil.RESET + exam.getTitle());
        System.out.println("  " + ConsoleUtil.BOLD + "Subject : " + ConsoleUtil.RESET + exam.getSubjectName());
        ConsoleUtil.printLine();
        System.out.println("  Score   : " + ConsoleUtil.BOLD + result.getScore() + " / " + result.getTotalMarks() + ConsoleUtil.RESET);
        System.out.println("  " + ConsoleUtil.progressBar(result.getPercentage()));
        System.out.println("  Grade   : " + ConsoleUtil.gradeBadge(result.getGrade()));
        System.out.println("  Time    : " + result.getTimeTakenMins() + " min(s) taken");
        ConsoleUtil.printLine();
        ConsoleUtil.printPassFail(result.isPassed());
        ConsoleUtil.printLine();
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 3. My results
    // ─────────────────────────────────────────────────────────
    private void viewMyResults() {
        ConsoleUtil.printHeader("MY PERFORMANCE REPORT");
        List<Result> results = resultDAO.getResultsByStudent(student.getId());

        if (results.isEmpty()) {
            ConsoleUtil.printInfo("You have not attempted any exams yet.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-28s %-10s %-10s %-8s %-8s%n",
                "No.", "Exam", "Score", "Percentage", "Grade", "Status");
        ConsoleUtil.printLine();

        int i = 1;
        double totalPct = 0;
        int passCount = 0;

        for (Result r : results) {
            totalPct += r.getPercentage();
            if (r.isPassed()) passCount++;

            String status = r.isPassed()
                    ? ConsoleUtil.GREEN + "PASS" + ConsoleUtil.RESET
                    : ConsoleUtil.RED   + "FAIL" + ConsoleUtil.RESET;

            System.out.printf("  %-4d %-28s %-10s %-10s %-8s %-8s%n",
                    i++,
                    r.getExamTitle().length() > 26 ? r.getExamTitle().substring(0, 26) + ".." : r.getExamTitle(),
                    r.getScore() + "/" + r.getTotalMarks(),
                    String.format("%.1f%%", r.getPercentage()),
                    r.getGrade(),
                    status);
        }

        ConsoleUtil.printLine();
        System.out.printf("  Exams Attempted: %d  |  Passed: %d  |  Failed: %d%n",
                results.size(), passCount, results.size() - passCount);
        System.out.printf("  Average Score  : %.2f%%%n", totalPct / results.size());
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 4. Attempted exams list
    // ─────────────────────────────────────────────────────────
    private void viewAttemptedExams() {
        ConsoleUtil.printHeader("ATTEMPTED EXAMS");
        List<Exam> attempted = examService.getAttemptedExams(student.getId());

        if (attempted.isEmpty()) {
            ConsoleUtil.printInfo("You haven't attempted any exam yet.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-30s %-15s%n", "No.", "Exam Title", "Subject");
        ConsoleUtil.printLine();
        int i = 1;
        for (Exam e : attempted) {
            System.out.printf("  %-4d %-30s %-15s%n", i++, e.getTitle(), e.getSubjectName());
        }
        ConsoleUtil.pause(sc);
    }
}
