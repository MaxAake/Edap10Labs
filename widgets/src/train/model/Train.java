package train.model;

import java.util.LinkedList;

public class Train {
	private LinkedList<Segment> trainChain = new LinkedList<>();
	private Route trainRoute;
	private TrainMonitor trainMonitor;

	public Train(Route trainRoute, TrainMonitor trainMonitor) {
		this.trainRoute = trainRoute;
		this.trainMonitor = trainMonitor;
	}

	/**
	 * Creates a train of segments length. Was not allowed in the constructor as per
	 * instructions.
	 * 
	 * @param length The length of the train in segments.
	 */
	public void makeTrain(int length) throws InterruptedException {
		for (int i = 0; i < length; i++) {
			Segment tmp = trainRoute.next();
			trainChain.add(tmp);
			tmp.enter();
		}
	}

	/**
	 * Method for handling the running of a train. Once it starts, cannot be
	 * stopped.
	 */
	public void runTrain() throws InterruptedException {
		while (true) {
			Segment head = trainRoute.next();
			trainMonitor.segmentBusy(head);
			head.enter();
			trainChain.addFirst(head);

			Segment tail = trainChain.pollLast();
			tail.exit();
			trainMonitor.segmentFree(tail);

		}
	}

}
