# R1: Which threads exist in your application?
Main, EDT, Sniffer  and two crackerThreads in the ThreadPool.
# R2: You have a number of callbacks in your solution (such as onMessageIntercepted() and onProgress()). Which threads call these callbacks?
Factorizer calls onProgress to increase the level of progress that the tracker counts up for the progressBar. onMessageIntercepted is called by the Sniffer Thread to send messages to the codebreaker object.
# R3: What shared data is there, and how do you ensure only one thread accesses it at a time?
The swing GUI is shared, and all calls to it are therefore run sequentially on the EDT thread. 

# R4: In the SnifferCallback interface method onMessageIntercepted, your application receives two references: one to a String, the other to a BigInteger. Strings and BigIntegers are both immutable. Why is this important here?
We can send data without worrying about race conditions, or that our copy would be modified in the future. Each thread has their own copy of the data.

# R5: What happens when you execute a longrunning task within the EDT in Swing?
The GUI freezes. This is bad. 

# R6: What are the advantages of using a thread pool? What reasons can you see for a limited pool size (number of threads) in this application?
Creating too many threads leads to increasing overhead. When a few threads might be able to manage sufficient throughput to keep up with demand.

# R7: In item I11, you found a Future method returning a boolean. In your application, in which situation(s) would the method return false?
It returns false if the task can not be cancelled. This can happen when the task has already completed. 

# R8: How have you used thread confinement in this lab?
The Swing EDT handles everything that has to do with the GUI. No other thread is allowed to access or touch the GUI without calling the SwingUtilities.invokeLater() method. 
