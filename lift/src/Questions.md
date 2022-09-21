## R1. What threads are there in your solution, and how do they communicate?
1. Passenger threads: Each passenger is represented by its own thread.  
2. One lift thread for managing the lift.  
3. The main thread.  
The threads communicate through the monitor with notifications.  
## R2. Why do we always put wait in a while loop? Why wouldn’t if work?
Spurious notifications.

## R3. Mathematician Augustus De Morgan (1806–1871) is known for having formulated two laws, which can be expressed in Java as !(a && b) == (!a || !b) !(a || b) == (!a && !b) How can these laws be useful when implementing monitor methods? (Think about your while loops.)
Monitors often have to keep track of multiple variable states (as they do in this lab), when developing these statements 'not' operators are often moved around, and one must be careful to change AND to OR and vice versa when this happens. (This happened to us this lab)

## R4. Why can’t we call the LiftView method moveLift() in a monitor?
Because it will freeze the monitor for a long time.

## R5. Suppose a monitor includes a single attribute x. Also suppose the monitor includes the following method, waiting for x to change. How can you decide which method(s) should call notify/notifyAll?
Any method that modifies the variable x should call notify. If there is certainty that only one thread will be waiting and will be waiting for x, it is safe to call notify() instead of notifyAll(). 

## R6. Why can wait only be called in a synchronized method (or a method called by another synchronized method for the same object)?
wait() requires ownership of the objects lock to be called. (this feels slightly tautological). 