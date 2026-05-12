package ui;

import dao.*;
import model.*;
import util.ConsoleUtil;

import java.util.*;

/**
 * Admin Dashboard — manage exams, questions, students, analytics.
 */
public class AdminUI {

    private final Scanner     sc          = new Scanner(System.in);
    private final ExamDAO     examDAO     = new ExamDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final UserDAO     userDAO     = new UserDAO();
    private final ResultDAO   resultDAO   = new ResultDAO();
    private final SubjectDAO  subjectDAO  = new SubjectDAO();
    private final User        admin;

    public AdminUI(User admin) {
        this.admin = admin;
    }

    public void show() {
        boolean exit = false;
        while (!exit) {
            ConsoleUtil.printHeader("ADMIN CONTROL PANEL");
            System.out.println(ConsoleUtil.BOLD + "  Logged in as: " + admin.getFullName() + ConsoleUtil.RESET);
            ConsoleUtil.printLine();
            System.out.println("  ── EXAM MANAGEMENT ──");
            System.out.println("  1.  Create New Exam");
            System.out.println("  2.  View All Exams");
            System.out.println("  3.  Toggle Exam Status (Active/Inactive)");
            System.out.println("  4.  Delete Exam");
            ConsoleUtil.printLine();
            System.out.println("  ── QUESTION MANAGEMENT ──");
            System.out.println("  5.  Add Question to Exam");
            System.out.println("  6.  View Questions of an Exam");
            System.out.println("  7.  Delete Question");
            ConsoleUtil.printLine();
            System.out.println("  ── STUDENTS & RESULTS ──");
            System.out.println("  8.  View All Students");
            System.out.println("  9.  View All Results");
            System.out.println("  10. Exam-wise Result Report");
            System.out.println("  11. Analytics Dashboard");
            ConsoleUtil.printLine();
            System.out.println("  ── SUBJECTS ──");
            System.out.println("  12. Add Subject");
            System.out.println("  13. View All Subjects");
            ConsoleUtil.printLine();
            System.out.println("  0.  Logout");
            ConsoleUtil.printLine();
            ConsoleUtil.printPrompt("Choice");

            int choice = ConsoleUtil.readInt(sc, 0, 13);
            switch (choice) {
                case 1  -> createExam();
                case 2  -> viewAllExams();
                case 3  -> toggleExamStatus();
                case 4  -> deleteExam();
                case 5  -> addQuestion();
                case 6  -> viewQuestions();
                case 7  -> deleteQuestion();
                case 8  -> viewAllStudents();
                case 9  -> viewAllResults();
                case 10 -> examWiseReport();
                case 11 -> analyticsBoard();
                case 12 -> addSubject();
                case 13 -> viewSubjects();
                case 0  -> { exit = true; ConsoleUtil.printSuccess("Admin logged out."); }
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // 1. Create Exam
    // ─────────────────────────────────────────────────────────
    private void createExam() {
        ConsoleUtil.printHeader("CREATE NEW EXAM");

        List<Subject> subjects = subjectDAO.getAllSubjects();
        if (subjects.isEmpty()) {
            ConsoleUtil.printError("No subjects found. Add a subject first!");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-15s %-30s%n", "No.", "Code", "Subject");
        ConsoleUtil.printLine();
        int i = 1;
        for (Subject s : subjects) {
            System.out.printf("  %-4d %-15s %-30s%n", i++, s.getCode(), s.getName());
        }

        ConsoleUtil.printLine();
        ConsoleUtil.printPrompt("Select Subject No");
        int sSel = ConsoleUtil.readInt(sc, 1, subjects.size());
        Subject sub = subjects.get(sSel - 1);

        ConsoleUtil.printPrompt("Exam Title");
        String title = ConsoleUtil.readLine(sc);

        ConsoleUtil.printPrompt("Total Marks");
        int total = ConsoleUtil.readInt(sc, 1, 1000);

        ConsoleUtil.printPrompt("Pass Marks");
        int pass = ConsoleUtil.readInt(sc, 1, total);

        ConsoleUtil.printPrompt("Duration (minutes)");
        int dur = ConsoleUtil.readInt(sc, 5, 300);

        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setSubjectId(sub.getId());
        exam.setTotalMarks(total);
        exam.setPassMarks(pass);
        exam.setDurationMins(dur);
        exam.setCreatedBy(admin.getId());

        int id = examDAO.createExam(exam);
        if (id > 0) {
            ConsoleUtil.printSuccess("Exam created with ID: " + id);
            ConsoleUtil.printInfo("Now add questions to this exam from Menu Option 5.");
        } else {
            ConsoleUtil.printError("Failed to create exam.");
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 2. View All Exams
    // ─────────────────────────────────────────────────────────
    private void viewAllExams() {
        ConsoleUtil.printHeader("ALL EXAMS");
        List<Exam> exams = examDAO.getAllExams();

        if (exams.isEmpty()) {
            ConsoleUtil.printInfo("No exams found.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-28s %-14s %-6s %-8s %-8s%n",
                "ID", "Title", "Subject", "Marks", "Dur(min)", "Status");
        ConsoleUtil.printLine();

        for (Exam e : exams) {
            String status = e.isActive()
                    ? ConsoleUtil.GREEN + "Active" + ConsoleUtil.RESET
                    : ConsoleUtil.RED   + "Inactive" + ConsoleUtil.RESET;
            System.out.printf("  %-4d %-28s %-14s %-6d %-8d %-8s%n",
                    e.getId(), truncate(e.getTitle(), 26),
                    truncate(e.getSubjectName(), 12),
                    e.getTotalMarks(), e.getDurationMins(), status);
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 3. Toggle exam status
    // ─────────────────────────────────────────────────────────
    private void toggleExamStatus() {
        ConsoleUtil.printHeader("TOGGLE EXAM STATUS");
        List<Exam> exams = examDAO.getAllExams();
        if (exams.isEmpty()) { ConsoleUtil.printInfo("No exams."); ConsoleUtil.pause(sc); return; }

        printExamPicker(exams);
        ConsoleUtil.printPrompt("Exam No (0=cancel)");
        int sel = ConsoleUtil.readInt(sc, 0, exams.size());
        if (sel == 0) return;

        Exam e = exams.get(sel - 1);
        boolean newStatus = !e.isActive();
        boolean ok = examDAO.toggleExamStatus(e.getId(), newStatus);

        if (ok) ConsoleUtil.printSuccess("Exam '" + e.getTitle() + "' is now " + (newStatus ? "ACTIVE" : "INACTIVE"));
        else    ConsoleUtil.printError("Failed to update status.");
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 4. Delete Exam
    // ─────────────────────────────────────────────────────────
    private void deleteExam() {
        ConsoleUtil.printHeader("DELETE EXAM");
        List<Exam> exams = examDAO.getAllExams();
        if (exams.isEmpty()) { ConsoleUtil.printInfo("No exams."); ConsoleUtil.pause(sc); return; }

        printExamPicker(exams);
        ConsoleUtil.printPrompt("Exam No (0=cancel)");
        int sel = ConsoleUtil.readInt(sc, 0, exams.size());
        if (sel == 0) return;

        Exam e = exams.get(sel - 1);
        System.out.print("  " + ConsoleUtil.RED + "Delete '" + e.getTitle() + "'? (yes/no): " + ConsoleUtil.RESET);
        String confirm = ConsoleUtil.readLine(sc);

        if (confirm.equalsIgnoreCase("yes")) {
            if (examDAO.deleteExam(e.getId()))
                ConsoleUtil.printSuccess("Exam deleted.");
            else
                ConsoleUtil.printError("Failed to delete.");
        } else {
            ConsoleUtil.printInfo("Cancelled.");
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 5. Add Question
    // ─────────────────────────────────────────────────────────
    private void addQuestion() {
        ConsoleUtil.printHeader("ADD QUESTION");
        List<Exam> exams = examDAO.getAllExams();
        if (exams.isEmpty()) { ConsoleUtil.printInfo("No exams."); ConsoleUtil.pause(sc); return; }

        printExamPicker(exams);
        ConsoleUtil.printPrompt("Exam No (0=cancel)");
        int sel = ConsoleUtil.readInt(sc, 0, exams.size());
        if (sel == 0) return;

        Exam e = exams.get(sel - 1);

        boolean addMore = true;
        while (addMore) {
            ConsoleUtil.printLine();
            int qCount = questionDAO.countQuestions(e.getId());
            ConsoleUtil.printInfo("Exam: " + e.getTitle() + " | Current questions: " + qCount);
            ConsoleUtil.printLine();

            ConsoleUtil.printPrompt("Question text");
            String qText = ConsoleUtil.readLine(sc);

            ConsoleUtil.printPrompt("Option A");
            String oa = ConsoleUtil.readLine(sc);
            ConsoleUtil.printPrompt("Option B");
            String ob = ConsoleUtil.readLine(sc);
            ConsoleUtil.printPrompt("Option C");
            String oc = ConsoleUtil.readLine(sc);
            ConsoleUtil.printPrompt("Option D");
            String od = ConsoleUtil.readLine(sc);

            char correct = '\0';
            while (correct == '\0') {
                ConsoleUtil.printPrompt("Correct Answer (A/B/C/D)");
                String ca = ConsoleUtil.readLine(sc).toUpperCase();
                if (ca.matches("[ABCD]")) correct = ca.charAt(0);
                else ConsoleUtil.printError("Enter A, B, C, or D.");
            }

            ConsoleUtil.printPrompt("Marks for this question");
            int marks = ConsoleUtil.readInt(sc, 1, 20);

            Question q = new Question(0, e.getId(), qText, oa, ob, oc, od, correct, marks);
            if (questionDAO.addQuestion(q))
                ConsoleUtil.printSuccess("Question added!");
            else
                ConsoleUtil.printError("Failed to add question.");

            System.out.print("  Add another question? (y/n): ");
            addMore = ConsoleUtil.readLine(sc).equalsIgnoreCase("y");
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 6. View Questions
    // ─────────────────────────────────────────────────────────
    private void viewQuestions() {
        ConsoleUtil.printHeader("VIEW QUESTIONS");
        List<Exam> exams = examDAO.getAllExams();
        if (exams.isEmpty()) { ConsoleUtil.printInfo("No exams."); ConsoleUtil.pause(sc); return; }

        printExamPicker(exams);
        ConsoleUtil.printPrompt("Exam No (0=cancel)");
        int sel = ConsoleUtil.readInt(sc, 0, exams.size());
        if (sel == 0) return;

        Exam e = exams.get(sel - 1);
        List<Question> questions = questionDAO.getQuestionsByExam(e.getId());

        ConsoleUtil.printHeader("Questions: " + e.getTitle());
        if (questions.isEmpty()) {
            ConsoleUtil.printInfo("No questions added yet.");
            ConsoleUtil.pause(sc);
            return;
        }

        int i = 1;
        for (Question q : questions) {
            System.out.println(ConsoleUtil.BOLD + "  Q" + i + ". [ID:" + q.getId() + "] " + q.getQuestion() + ConsoleUtil.RESET);
            System.out.println("     A) " + q.getOptionA());
            System.out.println("     B) " + q.getOptionB());
            System.out.println("     C) " + q.getOptionC());
            System.out.println("     D) " + q.getOptionD());
            System.out.println("     " + ConsoleUtil.GREEN + "✔ Answer: " + q.getCorrectAns() + " | Marks: " + q.getMarks() + ConsoleUtil.RESET);
            ConsoleUtil.printLine();
            i++;
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 7. Delete Question
    // ─────────────────────────────────────────────────────────
    private void deleteQuestion() {
        ConsoleUtil.printHeader("DELETE QUESTION");
        ConsoleUtil.printPrompt("Enter Question ID to delete");
        try {
            int qid = Integer.parseInt(ConsoleUtil.readLine(sc));
            if (questionDAO.deleteQuestion(qid))
                ConsoleUtil.printSuccess("Question ID " + qid + " deleted.");
            else
                ConsoleUtil.printError("Question not found.");
        } catch (NumberFormatException ex) {
            ConsoleUtil.printError("Invalid ID.");
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 8. View Students
    // ─────────────────────────────────────────────────────────
    private void viewAllStudents() {
        ConsoleUtil.printHeader("ALL STUDENTS");
        List<User> students = userDAO.getAllStudents();

        if (students.isEmpty()) {
            ConsoleUtil.printInfo("No students registered.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-6s %-20s %-25s %-20s%n",
                "No.", "ID", "Username", "Full Name", "Email");
        ConsoleUtil.printLine();

        int i = 1;
        for (User u : students) {
            System.out.printf("  %-4d %-6d %-20s %-25s %-20s%n",
                    i++, u.getId(), u.getUsername(), u.getFullName(), u.getEmail());
        }
        ConsoleUtil.printLine();
        ConsoleUtil.printInfo("Total students: " + students.size());
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 9. All Results
    // ─────────────────────────────────────────────────────────
    private void viewAllResults() {
        ConsoleUtil.printHeader("ALL EXAM RESULTS");
        List<Result> results = resultDAO.getAllResults();

        if (results.isEmpty()) {
            ConsoleUtil.printInfo("No results found.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-20s %-24s %-10s %-8s %-8s%n",
                "No.", "Student", "Exam", "Score", "Pct", "Grade");
        ConsoleUtil.printLine();

        int i = 1;
        for (Result r : results) {
            String status = r.isPassed()
                    ? ConsoleUtil.GREEN + "P" + ConsoleUtil.RESET
                    : ConsoleUtil.RED   + "F" + ConsoleUtil.RESET;
            System.out.printf("  %-4d %-20s %-24s %-10s %-8s %s %-8s%n",
                    i++,
                    truncate(r.getStudentName(), 18),
                    truncate(r.getExamTitle(), 22),
                    r.getScore() + "/" + r.getTotalMarks(),
                    String.format("%.1f%%", r.getPercentage()),
                    status,
                    r.getGrade());
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 10. Exam-wise Report
    // ─────────────────────────────────────────────────────────
    private void examWiseReport() {
        ConsoleUtil.printHeader("EXAM-WISE REPORT");
        List<Exam> exams = examDAO.getAllExams();
        if (exams.isEmpty()) { ConsoleUtil.printInfo("No exams."); ConsoleUtil.pause(sc); return; }

        printExamPicker(exams);
        ConsoleUtil.printPrompt("Exam No (0=cancel)");
        int sel = ConsoleUtil.readInt(sc, 0, exams.size());
        if (sel == 0) return;

        Exam e = exams.get(sel - 1);
        List<Result> results = resultDAO.getResultsByExam(e.getId());
        int[] pf = resultDAO.getPassFailCount(e.getId());

        ConsoleUtil.printHeader("Report: " + e.getTitle());
        System.out.println("  Attempts: " + results.size() +
                "  |  " + ConsoleUtil.GREEN + "Pass: " + pf[0] + ConsoleUtil.RESET +
                "  |  " + ConsoleUtil.RED   + "Fail: " + pf[1] + ConsoleUtil.RESET);
        ConsoleUtil.printLine();

        if (results.isEmpty()) {
            ConsoleUtil.printInfo("No attempts yet.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-22s %-10s %-10s %-8s%n",
                "Rank", "Student", "Score", "Percentage", "Grade");
        ConsoleUtil.printLine();

        int rank = 1;
        for (Result r : results) {
            System.out.printf("  %-4d %-22s %-10s %-10s %-8s%n",
                    rank++,
                    truncate(r.getStudentName(), 20),
                    r.getScore() + "/" + r.getTotalMarks(),
                    String.format("%.1f%%", r.getPercentage()),
                    r.getGrade());
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 11. Analytics Dashboard
    // ─────────────────────────────────────────────────────────
    private void analyticsBoard() {
        ConsoleUtil.printHeader("ANALYTICS DASHBOARD");

        List<Exam> exams    = examDAO.getAllExams();
        List<User> students = userDAO.getAllStudents();
        List<Result> allResults  = resultDAO.getAllResults();

        System.out.println("  " + ConsoleUtil.BOLD + "OVERVIEW" + ConsoleUtil.RESET);
        ConsoleUtil.printLine();
        System.out.println("  Total Exams    : " + exams.size());
        System.out.println("  Total Students : " + students.size());
        System.out.println("  Total Attempts : " + allResults.size());

        if (!allResults.isEmpty()) {
            double avgPct = allResults.stream().mapToDouble(Result::getPercentage).average().orElse(0);
            long passCount = allResults.stream().filter(Result::isPassed).count();
            System.out.printf("  Average Score  : %.2f%%%n", avgPct);
            System.out.printf("  Pass Rate      : %.1f%% (%d/%d)%n",
                    (passCount * 100.0 / allResults.size()), passCount, allResults.size());
        }

        ConsoleUtil.printLine();
        System.out.println("  " + ConsoleUtil.BOLD + "PER-EXAM STATS" + ConsoleUtil.RESET);
        ConsoleUtil.printLine();
        System.out.printf("  %-26s %-8s %-8s %-8s %-8s%n",
                "Exam", "Attempts", "Pass", "Fail", "Avg%");
        ConsoleUtil.printLine();

        for (Exam ex : exams) {
            List<Result> exResults = resultDAO.getResultsByExam(ex.getId());
            int[] pf = resultDAO.getPassFailCount(ex.getId());
            double avg = exResults.stream().mapToDouble(Result::getPercentage).average().orElse(0);
            System.out.printf("  %-26s %-8d %-8d %-8d %-8s%n",
                    truncate(ex.getTitle(), 24),
                    exResults.size(), pf[0], pf[1],
                    String.format("%.1f%%", avg));
        }

        ConsoleUtil.printLine();
        ConsoleUtil.printInfo("Top Performer:");
        allResults.stream()
                .max(Comparator.comparingDouble(Result::getPercentage))
                .ifPresent(r -> System.out.println("  " + ConsoleUtil.GREEN + ConsoleUtil.BOLD +
                        r.getStudentName() + " — " + String.format("%.1f%%", r.getPercentage()) +
                        " in " + r.getExamTitle() + ConsoleUtil.RESET));

        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 12. Add Subject
    // ─────────────────────────────────────────────────────────
    private void addSubject() {
        ConsoleUtil.printHeader("ADD SUBJECT");
        ConsoleUtil.printPrompt("Subject Name");
        String name = ConsoleUtil.readLine(sc);
        ConsoleUtil.printPrompt("Subject Code (e.g. CS101)");
        String code = ConsoleUtil.readLine(sc);
        ConsoleUtil.printPrompt("Description");
        String desc = ConsoleUtil.readLine(sc);

        Subject s = new Subject(0, name, code, desc);
        if (subjectDAO.addSubject(s))
            ConsoleUtil.printSuccess("Subject '" + name + "' added!");
        else
            ConsoleUtil.printError("Failed. Code may already exist.");
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // 13. View Subjects
    // ─────────────────────────────────────────────────────────
    private void viewSubjects() {
        ConsoleUtil.printHeader("ALL SUBJECTS");
        List<Subject> subjects = subjectDAO.getAllSubjects();

        if (subjects.isEmpty()) {
            ConsoleUtil.printInfo("No subjects found.");
            ConsoleUtil.pause(sc);
            return;
        }

        System.out.printf("  %-4s %-10s %-25s %-30s%n", "ID", "Code", "Name", "Description");
        ConsoleUtil.printLine();
        for (Subject s : subjects) {
            System.out.printf("  %-4d %-10s %-25s %-30s%n",
                    s.getId(), s.getCode(), s.getName(),
                    truncate(s.getDescription() == null ? "" : s.getDescription(), 28));
        }
        ConsoleUtil.pause(sc);
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────
    private void printExamPicker(List<Exam> exams) {
        System.out.printf("  %-4s %-5s %-28s %-8s%n", "No.", "ID", "Title", "Status");
        ConsoleUtil.printLine();
        int i = 1;
        for (Exam e : exams) {
            String status = e.isActive()
                    ? ConsoleUtil.GREEN + "Active" + ConsoleUtil.RESET
                    : ConsoleUtil.RED + "Inactive" + ConsoleUtil.RESET;
            System.out.printf("  %-4d %-5d %-28s %s%n",
                    i++, e.getId(), truncate(e.getTitle(), 26), status);
        }
        ConsoleUtil.printLine();
    }

    private String truncate(String s, int len) {
        if (s == null) return "";
        return s.length() <= len ? s : s.substring(0, len - 2) + "..";
    }
}
