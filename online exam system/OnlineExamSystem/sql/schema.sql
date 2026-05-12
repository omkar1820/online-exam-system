-- ============================================================
-- Online Examination System - Database Schema
-- MCA 2nd Semester Project
-- ============================================================

CREATE DATABASE IF NOT EXISTS online_exam_db;
USE online_exam_db;

-- Users Table (Admin + Students)
CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    role        ENUM('ADMIN','STUDENT') NOT NULL DEFAULT 'STUDENT',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subjects Table
CREATE TABLE IF NOT EXISTS subjects (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(20)  NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Exams Table
CREATE TABLE IF NOT EXISTS exams (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(150) NOT NULL,
    subject_id      INT NOT NULL,
    total_marks     INT NOT NULL DEFAULT 100,
    duration_mins   INT NOT NULL DEFAULT 60,
    pass_marks      INT NOT NULL DEFAULT 40,
    start_time      DATETIME,
    end_time        DATETIME,
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      INT NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Questions Table
CREATE TABLE IF NOT EXISTS questions (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    exam_id     INT NOT NULL,
    question    TEXT NOT NULL,
    option_a    VARCHAR(255) NOT NULL,
    option_b    VARCHAR(255) NOT NULL,
    option_c    VARCHAR(255) NOT NULL,
    option_d    VARCHAR(255) NOT NULL,
    correct_ans ENUM('A','B','C','D') NOT NULL,
    marks       INT NOT NULL DEFAULT 1,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
);

-- Exam Results Table
CREATE TABLE IF NOT EXISTS results (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    exam_id         INT NOT NULL,
    score           INT NOT NULL DEFAULT 0,
    total_marks     INT NOT NULL,
    percentage      DECIMAL(5,2),
    passed          BOOLEAN,
    attempted_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    time_taken_mins INT,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (exam_id)    REFERENCES exams(id),
    UNIQUE KEY unique_attempt (student_id, exam_id)
);

-- Student Answers Table (for detailed analytics)
CREATE TABLE IF NOT EXISTS student_answers (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    result_id       INT NOT NULL,
    question_id     INT NOT NULL,
    chosen_ans      ENUM('A','B','C','D'),
    is_correct      BOOLEAN,
    FOREIGN KEY (result_id)   REFERENCES results(id)   ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- ============================================================
-- Seed Data
-- ============================================================

-- Admin user (password: admin123)
INSERT INTO users (username, password, full_name, email, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@exam.com', 'ADMIN');

-- Sample students (password: student123)
INSERT INTO users (username, password, full_name, email, role) VALUES
('john_doe',   'student123', 'John Doe',   'john@student.com',  'STUDENT'),
('jane_smith', 'student123', 'Jane Smith', 'jane@student.com',  'STUDENT'),
('raj_kumar',  'student123', 'Raj Kumar',  'raj@student.com',   'STUDENT');

-- Subjects
INSERT INTO subjects (name, code, description) VALUES
('Data Structures',          'DS101',  'Arrays, Linked Lists, Trees, Graphs'),
('Database Management',      'DBMS102','SQL, Normalization, Transactions'),
('Object Oriented Programming','OOP103','Classes, Inheritance, Polymorphism'),
('Computer Networks',        'CN104',  'OSI, TCP/IP, Protocols');

-- Exams
INSERT INTO exams (title, subject_id, total_marks, duration_mins, pass_marks, is_active, created_by) VALUES
('Data Structures Mid-Term',    1, 50, 30, 20, TRUE, 1),
('DBMS Unit Test',              2, 40, 25, 16, TRUE, 1),
('OOP Fundamentals Quiz',       3, 30, 20, 12, TRUE, 1);

-- Questions for Exam 1 (DS Mid-Term)
INSERT INTO questions (exam_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(1,'What is the time complexity of Binary Search?','O(n)','O(log n)','O(n^2)','O(1)','B',2),
(1,'Which data structure uses LIFO principle?','Queue','Stack','Tree','Graph','B',2),
(1,'In a Binary Tree, the maximum nodes at level L is:','2^L','L^2','2L','L+1','A',2),
(1,'What is the worst-case time complexity of Quick Sort?','O(n log n)','O(n)','O(n^2)','O(log n)','C',2),
(1,'Which traversal visits root FIRST?','Inorder','Postorder','Preorder','Level Order','C',2),
(1,'A graph with no cycles is called:','Multigraph','Tree','DAG','Connected Graph','B',2),
(1,'Array index starts at __ in Java?','1','0','-1','Depends on JVM','B',2),
(1,'Linked List has O(1) time for:','Random Access','Search','Insertion at Head','Traversal','C',2),
(1,'Stack uses which end for operations?','Bottom','Top','Both','Middle','B',2),
(1,'Which sorting is stable by default?','Quick Sort','Heap Sort','Merge Sort','Selection Sort','C',2),
(1,'What is a deque?','Single-ended queue','Double-ended queue','Circular queue','Priority queue','B',2),
(1,'Depth First Search uses:','Queue','Stack','Array','Heap','B',2),
(1,'AVL Tree maintains:','Heap property','Balanced Height','BST property only','Color property','B',2),
(1,'A complete binary tree has max keys at:','Root','Leaf level','Second level','Last level','D',2),
(1,'Hashing resolves collision using:','Sorting','Probing','Merging','Splitting','B',2),
(1,'Which is NOT a linear data structure?','Array','Stack','Queue','Tree','D',2),
(1,'BFS uses which data structure?','Stack','Queue','Heap','Tree','B',2),
(1,'Recursion uses __ internally?','Queue','Array','Stack','Heap','C',2),
(1,'Postfix expression evaluation uses:','Queue','Stack','Tree','Graph','B',2),
(1,'Priority Queue is best implemented using:','Array','Linked List','Heap','Stack','C',2),
(1,'Circular linked list last node points to:','NULL','Itself','First node','Last node','C',2),
(1,'Which operation is O(n) in array?','Access','Update','Insert at middle','None','C',2),
(1,'Minimum spanning tree uses:','DFS only','BFS only','Kruskal or Prim','Hash table','C',2),
(1,'B-Tree is used in:','RAM','Disk-based DB','Registers','Cache','B',2),
(1,'Trie is used for:','Sorting','String search','Graph traversal','Hashing','B',2);

-- Questions for Exam 2 (DBMS)
INSERT INTO questions (exam_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(2,'SQL stands for?','Standard Query Language','Structured Query Language','Simple Query Language','Sequential Query Language','B',2),
(2,'Which normal form removes partial dependency?','1NF','2NF','3NF','BCNF','B',2),
(2,'PRIMARY KEY cannot have:','Duplicates','NULL values','Both A and B','Indexes','C',2),
(2,'ACID stands for?','Atomicity Consistency Isolation Durability','All Commands in Database','Automated Commit Index Deadlock','None','A',2),
(2,'Which JOIN returns all rows from both tables?','INNER JOIN','LEFT JOIN','FULL OUTER JOIN','CROSS JOIN','C',2),
(2,'DDL stands for?','Data Definition Language','Data Derived Language','Data Delete Language','None','A',2),
(2,'GROUP BY is used with:','WHERE','HAVING','ORDER BY','SELECT only','B',2),
(2,'Which command removes a table completely?','DELETE','TRUNCATE','DROP','REMOVE','C',2),
(2,'Foreign key references a:','Primary key in another table','Any column','INDEX','Unique column only','A',2),
(2,'Transaction ROLLBACK means:','Save changes','Undo changes','Delete table','Commit changes','B',2),
(2,'VIEW in SQL is a:','Physical table','Virtual table','Index','Procedure','B',2),
(2,'3NF removes:','Partial dependency','Transitive dependency','Multi-valued dependency','None','B',2),
(2,'COUNT(*) counts:','Only non-null','All rows including NULL','Only distinct','None','B',2),
(2,'Which is a DCL command?','SELECT','GRANT','CREATE','INSERT','B',2),
(2,'ER Diagram stands for?','Entity Relationship','Exact Record','Error Recovery','None','A',2),
(2,'BETWEEN is used for:','Pattern match','Range check','NULL check','Join','B',2),
(2,'LIKE pattern _ means:','Zero chars','Any single char','Any string','Digit only','B',2),
(2,'Deadlock occurs when:','Two users delete same row','Circular wait for resources','Server crashes','Query is slow','B',2),
(2,'INDEX improves:','Insertion speed','Query speed','Update speed','Storage','B',2),
(2,'Normalization goal is to:','Increase redundancy','Remove redundancy','Add more tables','Reduce performance','B',2);

-- Questions for Exam 3 (OOP)
INSERT INTO questions (exam_id, question, option_a, option_b, option_c, option_d, correct_ans, marks) VALUES
(3,'OOP stands for?','Object Oriented Programming','Optional Operator Program','Object Override Protocol','None','A',2),
(3,'Encapsulation means:','Hiding data','Multiple inheritance','Method overloading','Polymorphism','A',2),
(3,'Which keyword is used for inheritance in Java?','implements','extends','inherits','super','B',2),
(3,'Constructor has:','Return type void','Same name as class','Static keyword','None','B',2),
(3,'Overriding is resolved at:','Compile time','Runtime','Link time','Load time','B',2),
(3,'Abstract class can have:','Only abstract methods','Both abstract and concrete methods','Only constructors','Only static methods','B',2),
(3,'Interface in Java supports:','Single inheritance only','Multiple inheritance','No inheritance','Abstract only','B',2),
(3,'super keyword refers to:','Current class','Parent class','Interface','Static method','B',2),
(3,'final class cannot be:','Instantiated','Extended','Used','Compiled','B',2),
(3,'Which is not a pillar of OOP?','Encapsulation','Compilation','Polymorphism','Inheritance','B',2),
(3,'Static method belongs to:','Object','Class','Interface only','Abstract class','B',2),
(3,'Method overloading uses different:','Return types','Method names','Parameter lists','Access modifiers','C',2),
(3,'this keyword refers to:','Parent class object','Current class object','Static context','Interface','B',2),
(3,'Garbage collection handles:','Memory allocation','Memory deallocation','Compilation','Threading','B',2),
(3,'Which access modifier is most restrictive?','public','protected','default','private','D',2);

SELECT 'Database setup complete!' AS Status;
