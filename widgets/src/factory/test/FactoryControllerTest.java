package factory.test;

import factory.simulation.Factory;
import factory.simulation.FactoryController;

/** Run concurrency test for FactoryController. */
public class FactoryControllerTest {
    public static void main(String[] args) {
        Factory.enableTestMode();
        try {
            FactoryController.main(args);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
