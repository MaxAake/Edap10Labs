package lift;

enum Direction {
    UP,
    DOWN
}

public class LiftMonitor {
    private int[] toEnter;
    private int[] toExit;
    private int currentFloor;
    private boolean isMoving;
    private int passengersInLift;
    private Direction direction;
    private int numberOfFloors;

    public LiftMonitor(int numberOfFloors) {
        toEnter = new int[numberOfFloors];
        toExit = new int[numberOfFloors];
        this.numberOfFloors = numberOfFloors;
        currentFloor = 0;
        isMoving = false;
        passengersInLift = 0;
        direction = Direction.UP;
    }

    public synchronized void waitForLift(int floor, boolean entry) throws InterruptedException {
        if (entry) {
            toEnter[floor]++;
        } else {
            toExit[floor]++;
        }
        while (currentFloor != floor || isMoving) {
            wait();
        }
    }

    public synchronized int[] getCurrentAndDestinationFloors() {
        int destinationFloor;
        int temp = currentFloor;
        handleDirection();
        if (direction == Direction.UP) {
            destinationFloor = ++currentFloor;
        } else {
            destinationFloor = --currentFloor;
        }
        int[] result = { temp, destinationFloor };
        return result;
    }

    private void handleDirection() {
        if (currentFloor == numberOfFloors - 1)
            direction = Direction.DOWN;
        if (currentFloor == 0)
            direction = Direction.UP;
    }

    public synchronized void moveLift(LiftView view) throws InterruptedException {
        isMoving = false;
        view.openDoors(currentFloor);
        waitForPassengerEntryAndExit();
        view.closeDoors();
        isMoving = true;
    }

    private void waitForPassengerEntryAndExit() throws InterruptedException {
        while (toEnter[currentFloor] > 0 || toExit[currentFloor] > 0) {
            System.out.println("toEnter: " + toEnter[currentFloor] + "\ntoExit: " + toExit[currentFloor]);
            notifyAll();
            wait();
        }
    }

    public synchronized void passengerEntry(int startFloor, int destinationFloor) {
        passengersInLift++;
        toEnter[startFloor]--;
        notifyAll();
    }

    public synchronized void passengerExit(int destinationFloor) {
        passengersInLift--;
        toExit[destinationFloor]--;
        notifyAll();
    }
}
