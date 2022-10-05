package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

    private WashingIO io;
    private Order waterSetting;
    private ActorThread<WashingMessage> lastSender;
    private final double waterAdequateLevel = 20 / 2;

    public WaterController(WashingIO io) {
        this.io = io;
        waterSetting = Order.WATER_IDLE;
    }

    @Override
    public void run() {
        while (true) {

            try {
                WashingMessage m = receiveWithTimeout(1000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    waterSetting = m.getOrder();
                    lastSender = m.getSender();
                    System.out.println("got " + m);
                }

                switch (waterSetting) {
                    case WATER_IDLE:
                        io.fill(false);
                        io.drain(false);
                        break;
                    case WATER_FILL:
                        if (io.getWaterLevel() < waterAdequateLevel) {
                            io.drain(false);
                            io.fill(true);
                        } else {
                            io.fill(false);
                            if (lastSender != null) {
                                lastSender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                                lastSender = null;
                            }
                            waterSetting = Order.WATER_IDLE;
                        }
                        break;
                    case WATER_DRAIN:
                        io.fill(false);
                        io.drain(true);
                        if (io.getWaterLevel() == 0) {
                            if (lastSender != null) {
                                lastSender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                                lastSender = null;
                            }
                        }
                        break;
                    default:
                        throw new Error("Unknown order in WaterController");

                }

            } catch (InterruptedException e) {
                System.out.println("error in water");
            }
        }
    }
}
