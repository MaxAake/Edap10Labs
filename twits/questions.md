## R1. So, how did you solve that bug?
By introducing mutual exclusion as well as conditional waits instead of busy-waits. 
## R2. How does the output of a crashing C program compare to that of a Java exception printout?
Errors show up in compile-time instead of in run-time. If this is better or worse we would have to ask the supervisor. 
## R3. Your test case made the server crash quite reliably (as it should). What about the crashes could suggest a concurrency problem?
The fact that they occur seemingly at random intervals and can only be reliably caused by massive load with very low or no delays.
## R4. In step I11 (page 49), you inspected a module to see whether it could possibly be threadsafe. What did you look for? How did you determine whether the module was threadsafe?
The fact that multiple threads look to the same instance of the module concurrently, making it thread safe required ensuring mutual exclusion whenever one client was accessing it, so they did not concurrently alter internal states.
## R5. How do the pthreads concepts of mutexes and condition variables relate to what you know from concurrency in Java?
They are very similar in how they are used, they only vary slightly in how they are applied (condition variables are named variables instead of being assigned to the object itself.. Since C.). 
## R6. Compare working with threads in C, to working with threads in Java. Which similarities and differences can you think of?
Mutex locks are more cleanly integrated in Java, with synchronized functions and integrated methods for wait() and notify().
## R7. Why did the server initially deadlock when you added the mutex?
Because it got stuck in a busy wait without dropping the mutex lock, meaning that no other thread could access it to alter the state needed to get out of the wait.
## R8. Why is a pthreads condition variable always associated with a mutex?
So that the condition variable knows which lock to drop in case it gets stuck. 
## R9. The initial test (ServerTest) verified that the server responds correctly to input, but it was clearly not sufficient to identify the problem you solved. Why not? What can you learn from this about testing concurrent programs? Is it possible to rely on testing alone to determine whether a concurrent program is correct?
The test did not test sufficient volumes to have any decent likelyhood of catching a concurrency issue given the short times involved. 
It is impossible to test all cases of concurrent programming. It is possible to test for most cases, but a situation may occur that the programmers did not even think about (think quantum-computers). 
