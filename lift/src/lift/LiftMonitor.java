package lift;

import java.util.concurrent.Semaphore;

public class LiftMonitor {
    private int[] toEnter;
    private int[] toExit;
    private int currentFloor;
    private boolean isMoving;
    private int maxPassengers;
    private int passengersInLift;
    private int numberOfFloors;
    private Semaphore entering;
    int direction;

    public LiftMonitor(int numberOfFloors, int maxPassengers) {
        toEnter = new int[numberOfFloors];
        toExit = new int[numberOfFloors];

        this.numberOfFloors = numberOfFloors;
        currentFloor = 0;
        isMoving = true;
        direction = 1;

        this.maxPassengers = maxPassengers;
        passengersInLift = 0;
        entering = new Semaphore(maxPassengers);
    }

    public synchronized void waitForEntry(int floor) throws InterruptedException {
        toEnter[floor]++;
        notifyAll();
        while (currentFloor != floor || isMoving || maxPassengers == passengersInLift) {
            wait();
        }
        passengersInLift++;
        entering.acquire();
    }

    public synchronized void waitForExit(int floor) throws InterruptedException {
        toExit[floor]++;
        while (currentFloor != floor || isMoving) {
            wait();
        }
        passengersInLift--;
    }

    private void waitForPassengers() throws InterruptedException {
        boolean passengers = checkPassengers();
        while (!passengers) {
            wait();
            passengers = checkPassengers();
        }
    }

    private boolean checkPassengers() {
        boolean passengers = passengersInLift > 0;
        for (int i = 0; i < toEnter.length; i++) {
            if (toEnter[i] != 0) {
                passengers = true;
            }
        }
        return passengers;
    }

    public synchronized int[] getCurrentAndDestinationFloors(LiftView view) throws InterruptedException {
        waitForPassengers();
        handleDirection();
        moveLift(view);
        if (!checkPassengers()) {
            int[] result = { currentFloor, currentFloor };
            return result;
        }
        int temp = currentFloor;
        currentFloor = currentFloor + direction;
        int[] result = { temp, currentFloor };
        return result;
    }

    private void handleDirection() {
        if (currentFloor == numberOfFloors - 1) {
            direction = -1;
        }
        if (currentFloor == 0) {
            direction = 1;
        }
    }

    private void moveLift(LiftView view) throws InterruptedException {
        isMoving = false;
        if (toEnter[currentFloor] > 0 || toExit[currentFloor] > 0) {
            view.openDoors(currentFloor);
            waitForPassengerEntryAndExit();
            view.closeDoors();
        }
        isMoving = true;
    }

    private void waitForPassengerEntryAndExit() throws InterruptedException {
        notifyAll();
        while (isWaitingAndSpaceExists() || toExit[currentFloor] > 0 || entering.availablePermits() < 4) {
            wait();
        }
    }

    private boolean isWaitingAndSpaceExists() {
        return toEnter[currentFloor] > 0 && passengersInLift < maxPassengers;
    }

    public synchronized void passengerEntry(int startFloor, int destinationFloor) throws InterruptedException {
        toEnter[startFloor]--;
        entering.release();
        notifyAll();
        waitForExit(destinationFloor);
    }

    public synchronized void passengerExit(int destinationFloor) {
        toExit[destinationFloor]--;
        notifyAll();
    }
}
