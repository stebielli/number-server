# Number Server

Number Server is a stand-alone application able to accept multiple socket
connections over TCP/IP that write numbers in a single log file free from duplicated.

## Prerequisites

In order to run Number Server on your machine you need some libraries installed:
* Java 11+ (how to install [here](https://www.oracle.com/java/technologies/javase-downloads.html))
* Maven (how to install [here](https://maven.apache.org/install.html))

## Quick start

The easiest way to run Number Server is running from the project folder:
```
mvn clean install exec:java
```
This command will start the application listening to sockets on port `4000` and 
creating the file `numbers.log` containing the numbers read from the connections.
The maximum number of connections is set to five, but you can increase it as your needs.

Periodically (by default 10 sec) the application print on standard output a report 
containing:
* The difference since the last report of the count of new unique numbers that have
  been received;
* The difference since the last report of the count of new duplicate numbers that 
  have been received;
* The total number of unique numbers received for this run of the Application.

An example of report could be the following:
```
Received 50 unique numbers, 2 duplicates. Unique total: 567231
```

It is possible pass some arguments to the application to modify some behaviors by running 
the command:
```
mvn clean install exec:java -Dexec.args="arg1=value1 arg2=value2 ..."
```
The accepted argument are:
* `port` (default `4000`) - define the listening port;
* `logFile` (default `numbers.log`) - define the file's path where logging the numbers;
* `maxConnections` (default `5`) - define the maximum number of connections accepted;
* `reportPeriod` (default `10000`) - define which is the period (in milliseconds)
  between print on standard output of the report.
  
## The communication protocol
It is possible sending numbers to the Number Server by using `telnet` (or `PuTTY` in windows) clients.
For example by typing on your terminal `telnet localhost 4000`.

Once the session is created, it is possible sending a number by simply typing it and press enter.
There are few rules to follow by the way:
* The number can be composed at most of 9 digits;
* If a number is composed by less than 9 digits, it must include leading zeros to till reach 9 digits;
* Any invalid number will disconnect the client;
* Typing `terminate` and press enter will stop Number Server and disconnect all the clients.

---
Now that you know everything enjoy my Number Server :)