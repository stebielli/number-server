# Notes

## Architecture

The Number Server application has been implemented by following the architecture sketched below:

![architecture](./architecture.png)

There are five main component in the design:
* `SocketService` - its function is to accept socket connections by listening the specified port;
* `NumberServiceHandler` - once the socket connection is accepted the socket is passed to this handler 
  that try to dispatch the socket stream to some free `NumberSocketReader`; it also is responsible to 
  manage the lifecycle of the socket.
* `NumberSocketReader` - it reads the data sent through the socket; if the data is a valid number then
  it is passed to `NumberLogger` to log it; the read continues till an invalid number is sent; if a 
  `temination` is required the `Terminatior` is called;
* `NumberLogger` - it is a thread safe module with the responsibility of writing to the log file 
  those numbers that are not duplicated. It also notifies the `NumberReporter` if the received number 
  was a duplicated or not;
* `NumberReporter` - it keeps the count of uniques, duplicates and total uniques numbers between each report 
  and print them to standard output.
  
## Performance

Number server has been tested on a machine with Windows 10, intel core i5 8th gen and 16 GB of RAM. 
The implementation proved to be able to handle more than 2M of numbers per 10 seconds.

Test                            | Elapsed time (sec)    | Throughput (numbers/sec)
--------------------------------|-----------------------|-------------------------
1 socket client                 |1,87                   | 1,06 M
5 socket clients all duplicates |4,60                   | 0,43 M
5 socket clients no duplicates  |5,59                   | 0,35 M

## Development style

Number Server has been developed following as much as possible TDD approach that lead to a final 
code coverage of `87%`.

## Technical decisions

* The socket stream is read using a `java.io.BufferedReader` which proved better performance respect to
  `java.util.Scanner`;
* File is written using `java.io.FileWriter` because of its performance;
* The number logged in the log file is the plain number without leading zeros because it is not directly 
  specified in the requirements.