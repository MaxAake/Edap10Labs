
# I5
## a) Removing wait and notify:
It works until they reach a segment which calls segmentBusy, then the monitor is locked forever (seemingly). 

## b) Removing synchronized, did not restore wait/notify:
Busy waits occupy a lot of computer resources, so much that the computer that tried to run the simulation froze, making it so that even a voice chat would not work. // Fråga handledare?!?! They stop in the middle of segments, not in the middle of crossings. This does seem odd. When two segments fight over a crossing, the computer dies.

## c1) Changing the while to an if statement:
Trains will crash, since they will exit their wait() as soon as they get any notification of a freed segment, even if it isn't the segment they were waiting for.


## c2) Changing the notifyAll() to a notify():
The trains will freeze for a while longer than needed, since the train that is notified will not always be the one that was blocked.

## d) Changing train length to 7:
Deadlock, as 4 trains can each be blocking each other, or if the track curves, 2 trains can block each other. (show on track)
We do not think there is a safe maximal train length for ALL combinations of rails, there can of course be a maximal train length for any given rail and its crossings. 
# I6
Case _ illustrates:
a) Busy-wait
b) race-condition
c) race-condition (not sure?)
d) deadlock

