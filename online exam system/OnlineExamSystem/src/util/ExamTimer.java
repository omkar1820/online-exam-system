package util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Exam countdown timer — runs on a background thread.
 * Shows a warning when 5 minutes remain.
 */
public class ExamTimer {

    private final int           totalSeconds;
    private       int           secondsLeft;
    private final AtomicBoolean running  = new AtomicBoolean(false);
    private final AtomicBoolean timeUp   = new AtomicBoolean(false);
    private       Thread        timerThread;
    private       long          startTime;

    public ExamTimer(int durationMins) {
        this.totalSeconds = durationMins * 60;
        this.secondsLeft  = totalSeconds;
    }

    public void start() {
        running.set(true);
        startTime = System.currentTimeMillis();
        timerThread = new Thread(() -> {
            while (running.get() && secondsLeft > 0) {
                try {
                    Thread.sleep(1000);
                    secondsLeft--;

                    if (secondsLeft == 300) {
                        System.out.println(ConsoleUtil.YELLOW +
                            "\n  ⚠  WARNING: 5 minutes remaining!" + ConsoleUtil.RESET);
                    }
                    if (secondsLeft == 60) {
                        System.out.println(ConsoleUtil.RED +
                            "\n  ⚠  WARNING: 1 minute remaining!" + ConsoleUtil.RESET);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            if (secondsLeft <= 0) {
                timeUp.set(true);
                System.out.println(ConsoleUtil.RED + ConsoleUtil.BOLD +
                    "\n\n  ⏰  TIME IS UP! Auto-submitting your exam..." + ConsoleUtil.RESET);
            }
            running.set(false);
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    public void stop() {
        running.set(false);
        if (timerThread != null) timerThread.interrupt();
    }

    public boolean isTimeUp()  { return timeUp.get(); }
    public boolean isRunning() { return running.get(); }

    public String getTimeRemaining() {
        int mins = secondsLeft / 60;
        int secs = secondsLeft % 60;
        String color = secondsLeft < 300 ? ConsoleUtil.RED : ConsoleUtil.GREEN;
        return color + String.format("⏱  %02d:%02d remaining", mins, secs) + ConsoleUtil.RESET;
    }

    /** Returns elapsed time in minutes (rounded) */
    public int getElapsedMins() {
        long elapsed = System.currentTimeMillis() - startTime;
        return (int) Math.ceil(elapsed / 60000.0);
    }
}
