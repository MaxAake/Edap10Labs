
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
b) Busy-wait & Race condition
c) race-condition (not sure?)
d) deadlock

# R1. What are the shared resources in the train simulation?
The crossings are shared resources. That is, the segments that two or more trains use. 
# R2. What happened when multiple trains accessed the same resource?
The program would crash
# R3. How did you ensure trains have exclusive access to their resources?
We created a monitor that ensured mutual exclusion on all track segments
(Mutex)
# R4. Recall your monitor for keeping track of busy segments. Suppose we have a monitor with methods waitUntilFree() and markSegmentBusy(), and the train code contains the following: monitor.waitUntilFree(segment); monitor.markSegmentBusy(segment); segment.enter(); Occasionally, particularly at the highest simulation rate, trains collide. Why? Where is the bug: in the train code, in the monitor, or both?
The monitor ensures mutual exclusion within the methods, here multiple methods are called in sequence, two threads could do this at the same time. T1 calls waitUntilFree, then T2, then T1 marks segment busy and enters it. T2, which has already checked that it is empty (though it no longer is) would then mark it as busy as well and try to enter the same segment (crashing when it does so).
# R5. In step I3, you introduced a monitor method for blocking until a segment becomes free. Look back at your code, and consider the case where a segment is (initially) busy. In your code, a thread T1
## a) grabs the synchronized lock,
## b) checks a condition (and determines that the segment is busy), and
## c) waits for the condition to change.
### For the segment to become free, another thread T2 will need to enter the monitor, and mark the segment as free. But didn’t thread T1 already take the monitor lock, when it entered the monitor (step R5a above) and now waits (R5c)? How can T2 enter the monitor in this case? Explain.
When a thread waits in a monitor, it drops the monitor lock. If it did not, waiting in monitors would be impossible.

# R6. When you changed the train length from 3 to 7 (in step I5d), something went wrong. Why do trains of length 3 appear to work, but some larger ones don’t? Did you come up with a maximal train length N? If you did, can you see how this number N could be deduced from the track layout? In other words, how can you be sure trains of length N will work?
Deadlock, as 4 trains can each be blocking each other, or if the track curves, 2 trains can block each other. (show on track)
We do not think there is a safe maximal train length for ALL combinations of rails, there can of course be a maximal train length for a given rail and its crossings. 

# R7. In section 2.1.1, every intersection was stated to contain exactly one segment. Why does this matter? Consider a twosegment crossing, such as the one in figure 2.4. What could go wrong in in this situation? Do the respective directions of the trains matter?
Right now we are only looking one segment ahead. They can deadlock if they are reliant on the other one being able to move, when the crossing is for example two segments long, given that they are entering the crossing from opposite directions. They will block each other permanently and we'll be stuck waiting. (much like when two long trains encountered each others tails in different crossings at the same time, each needed the other to move before they could move themselves)
# Part 2: the factory

# R8. Initially, when the factory only produced green cubes, we didn’t have to consider synchronization. Why not?
There was only 1 thread working, which could on it's own stop the conveyor and restart it once it's work was done.

# R9. To also produce red marbles, your program needed to wait for two different events (widget under press, or widget under paint gun). These events can happen in any order. How did you change your program to handle this?
By introducing three threads, one to handle each of the two possible tool actions, as well as one to handle the conveyor's movement. If any of the tools are currently in use, the thread that uses the conveyor belt pauses until it is done and should proceed. 

# R10. The threads in your FactoryController share a common resource. Which one? Did you observe any race condition when this common resource was accessed by your threads?
They share the conveyor, if for example the painter (which worked faster than the press) tried to restart the conveyor after it was done working, the press might still be down and damaging other widgets.
# R11. There are (at least) two ways to design the factory monitor: either with wait()/notifyAll(), or without. If you used wait()/notifyAll() (or notify()), can you think of how the monitor could be designed without them? If you didn’t use wait()/notifyAll(), can you think of how they could be used here?
We could design it with a busy-wait instead. Forcing the program to run through an empty while loop until it gets 'clearance' to move through it and execute. This is however wasteful, as we are stuck using processor power processing an empty while-loop. One thread in other words operate at 100% executing an instruction that only tells it to wait. Whereas wait() simply idles and waits for a notify(). 
# R12. Consider a factory monitor based on wait()/notifyAll(). Would it, in this particular case, work if you replaced notifyAll() with notify()? Can you think of any particular advantages or disadvantages in doing so?
If both tools are waiting, a single notify would only wake one of them, thereby not allowing the other to work at the same time. The tool that got to work would then notify again, which might wake the conveyor OR the other tool, either leading to the other widget being processed, or it being skipped.


# R13. Suppose you have a concurrent program that doesn’t work. You run it and observe its behaviour. What sort of behaviour would make you suspect
## • a race condition?
States being unreliable would be a strong indicator for a race-condition. We notice for example in the bank account case from the lectures that depositing 1 10 000 times and withdrawing 1 10 000 times does not always give the result of 0. 
## • a deadlock?
Two or more threads stop at the same time, each one trying to access a shared resource. The program freezes when introducing concurrency. 
## • a busywait
A thread is taking a lot of power while doing nothing.

# R14. Is it ever safe to use if instead of while with wait()?
Reasonably it should be safe to use 'if', if there is only 1 thing in the code that could possibly notify while the wait is ongoing.

# R15. How can we ensure a concurrent program is correct? Can we expect to find all issues by testing?
No, if an error is unlikely but possible, there is no amount of testing which will be 100% certain to discover it. At a certain point the chances of an undiscovered error in the code may be very low, but not 0. The program must instead be manually analyzed for errors in the mutex setup.