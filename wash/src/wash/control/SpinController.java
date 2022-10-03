package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

    public enum spinState {
        SPINNING_RIGHT,
        SPINNING_LEFT,
        SPINNING_FAST,
        NOT_SPINNING
    }

    private WashingIO io;
    public Order spinSetting;
    private spinState state;
    private ActorThread<WashingMessage> lastSender;

    public SpinController(WashingIO io) {
        this.io = io;
        state = spinState.NOT_SPINNING;
        spinSetting = Order.SPIN_OFF;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    spinSetting = m.getOrder();
                    lastSender = m.getSender();
                    System.out.println("got " + m);
                }

                switch (spinSetting) {
                    case SPIN_SLOW:
                        spinningSlowly();
                        break;
                    case SPIN_FAST:
                        io.setSpinMode(WashingIO.SPIN_FAST);
                        state = spinState.SPINNING_FAST;
                        break;
                    case SPIN_OFF:
                        io.setSpinMode(WashingIO.SPIN_IDLE);
                        state = spinState.NOT_SPINNING;
                        break;
                    default:
                        throw new Error("Unknown order in SpinController");
                }

                if (m != null) {
                    lastSender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                }
                // ... TODO ...
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }

    private void spinningSlowly() {
        switch (state) {
            case SPINNING_LEFT:
                io.setSpinMode(WashingIO.SPIN_RIGHT);
                state = spinState.SPINNING_RIGHT;
                break;
            case SPINNING_RIGHT:
                io.setSpinMode(WashingIO.SPIN_LEFT);
                state = spinState.SPINNING_LEFT;
                break;
            case SPINNING_FAST:
                io.setSpinMode(WashingIO.SPIN_RIGHT);
                state = spinState.SPINNING_RIGHT;
                break;
            case NOT_SPINNING:
                io.setSpinMode(WashingIO.SPIN_RIGHT);
                state = spinState.SPINNING_RIGHT;
                break;
        }
    }
}
