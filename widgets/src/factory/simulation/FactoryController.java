package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

public class FactoryController {

    public static void main(String[] args) throws InterruptedException {

        Factory factory = new Factory();

        Conveyor conveyor = factory.getConveyor();
        ConveyorMonitor monitor = new ConveyorMonitor();

        Tool press = factory.getPressTool();
        Tool paint = factory.getPaintTool();
        new Thread(() -> {
            while (true) {
                press.waitFor(Widget.GREEN_BLOB);
                monitor.blockPress();
                monitor.waitforConveyorStop();
                press.performAction();
                monitor.freePress();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                paint.waitFor(Widget.ORANGE_MARBLE);
                monitor.blockPainter();
                monitor.waitforConveyorStop();
                paint.performAction();
                monitor.freePainter();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                monitor.waitToStop();
                conveyor.off();
                monitor.toggleConveyor();
                monitor.waitToStart();
                conveyor.on();
                monitor.toggleConveyor();
            }
        }).start();
    }

    public static class ConveyorMonitor {
        private boolean blockingPress;
        private boolean blockingPainter;
        private boolean conveyorOn;

        public ConveyorMonitor() {
            blockingPress = false;
            blockingPainter = false;
            conveyorOn = true;
        }

        public synchronized void blockPress() {
            blockingPress = true;
            notifyAll();
        }

        public synchronized void blockPainter() {
            blockingPainter = true;
            notifyAll();
        }

        public synchronized void freePress() {
            blockingPress = false;
            notifyAll();
        }

        public synchronized void freePainter() {
            blockingPainter = false;
            notifyAll();
        }

        private synchronized boolean runConveyor() {
            return !blockingPainter && !blockingPress;
        }

        public synchronized void waitToStop() {
            while (this.runConveyor()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            notifyAll();
        }

        public synchronized void waitToStart() {
            while (!this.runConveyor()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        public synchronized void toggleConveyor() {
            conveyorOn = !conveyorOn;
        }

        public synchronized void waitforConveyorStop() {
            while (conveyorOn) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
