import config.DBConnection;
import ui.AuthUI;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║       ONLINE EXAMINATION SYSTEM                          ║
 * ║       MCA 2nd Semester Java Project                      ║
 * ║       Tech Stack: Java + JDBC + MySQL                    ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Features:
 *  - Admin: Create exams, manage questions, view analytics
 *  - Student: Take timed exams, view results, track performance
 *  - Real-time countdown timer with warnings
 *  - Grade calculation (A+, A, B, C, D, F)
 *  - Pass/Fail tracking and analytics dashboard
 */
public class Main {
    public static void main(String[] args) {
        // Verify DB connection on startup
        if (DBConnection.getConnection() == null) {
            System.err.println("FATAL: Cannot connect to database. Check DBConnection.java settings.");
            System.exit(1);
        }

        // Launch the application
        try {
            new AuthUI().start();
        } finally {
            DBConnection.closeConnection();
        }
    }
}
