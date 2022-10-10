package train.model;

import java.util.HashSet;
import java.util.LinkedList;
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

	public synchronized LinkedList<Segment> makeTrain(Route r, int len) throws InterruptedException {
		LinkedList<Segment> trainChain = new LinkedList<>();
		for (int i = 0; i < len; i++) {
			Segment tmp = r.next();
			segmentBusy(tmp);
			trainChain.addFirst(tmp);
			tmp.enter();
		}
		return trainChain;
	}

}
