JCC = javac

JFLAGS =  #-g

default: client server

#name dependencies
client: client.java
	$(JCC) $(JFLAGS) client.java		
server: server.java
	$(JCC) $(JFLAGS) server.java		

clean:
	rm *.class *~ *#* 