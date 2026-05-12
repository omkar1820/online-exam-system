package model;

public class Result {
    private int     id;
    private int     studentId;
    private String  studentName;
    private int     examId;
    private String  examTitle;
    private int     score;
    private int     totalMarks;
    private double  percentage;
    private boolean passed;
    private int     timeTakenMins;
    private String  attemptedAt;

    public Result() {}

    // Getters & Setters
    public int     getId()                      { return id; }
    public void    setId(int id)                { this.id = id; }

    public int     getStudentId()               { return studentId; }
    public void    setStudentId(int s)          { this.studentId = s; }

    public String  getStudentName()             { return studentName; }
    public void    setStudentName(String n)     { this.studentName = n; }

    public int     getExamId()                  { return examId; }
    public void    setExamId(int e)             { this.examId = e; }

    public String  getExamTitle()               { return examTitle; }
    public void    setExamTitle(String t)       { this.examTitle = t; }

    public int     getScore()                   { return score; }
    public void    setScore(int s)              { this.score = s; }

    public int     getTotalMarks()              { return totalMarks; }
    public void    setTotalMarks(int t)         { this.totalMarks = t; }

    public double  getPercentage()              { return percentage; }
    public void    setPercentage(double p)      { this.percentage = p; }

    public boolean isPassed()                   { return passed; }
    public void    setPassed(boolean p)         { this.passed = p; }

    public int     getTimeTakenMins()           { return timeTakenMins; }
    public void    setTimeTakenMins(int t)      { this.timeTakenMins = t; }

    public String  getAttemptedAt()             { return attemptedAt; }
    public void    setAttemptedAt(String a)     { this.attemptedAt = a; }

    public String getGrade() {
        if (percentage >= 90) return "A+";
        if (percentage >= 75) return "A";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }

    @Override
    public String toString() {
        return String.format("Result[student=%s, exam=%s, score=%d/%d, grade=%s]",
                studentName, examTitle, score, totalMarks, getGrade());
    }
}
