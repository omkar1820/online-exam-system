# 🎓 Online Examination System
**MCA 2nd Semester Java Project**  
Tech Stack: **Java 17+ | JDBC | MySQL 8.x**

---

## 📁 Project Structure

```
OnlineExamSystem/
├── sql/
│   └── schema.sql              ← Run this FIRST in MySQL
├── src/
│   ├── Main.java               ← Entry point
│   ├── config/
│   │   └── DBConnection.java   ← DB credentials here
│   ├── model/
│   │   ├── User.java
│   │   ├── Exam.java
│   │   ├── Question.java
│   │   ├── Result.java
│   │   └── Subject.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── ExamDAO.java
│   │   ├── QuestionDAO.java
│   │   ├── ResultDAO.java
│   │   └── SubjectDAO.java
│   ├── service/
│   │   └── ExamService.java
│   ├── ui/
│   │   ├── AuthUI.java
│   │   ├── AdminUI.java
│   │   └── StudentUI.java
│   └── util/
│       ├── ConsoleUtil.java
│       └── ExamTimer.java
└── README.md
```

---

## ⚙️ Setup Instructions

### Step 1 — MySQL Database
```sql
-- Open MySQL Workbench or terminal:
mysql -u root -p
source /path/to/OnlineExamSystem/sql/schema.sql
```

### Step 2 — Configure DB Password
Open `src/config/DBConnection.java` and set your MySQL password:
```java
private static final String PASSWORD = "your_mysql_password";
```

### Step 3 — Download MySQL JDBC Driver
Download: https://dev.mysql.com/downloads/connector/j/  
File: `mysql-connector-j-8.x.x.jar`

### Step 4 — Compile

**Using terminal (with JDBC jar):**
```bash
cd OnlineExamSystem

# Compile all .java files
javac -cp ".:lib/mysql-connector-j-8.x.x.jar" \
      -d out \
      src/config/*.java \
      src/model/*.java \
      src/dao/*.java \
      src/service/*.java \
      src/util/*.java \
      src/ui/*.java \
      src/Main.java
```

**Using IntelliJ IDEA (Recommended):**
1. Open project → File → Project Structure → Libraries → Add JAR → select MySQL connector
2. Mark `src/` as Sources Root
3. Right-click `Main.java` → Run

**Using Eclipse:**
1. New Java Project → Add src/ folder
2. Right-click project → Build Path → Add External JARs → select MySQL connector
3. Run `Main.java`

### Step 5 — Run
```bash
java -cp ".:out:lib/mysql-connector-j-8.x.x.jar" Main
```

---

## 🔑 Default Login Credentials

| Role    | Username    | Password    |
|---------|-------------|-------------|
| Admin   | admin       | admin123    |
| Student | john_doe    | student123  |
| Student | jane_smith  | student123  |
| Student | raj_kumar   | student123  |

---

## 🌟 Features

### Admin Panel
| Feature | Description |
|---|---|
| Create Exam | Title, subject, marks, duration, pass marks |
| Manage Questions | Add/View/Delete MCQ questions per exam |
| Toggle Exam Status | Activate or deactivate exams |
| View All Students | List of registered students |
| All Results | Every student's result across all exams |
| Exam-wise Report | Ranked results + pass/fail counts |
| Analytics Dashboard | Top performer, pass rate, avg scores |
| Subject Management | Add and list subjects |

### Student Panel
| Feature | Description |
|---|---|
| Available Exams | See exams not yet attempted |
| Take Exam | Timed MCQ exam with live countdown |
| Auto-submit | Exam submits when timer hits 00:00 |
| Result Card | Score, grade, progress bar, pass/fail |
| My Performance | All results with avg percentage |
| Attempted Exams | View already-taken exams |

---

## 📊 Grading System

| Grade | Percentage |
|-------|-----------|
| A+    | 90 – 100% |
| A     | 75 – 89%  |
| B     | 60 – 74%  |
| C     | 50 – 59%  |
| D     | 40 – 49%  |
| F     | Below 40% |

---

## 🛠️ Design Patterns Used

- **DAO Pattern** — Separates DB logic from business logic
- **Service Layer** — ExamService handles scoring & exam flow
- **MVC Structure** — model / dao+service / ui
- **Singleton** — DBConnection reuses one DB connection

---

## 📦 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| MySQL Connector/J | 8.x | JDBC driver for MySQL |
| Java SE | 17+ | Language (uses switch expressions, text blocks) |

---

*Developed as MCA 2nd Semester Java Project*
