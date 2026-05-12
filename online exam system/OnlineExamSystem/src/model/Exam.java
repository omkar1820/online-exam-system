package model;

public class Exam {
    private int    id;
    private String title;
    private int    subjectId;
    private String subjectName;
    private int    totalMarks;
    private int    durationMins;
    private int    passMarks;
    private boolean isActive;
    private int    createdBy;

    public Exam() {}

    public Exam(int id, String title, String subjectName, int totalMarks, int durationMins, int passMarks) {
        this.id          = id;
        this.title       = title;
        this.subjectName = subjectName;
        this.totalMarks  = totalMarks;
        this.durationMins = durationMins;
        this.passMarks   = passMarks;
        this.isActive    = true;
    }

    // Getters & Setters
    public int     getId()                      { return id; }
    public void    setId(int id)                { this.id = id; }

    public String  getTitle()                   { return title; }
    public void    setTitle(String t)           { this.title = t; }

    public int     getSubjectId()               { return subjectId; }
    public void    setSubjectId(int s)          { this.subjectId = s; }

    public String  getSubjectName()             { return subjectName; }
    public void    setSubjectName(String s)     { this.subjectName = s; }

    public int     getTotalMarks()              { return totalMarks; }
    public void    setTotalMarks(int m)         { this.totalMarks = m; }

    public int     getDurationMins()            { return durationMins; }
    public void    setDurationMins(int d)       { this.durationMins = d; }

    public int     getPassMarks()               { return passMarks; }
    public void    setPassMarks(int p)          { this.passMarks = p; }

    public boolean isActive()                   { return isActive; }
    public void    setActive(boolean a)         { this.isActive = a; }

    public int     getCreatedBy()               { return createdBy; }
    public void    setCreatedBy(int c)          { this.createdBy = c; }

    @Override
    public String toString() {
        return String.format("Exam[id=%d, title=%s, subject=%s, duration=%dmin]",
                id, title, subjectName, durationMins);
    }
}
