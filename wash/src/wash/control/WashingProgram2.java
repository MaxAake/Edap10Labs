package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram2 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;

    public WashingProgram2(WashingIO io,
            ActorThread<WashingMessage> temp,
            ActorThread<WashingMessage> water,
            ActorThread<WashingMessage> spin) {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }

    @Override
    public void run() {
        try {
            preConditions();

            duringConditions();

            postConditions();
        } catch (InterruptedException e) {

            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle
            temp.send(new WashingMessage(this, TEMP_IDLE));
            water.send(new WashingMessage(this, WATER_IDLE));
            spin.send(new WashingMessage(this, SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }

    /*
     * Lock and prewash for 20 minutes in 40°C water.
     * Drain water for main wash.
     */
    private void preConditions() throws InterruptedException {
        io.lock(true);
        wash(Order.TEMP_SET_40, 20);

    }

    /*
     * Main wash for 30 minutes in 60°C water.
     * Drain the water.
     * Rinse 5 times for 2 minutes in cold water.
     * Centrifuge for 5 minutes.
     */

    private void duringConditions() throws InterruptedException {
        wash(Order.TEMP_SET_60, 30);
        rinse();
        centrifuge();

    }

    private void rinse() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            System.out.println("setting WATER_FILL...");
            water.send(new WashingMessage(this, WATER_FILL));
            WashingMessage ackZ1 = receive();
            System.out.println("washing program 1 got " + ackZ1);

            System.out.println("setting SPIN_SLOW...");
            spin.send(new WashingMessage(this, SPIN_SLOW));
            WashingMessage ack11 = receive();
            System.out.println("washing program 1 got " + ack11);

            Thread.sleep(2 * 60000 / Settings.SPEEDUP);

            System.out.println("setting SPIN_OFF...");
            spin.send(new WashingMessage(this, SPIN_OFF));
            WashingMessage ack21 = receive();
            System.out.println("washing program 1 got " + ack21);

            System.out.println("setting WATER_DRAIN...");
            water.send(new WashingMessage(this, WATER_DRAIN));
            WashingMessage ackEmpty1 = receive();
            System.out.println("washing program 1 got " + ackEmpty1);
        }
    }

    private void centrifuge() throws InterruptedException {
        System.out.println("setting SPIN_FAST...");
        spin.send(new WashingMessage(this, SPIN_FAST));
        WashingMessage ackCent = receive();
        System.out.println("washing program 1 got " + ackCent);

        Thread.sleep(5 * 60000 / Settings.SPEEDUP);

    }

    private void postConditions() throws InterruptedException {
        System.out.println("setting SPIN_OFF...");
        spin.send(new WashingMessage(this, SPIN_OFF));
        WashingMessage ack21 = receive();
        System.out.println("washing program 2 got " + ack21);

        io.lock(false);

        System.out.println("washing program 2 finished");

    }

    private void wash(Order temperature, int time) throws InterruptedException {
        System.out.println("setting WATER_FILL...");
        water.send(new WashingMessage(this, WATER_FILL));
        WashingMessage ackWaterFill = receive();
        System.out.println("washing program 1 got " + ackWaterFill);

        System.out.println("setting Temperature...");
        temp.send(new WashingMessage(this, temperature));
        WashingMessage ackTemp = receive();
        System.out.println("washing program 1 got " + ackTemp);

        System.out.println("setting SPIN_SLOW...");
        spin.send(new WashingMessage(this, SPIN_SLOW));
        WashingMessage ack1 = receive();
        System.out.println("washing program 1 got " + ack1);

        Thread.sleep(time * 60000 / Settings.SPEEDUP);
        System.out.println("setting SPIN_OFF...");
        spin.send(new WashingMessage(this, SPIN_OFF));
        WashingMessage ack2 = receive();
        System.out.println("washing program 1 got " + ack2);

        System.out.println("setting TEMP_IDLE...");
        temp.send(new WashingMessage(this, TEMP_IDLE));
        WashingMessage ackCool = receive();
        System.out.println("washing program 1 got " + ackCool);

        System.out.println("setting WATER_DRAIN...");
        water.send(new WashingMessage(this, WATER_DRAIN));
        WashingMessage ackEmpty = receive();
        System.out.println("washing program 1 got " + ackEmpty);
    }
}
