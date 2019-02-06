execute following commands to get the output:
cd /*directory that have your .java files*/
javac  Client.java
javac ClientWorker.java
javac ThrdServer.java

java ThredServer 6666 //execuete server, 6666 is the port that you may want to use, or can be replaced by other four digit number 

java Client host 6666 //login as a client. If multiple cleint want to execute the program, run this command multiple times. Port number have to be same as server's port