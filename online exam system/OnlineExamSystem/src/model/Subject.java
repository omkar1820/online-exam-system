package model;

public class Subject {
    private int    id;
    private String name;
    private String code;
    private String description;

    public Subject() {}

    public Subject(int id, String name, String code, String description) {
        this.id          = id;
        this.name        = name;
        this.code        = code;
        this.description = description;
    }

    public int    getId()                    { return id; }
    public void   setId(int id)              { this.id = id; }

    public String getName()                  { return name; }
    public void   setName(String n)          { this.name = n; }

    public String getCode()                  { return code; }
    public void   setCode(String c)          { this.code = c; }

    public String getDescription()           { return description; }
    public void   setDescription(String d)   { this.description = d; }

    @Override
    public String toString() {
        return String.format("[%s] %s", code, name);
    }
}
