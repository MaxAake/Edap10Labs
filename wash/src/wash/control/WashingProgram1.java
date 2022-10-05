package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

/**
 * Program 1 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This program runs the following process:
 * Lock the hatch, let water into the machine, heat to 40◦C, keep the
 * temperature for 30 minutes, drain, rinse 5 times 2 minutes in cold water,
 * centrifuge for 5 minutes and unlock the hatch.
 * 
 * 
 */
public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;

    public WashingProgram1(WashingIO io,
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
            System.out.println("washing program 1 finished");
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
     * Lock the hatch.
     * Fill the machine halfways to full with water.
     * Heat to 40°C.
     * 
     */
    private void preConditions() throws InterruptedException {
        io.lock(true);
        System.out.println("setting WATER_FILL...");
        water.send(new WashingMessage(this, WATER_FILL));
        WashingMessage ackWaterFill = receive();
        System.out.println("washing program 1 got " + ackWaterFill);

        System.out.println("setting TEMP_40...");
        temp.send(new WashingMessage(this, TEMP_SET_40));
        WashingMessage ackTemperature = receive();
        System.out.println("washing program 1 got " + ackTemperature);

    }

    /*
     * Main wash for 30 minutes in 40°C water.
     * Drain the water.
     * Rinse 5 times for 2 minutes in cold water.
     * Centrifuge for 5 minutes.
     */

    private void duringConditions() throws InterruptedException {
        // 30 minutes of slow spin while keeping the temperature stable.
        mainWash();
        // Rinse 5 times for 2 minutes in cold water.
        rinse();
        // Centrifuge for 5 minutes.
        centrifuge();

    }

    private void mainWash() throws InterruptedException {
        System.out.println("setting SPIN_SLOW...");
        spin.send(new WashingMessage(this, SPIN_SLOW));
        WashingMessage ackSlowSpin = receive();
        System.out.println("washing program 1 got " + ackSlowSpin);

        Thread.sleep(30 * 60000 / Settings.SPEEDUP);

        // Turn off spinning. Lower the temperature, drain the water.
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

    private void rinse() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            System.out.println("setting WATER_FILL...");
            water.send(new WashingMessage(this, WATER_FILL));
            WashingMessage ackWaterFill = receive();
            System.out.println("washing program 1 got " + ackWaterFill);

            System.out.println("setting SPIN_SLOW...");
            spin.send(new WashingMessage(this, SPIN_SLOW));
            WashingMessage ackSlowSpin2 = receive();
            System.out.println("washing program 1 got " + ackSlowSpin2);

            Thread.sleep(2 * 60000 / Settings.SPEEDUP);
            System.out.println("setting SPIN_OFF...");
            spin.send(new WashingMessage(this, SPIN_OFF));
            WashingMessage ackNoSpin = receive();
            System.out.println("washing program 1 got " + ackNoSpin);

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
        System.out.println("washing program 1 got " + ack21);
        io.lock(false);

    }
}
