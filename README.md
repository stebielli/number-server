# Number Server

Number Server is a stand-alone application able to accept multiple socket
connections over TCP/IP for writing numbers in a single log file free from duplicates.

## Prerequisites

In order to run Number Server on your machine you need:
* Java 11+ (how to install [here](https://www.oracle.com/java/technologies/javase-downloads.html))
* Maven (how to install [here](https://maven.apache.org/install.html))

## Quick start

The easiest way to run Number Server is typing from the project folder the command:
```
mvn clean install exec:java
```
It will start the application, create the file `numbers.log` and listen for 
numbers to write in the file on port `4000`. The maximum number of connections is set to five, 
but you can increase it as your needs.

Periodically (by default 10 sec) the application prints on standard output a report 
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

### Tune the application

It is possible to pass some arguments at the startup to modify some default behaviors by running 
the command:
```
mvn clean install exec:java -Dexec.args="arg0=value0 arg1=value1 ..."
```
The accepted arguments are:
* `port` (default `4000`) - define the listening port;
* `logFile` (default `numbers.log`) - define the file's path where logging the numbers;
* `maxConnections` (default `5`) - define the maximum number of connections accepted;
* `reportPeriod` (default `10000`) - define how long is the period (in milliseconds)
  between each report.
  
## The communication protocol
It is possible to send numbers to the Number Server by using `telnet` (or `PuTTY` for windows) clients.
For example by typing on your terminal `telnet localhost 4000`.

Once the client session is created, a number can be sent by simply typing it and press enter.
There are few rules to follow by the way:
* The number can be composed at most of 9 digits (e.g. `314159265` or `007007009`);
* If a number is composed by less than 9 digits, it must include leading zeros till reach 9 digits;
* Any invalid sent number will disconnect the client;
* Typing `terminate` followed by enter will stop Number Server and disconnect all the clients.

---
Now that you know everything enjoy the Number Server :)