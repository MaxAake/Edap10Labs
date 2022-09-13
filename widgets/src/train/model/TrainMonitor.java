package train.model;

import java.util.HashSet;
import java.util.Set;

public class TrainMonitor {
	private Set<Segment> busySegments;

	public TrainMonitor() {
		busySegments = new HashSet<>();
	}

	public synchronized void segmentBusy(Segment s) throws InterruptedException {
		while (busySegments.contains(s)) {
			wait();
		}
		busySegments.add(s);
	}

	public synchronized void segmentFree(Segment s) {
		busySegments.remove(s);
		notifyAll();

	}

}
