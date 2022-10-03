package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

        WashingIO io = sim.startSimulation();

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();
        WashingProgram1 prog1 = new WashingProgram1(io, temp, water, spin);
        // WashingProgram2 prog2 = new WashingProgram2(io, temp, water, spin);
        WashingProgram3 prog3 = new WashingProgram3(io, temp, water, spin);
        ActorThread<WashingMessage> currentProg = null; // This will be instanced. Probably.

        while (true) {
            int n = io.awaitButton();
            System.out.println("user selected program " + n);
            switch (n) {
                case 1:
                    currentProg = prog1;
                    prog1.run();
                    break;
                case 2:
                    break;
                case 3:
                    currentProg = prog3;
                    prog3.run();
                    break;
                case 0:
                    currentProg.interrupt();
                    break;
            }
            // TODO:
            // if the user presses buttons 1-3, start a washing program
            // if the user presses button 0, and a program has been started, stop it
        }
    }
};
