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
        ActorThread<WashingMessage> currentProg = null; // This will be instanced.

        while (true) {
            int n = io.awaitButton();
            System.out.println("user selected program " + n);
            switch (n) {
                case 1:
                    currentProg = new WashingProgram1(io, temp, water, spin);
                    currentProg.start();
                    break;
                case 2:
                    currentProg = new WashingProgram2(io, temp, water, spin);
                    currentProg.start();
                    break;
                case 3:
                    currentProg = new WashingProgram3(io, temp, water, spin);
                    currentProg.start();
                    break;
                case 0:
                    currentProg.interrupt();
                    break;
            }
        }
    }
};
