Compilation:
To compile, use the makefile by executing the make command. This will produce the Sender and Receiver executables. 

Execution:
1) Execute the emulator: ./nEmulator-linux386 3000 ubuntu1204-004 5000 4500 ubuntu1204-006 6000 1 0.4 1
2) Execute the receiver: java receiver ubuntu1204-002 4500 5000 "output.txt"
3) Execute the sender:   java sender ubuntu1204-002 3000 6000 "README.txt"

Parameters:
	Emulator: 
	<emulator's receiving UDP port number in the forward (sender)direction>,
	<receiver's network address>,
	<receiver's receiving UDP port number>,
	<emulator's receiving UDP port number in the backward(receiver)direction>
	<sender's network address>,
	<sender's receiving UDP port number>,
	<maximum delay of the link in units of millisecond>,
	<packet discard probability>,
	<verbose-mode>

	Receiver: 
	<hostname for the network emulator>,
	<UDP port number used by the link emulator to receive ACKs from the receiver>,
	<UDP port number used by the receiver to receive data from the emulator>,
	<name of the file into which the received data is written>

	Sender: 
	<host address of the network emulator>,
	<UDP port number used by the emulator to receive data from the sender>,
	<UDP port number used by the sender to receive ACKs from the emulator>,
	<name of the file to be transferred>

Machines:
These programs were tested across three different machines in the student environment:
	1) Emulator was run on ubuntu1204-002
	2) Receiver was run on ubuntu1204-004
	3) Sender was run on ubuntu1204-006