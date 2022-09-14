package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

public class FactoryController {

    public static void main(String[] args) {

        Factory factory = new Factory();

        Conveyor conveyor = factory.getConveyor();
        ConveyorMonitor monitor = new ConveyorMonitor();
        Tool press = factory.getPressTool();
        Tool paint = factory.getPaintTool();
        new Thread(() -> {
            while (true) {
                monitor.waitToStop(conveyor);
                monitor.waitToStart(conveyor);
            }
        }).start();

        new Thread(() -> {
            while (true) {
                press.waitFor(Widget.GREEN_BLOB);
                monitor.usingPress();
                monitor.waitforConveyorStop();
                press.performAction();
                monitor.freePress();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                paint.waitFor(Widget.ORANGE_MARBLE);
                monitor.usingPainter();
                monitor.waitforConveyorStop();
                paint.performAction();
                monitor.freePainter();
            }
        }).start();
    }

    public static class ConveyorMonitor {
        private boolean pressInUse;
        private boolean painterInUse;
        private boolean conveyorOn;

        public ConveyorMonitor() {
            pressInUse = false;
            painterInUse = false;
            conveyorOn = true;
        }

        public synchronized void usingPress() {
            pressInUse = true;
            notifyAll();
        }

        public synchronized void usingPainter() {
            painterInUse = true;
            notifyAll();
        }

        public synchronized void freePress() {
            pressInUse = false;
            notifyAll();
        }

        public synchronized void freePainter() {
            painterInUse = false;
            notifyAll();
        }

        private synchronized boolean runConveyor() {
            return !painterInUse && !pressInUse;
        }

        public synchronized void waitToStop(Conveyor con) {
            while (this.runConveyor()) {
                tryToWait();
            }
            con.off();
            conveyorOn = false;
            notifyAll();
        }

        public synchronized void waitToStart(Conveyor con) {
            while (!this.runConveyor()) {
               tryToWait();
            }
            con.on();
            conveyorOn = true;
            notifyAll();
        }

        public synchronized void waitforConveyorStop() {
            while (conveyorOn) {
            	tryToWait();
            }
        }
        private void tryToWait() {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
