## Q1. The main method (sketched above) handles user input. What additional thread(s) do you need, beyond this main thread?
One thread each for handling:
the time progression.
the user input. (this is the main thread)
the alarm. (we moved the alarm functionality into the time progression thread)

## Q2. What common data needs to be shared between threads? Where is the data to be stored?
### Hint: introduce a dedicated class for this shared data, as outlined above.
The Current time, Alarm time, State (alarm on/off) and a lock
## Q3. For each of your threads, consider:
### • What operations on shared data are needed for the thread?
### • Where in the code is this logic best implemented?
Time progression: current time
Alarm: current time, alarm time, alarm on/off
User input: should be able to change current time, change alarm time, change alarm on/off state
## Q4. In which parts of your code is data accessed concurrently from different threads? Where in your code do you need to ensure mutual exclusion?
Time progression accesses and updates current time constantly, and needs to ensure that it has mutual exclusion while that is happening.
Alarm will be reading current time, alarm state and alarm time constantly, and needs to ensure that it has mutual exclusion while that is happening.
User input can change alarm time, alarm state and current time constantly, and needs to ensure that it has mutual exclusion while that is happening.
## Q5. Are there other situations in the alarm clock where semaphores are to be used?
### Hint: have a look at ClockInput in section 1.1.2.
There is a semaphore on userInput to ensure that it does not change as the program is executing based on the user input.