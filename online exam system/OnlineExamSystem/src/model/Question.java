package model;

public class Question {
    private int    id;
    private int    examId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private char   correctAns; // A, B, C, D
    private int    marks;

    public Question() {}

    public Question(int id, int examId, String question,
                    String a, String b, String c, String d,
                    char correctAns, int marks) {
        this.id         = id;
        this.examId     = examId;
        this.question   = question;
        this.optionA    = a;
        this.optionB    = b;
        this.optionC    = c;
        this.optionD    = d;
        this.correctAns = correctAns;
        this.marks      = marks;
    }

    // Getters & Setters
    public int    getId()                    { return id; }
    public void   setId(int id)              { this.id = id; }

    public int    getExamId()                { return examId; }
    public void   setExamId(int e)           { this.examId = e; }

    public String getQuestion()              { return question; }
    public void   setQuestion(String q)      { this.question = q; }

    public String getOptionA()               { return optionA; }
    public void   setOptionA(String a)       { this.optionA = a; }

    public String getOptionB()               { return optionB; }
    public void   setOptionB(String b)       { this.optionB = b; }

    public String getOptionC()               { return optionC; }
    public void   setOptionC(String c)       { this.optionC = c; }

    public String getOptionD()               { return optionD; }
    public void   setOptionD(String d)       { this.optionD = d; }

    public char   getCorrectAns()            { return correctAns; }
    public void   setCorrectAns(char c)      { this.correctAns = c; }

    public int    getMarks()                 { return marks; }
    public void   setMarks(int m)            { this.marks = m; }

    /** Returns option text for a given letter (A/B/C/D) */
    public String getOptionText(char letter) {
        return switch (Character.toUpperCase(letter)) {
            case 'A' -> optionA;
            case 'B' -> optionB;
            case 'C' -> optionC;
            case 'D' -> optionD;
            default  -> "Unknown";
        };
    }

    @Override
    public String toString() {
        return String.format("Q[%d]: %s", id, question);
    }
}
