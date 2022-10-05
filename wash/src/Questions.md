# Margins
Upper margin: 0.478 +0.2 ~ 0.68
Lower margin: 0.0952 + 0.2 ~ 0.3

# R1. Which threads exist in your solution?
Io, main, temp, water, spin, washing programms 1,2 and 3.



# R2. How do the threads communicate? Is there any shared data?
The threads communicate via messages, the only shared data is those messages as well as the io (which acts as something of a monitor).

# R3. For TemperatureController, we selected a period of 10 seconds. What could the downside of a too long or too short period be?
A too long period will cause larger margins, a too short period will result in extra work.

# R4. What period did you select for WaterController? What could the downside of a too long or too short period be?
a too short period will again result in needless work, a too long period will result in inconsistency in the water level.
We chose 1 second. 
# R5. Do you use any BlockingQueue in your solution? How?
Yes, messages in our actorthreads are put into a blocking queue, allow them us to wait for messages if one has not yet arrived.

# R6. How do you use Java’s interruption facility (interrupt(), InterruptedException)?
we use them to stop threads that are currently working, the interuptedException is thrown and allows the threads to shut down in a controlled manner.
The controllers have to catch exceptions but do not do anything with them, as this should never happen. 
# R7. How do you ensure that the machine never heats unless there is water in it?
Waiting for a receive() between the steps, it ensures that the thread is blocked and is waiting for said process to finish before starting a new one. 
# R8. How can you ensure that the heat has indeed been turned off before the washing program continues (and starts the drain pump)?
Same as R7. 