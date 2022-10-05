package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

    private WashingIO io;
    final private double lowerMargin = 0.3;
    final private double upperMargin = 0.68;
    private Order tempSetting;
    private ActorThread<WashingMessage> lastSender;
    private boolean heating;

    public TemperatureController(WashingIO io) {
        this.io = io;
        tempSetting = Order.TEMP_IDLE;
        heating = false;
    }

    @Override
    public void run() {
        while (true) {

            try {
                WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    tempSetting = m.getOrder();
                    lastSender = m.getSender();
                    System.out.println("got " + m);
                }

                switch (tempSetting) {
                    case TEMP_IDLE:
                        io.heat(false);
                        heating = false;
                        if (lastSender != null) {
                            lastSender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                            lastSender = null;
                        }
                        break;
                    case TEMP_SET_40:
                        setState(40);
                        break;
                    case TEMP_SET_60:
                        setState(60);
                        break;
                    default:
                        throw new Error("Unknown order in TemperatureController");

                }
            } catch (InterruptedException e) {
                System.out.println("error in temp");
            }
        }
    }

    private void setState(int temperature) {
        if (io.getTemperature() > temperature - upperMargin && heating) {
            io.heat(false);
            heating = false;
            if (lastSender != null) {
                lastSender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                lastSender = null;
            }
        } else if (io.getTemperature() < temperature - 2 + lowerMargin && !heating) {
            io.heat(true);
            heating = true;
        }
    }
}
