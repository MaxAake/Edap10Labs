
import lift.LiftView;
import lift.Passenger;
import lift.LiftMonitor;

public class ConcurrentLift {

    public static void main(String[] args) {

        final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;

        LiftView view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
        LiftMonitor monitor = new LiftMonitor(NBR_FLOORS, MAX_PASSENGERS);
        createPassengers(view, 20, monitor);
        Thread elevatorLift = new Thread(() -> {
            try {
                int[] floors;
                while (true) {
                    floors = monitor.getCurrentAndDestinationFloors(view);
                    if (floors[0] != floors[1]) {
                        view.moveLift(floors[0], floors[1]);
                    }
                    // monitor.moveLift(view);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        elevatorLift.start();
    }

    private static void createPassengers(LiftView view, int nPassengers, LiftMonitor monitor) {
        for (int i = 0; i < nPassengers; i++) {
            new Thread(() -> {
                try {
                    Passenger tmp = view.createPassenger();
                    int startFloor = tmp.getStartFloor();
                    int destinationFloor = tmp.getDestinationFloor();
                    tmp.begin(); // Walk in from left

                    monitor.waitForEntry(startFloor);
                    tmp.enterLift();
                    monitor.passengerEntry(startFloor, destinationFloor);

                    tmp.exitLift();
                    monitor.passengerExit(destinationFloor);
                    tmp.end();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}