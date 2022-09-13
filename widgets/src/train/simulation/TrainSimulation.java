package train.simulation;

import train.model.Train;
import train.model.TrainMonitor;
import train.view.TrainView;

public class TrainSimulation {

	public static void main(String[] args) {
		new TrainSimulation().run(); // Getting rid of the static requirements.
	}

	void run() {
		TrainView view = new TrainView();
		TrainMonitor trainMonitor = new TrainMonitor();
		createTrains(view, trainMonitor, 20, 3);
	}

	/**
	 * Method for creating trains. Each thread gets allocated a separate thread.
	 * 
	 * @param view         The view of the train.
	 * 
	 * @param trainMonitor The monitor keeping track of the train paths for
	 *                     concurrent segments.
	 * 
	 * @param num          The number of trains to create and run.
	 * 
	 * @param length       The length of the train in segments.
	 */
	private void createTrains(TrainView view, TrainMonitor trainMonitor, int num, int length) {
		for (int i = 0; i < num; i++) {
			new Thread(() -> {
				try {
					Train tmp = new Train(view.loadRoute(), trainMonitor);
					tmp.makeTrain(length);
					tmp.runTrain();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

}
