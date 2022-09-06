import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        new ClockMain().run(); // To get rid of static requirements for methods that should not require the
                               // static keyword.
    }

    void run() throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();
        ClockInput in = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        Semaphore inputSemaphore = in.getSemaphore();
        clockMonitor monitor = new clockMonitor(0, 0, 0);

        Thread timeProgressionThread = new Thread(() -> {
            try {
                timeProgression(monitor, out);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timeProgressionThread.start();
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
    }

    void timeProgression(clockMonitor monitor, ClockOutput out) throws InterruptedException {
        long t0 = System.currentTimeMillis();
        int counter = 1;
        int[] time;
        while (true) {
            long now = System.currentTimeMillis();
            Thread.sleep(counter * 1000 - now + t0);
            time = monitor.getTime();
            time[2] = time[2] + 1;
            keepTrackOfTimePeriodicity(time);
            changeTime(time, monitor, out);
            keepTrackOfAlarm(time, monitor, out);
            counter++;
        }
    }

    /*
     * To handle the periodicity of a 24hr clock.
     */
    private void keepTrackOfTimePeriodicity(int[] time) {
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

    private void changeTime(int[] time, clockMonitor m, ClockOutput out) {
        out.displayTime(time[0], time[1], time[2]);
        m.setTime(time);
    }

    private void keepTrackOfAlarm(int[] time, clockMonitor m, ClockOutput out) {
        int timeDiff = timeComparison(time, m.getAlarmTime());
        if (0 <= timeDiff && timeDiff < 20 && m.getAlarmSet()) {
            out.alarm();
        }
    }

    private int timeComparison(int[] currentTime, int[] alarmTime) {
        return (currentTime[0] * 3600 + currentTime[1] * 60 + currentTime[2])
                - (alarmTime[0] * 3600 + alarmTime[1] * 60 + alarmTime[2]);
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
    private Lock lock = new ReentrantLock();

    clockMonitor(int h, int m, int s) {
        this.currentHour = h;
        this.currentMinute = m;
        this.currentSecond = s;
        alarmHour = 0;
        alarmMinute = 0;
        alarmSecond = 0;
        alarmSet = false;
    }

    public void setTime(int[] time) {
        lock.lock();
        currentHour = time[0];
        currentMinute = time[1];
        currentSecond = time[2];
        lock.unlock();
    }

    public void setAlarmTime(int[] time) {
        lock.lock();
        alarmHour = time[0];
        alarmMinute = time[1];
        alarmSecond = time[2];
        lock.unlock();
    }

    public int[] getTime() {
        lock.lock();
        int[] result = { currentHour, currentMinute, currentSecond };
        lock.unlock();
        return result;
    }

    public int[] getAlarmTime() {
        lock.lock();
        int[] result = { alarmHour, alarmMinute, alarmSecond };
        lock.unlock();
        return result;
    }

    public boolean toggleAlarm() {
        lock.lock();
        alarmSet = !alarmSet;
        lock.unlock();
        return alarmSet;
    }

    public boolean getAlarmSet() {
        return alarmSet; // Should not require a lock, since it only performs one memory access. Even if
                         // it happens concurrently with toggle alarm it will either act as though it
                         // happened before or after.
    }
}
