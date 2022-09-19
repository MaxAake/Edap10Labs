
import lift.LiftView;
import lift.Passenger;
import lift.LiftMonitor;

public class ConcurrentLift {

    public static void main(String[] args) {

        final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;

        LiftView view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
        LiftMonitor monitor = new LiftMonitor(NBR_FLOORS);
        createPassengers(view, 10, monitor);
        Thread elevatorLift = new Thread(() -> {
            try {
                while (true) {
                    int[] floors = monitor.getCurrentAndDestinationFloors();
                    view.moveLift(floors[0], floors[1]);
                    monitor.moveLift(view);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        elevatorLift.start();
        /**
         * 
         * pass.begin(); // walk in (from left)
         * if (fromFloor != 0) {
         * view.moveLift(0, fromFloor);
         * }
         * view.openDoors(fromFloor);
         * pass.enterLift(); // step inside
         * 
         * view.closeDoors();
         * view.moveLift(fromFloor, toFloor); // ride lift
         * view.openDoors(toFloor);
         * 
         * pass.exitLift(); // leave lift
         * pass.end(); // walk out (to the right)
         */
    }

    private static void createPassengers(LiftView view, int number, LiftMonitor monitor) {
        for (int i = 0; i < number; i++) {
            new Thread(() -> {
                try {
                    Passenger tmp = view.createPassenger();
                    int startFloor = tmp.getStartFloor();
                    int destinationFloor = tmp.getDestinationFloor();
                    tmp.begin(); // Walk in from left
                    monitor.waitForLift(startFloor, true);
                    tmp.enterLift();
                    monitor.passengerEntry(startFloor, destinationFloor);
                    monitor.waitForLift(destinationFloor, false);
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