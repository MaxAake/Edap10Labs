import java.util.concurrent.Semaphore;
import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();
        ClockInput in = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        Semaphore inputSemaphore = in.getSemaphore();
        clockMonitor monitor = new clockMonitor(0, 0, 0);
        Thread t1 = new Thread(() -> {
            try {
                timeProgression(monitor, out);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> { // Not sure if we need this thread, might be removed later.
            try {

                while (true) {
                    inputSemaphore.acquire();
                    UserInput userInput = in.getUserInput();
                    int choice = userInput.getChoice();
                    int h = userInput.getHours();
                    int m = userInput.getMinutes();
                    int s = userInput.getSeconds();
                    int[] time = { h, m, s };
                    switch (choice) {
                        case 1: // If the user is setting a new clock time.
                            changeTime(time, monitor, out);
                            break;

                        case 2: // If the user is setting a new alarm time.
                            monitor.setAlarmTime(time);
                            break;

                        case 3: // If the user wants to toggle the alarm.
                            out.setAlarmIndicator(monitor.toggleAlarm());
                            break;
                    }
                    System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
        t1.start();
        t2.start();
    }

    static void timeProgression(clockMonitor monitor, ClockOutput out) throws InterruptedException {
        long t0 = System.currentTimeMillis();
        int counter = 1;
        while (true) {
            long now = System.currentTimeMillis();
            Thread.sleep(counter*1000 - now + t0);
            int[] time = monitor.getTime();
            time[2] = time[2] + 1;
            keepTrackOfTimePeriodicity(time);
            changeTime(time, monitor, out);
            counter++;
        }
    }

    /*
     * To handle the periodicity of a 24hr clock.
     */
    private static void keepTrackOfTimePeriodicity(int[] time) {
        if (time[2] == 60) {
            time[1]++;
            time[2] = 0;
        }
        if (time[1] == 60) {
            time[0]++;
            time[1] = 0;
        }
        if (time[0] == 24) {
            time[0] = 0;
        }
    }

    private static void changeTime(int[] time, clockMonitor m, ClockOutput out) {
        out.displayTime(time[0], time[1], time[2]);
        m.setTime(time);
    }
}

class clockMonitor {
    private int currentHour;
    private int currentMinute;
    private int currentSecond;
    private int alarmHour;
    private int alarmMinute;
    private int alarmSecond;
    private boolean alarmSet;

    /*
     * 
     */
    clockMonitor(int h, int m, int s) {
        this.currentHour = h;
        this.currentMinute = m;
        this.currentSecond = s;
        alarmHour = 0;
        alarmMinute = 0;
        alarmSecond = 0;
        alarmSet = false;
    }

    public synchronized void setTime(int[] time) {
        currentHour = time[0];
        currentMinute = time[1];
        currentSecond = time[2];
    }

    public synchronized void setAlarmTime(int[] time) {
        alarmHour = time[0];
        alarmMinute = time[1];
        alarmSecond = time[2];
    }

    public synchronized int[] getTime() {
        int[] result = { currentHour, currentMinute, currentSecond };
        return result;
    }

    public boolean toggleAlarm() {
        alarmSet = !alarmSet;
        return alarmSet;
    }
}
