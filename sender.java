import java.io.*;
import java.net.*;
import java.io.FileInputStream;
import java.io.PrintWriter;

class sender{
	static int packetSize = packet.maxDataLength;
    static int seqMod = packet.SeqNumModulo;
    static int windowSize = 20;
    static int timeout = 500;

    //send packets<num> in packets array from initial to the destination
    static int sendPacket(packet packets[], int initial, String hostAddress, int port, int num, PrintWriter write) throws Exception{
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(hostAddress);
        
        int count = 0;
        int i = initial;
        //create datagrams from data and send it
        while(i < packets.length && i < initial + num){
            byte[] send = packets[i].getUDPdata();
            //combain the data with hostaddress and the destination port
            DatagramPacket sendPacket = new DatagramPacket(send, send.length, IPAddress, port);
            clientSocket.send(sendPacket);
            count++;
            //write the seq number to the file
            write.println(packets[i].getSeqNum());	
            i++;
        }
        return count;
    }

    //read in the data from the file
    static byte[] getContent(String file) throws Exception{
        byte sentence[] = null;
        FileInputStream contentStream = null;
        
        try{
            File content = new File(file);
            sentence = new byte[(int)content.length()];
            contentStream = new FileInputStream(content);
            contentStream.read(sentence);
        }catch(FileNotFoundException e){
            System.out.println("File miss: " + e + "\n");
        }
        return sentence;
    }

    //accept array of byte, and packet these data prepare to send
    static packet[] getPacket(byte sentence[]) throws Exception{
        //calculate how many packets that need to be created
        int num = (int)Math.ceil((double)sentence.length/(double)packetSize);
        packet packets[] = new packet[num];
        
        int i=0;
        int j = 0;
        //create packet
        while(i < num){
            byte data[] = new byte[Math.min(packetSize, sentence.length-j)];	
            System.arraycopy(sentence, j, data, 0, Math.min(packetSize, sentence.length-j));
            packets[i] = packet.createPacket(i % seqMod, new String(data));
            i++;
            j += packetSize;
        }
        return packets;
    }

    //receive the ack packets from the receiver, and send the seq of these ack packets back
    static int receACK(int sendPort, PrintWriter ackWrite) throws Exception{
        byte[] receiveData = new byte[512];
        //create stocket for receive ack packets
        DatagramSocket ackSocket = new DatagramSocket(sendPort);
        ackSocket.setSoTimeout(timeout);
        //set timeout
        DatagramPacket ackPacket = new DatagramPacket(receiveData, receiveData.length);
        
        try{
            ackSocket.receive(ackPacket);
        }catch(SocketTimeoutException e){
            //timeout
            ackSocket.close();
            return -1;
        }finally{
            ackSocket.close();
        }
        
        packet recvPacket = packet.parseUDPdata(receiveData);
        int seq = recvPacket.getSeqNum();
        //write the seq number to the file
        ackWrite.println(seq);
        return seq;
    }


    public static void main(String args[]) throws Exception{
        if (args.length != 4){
            System.out.println("You should input four Parameters");
            System.exit(1);
        }
        String eAddress = null;
        String file = null;
        int ePort = 0;
        int sendPort = 0;
        PrintWriter ackWrite = new PrintWriter("ack.log", "UTF-8");
        PrintWriter seqWrite = new PrintWriter("seqnum.log", "UTF-8");
        
        
        eAddress = args[0];
        ePort = Integer.parseInt(args[1]);
        sendPort = Integer.parseInt(args[2]);
        file = args[3];
       
       //read in file and convert to array of packets    
        byte content[] = getContent(file);
        packet packets[] = getPacket(content);
        

        //start to send packets and stop when we acked all packets
        int packetACK = 0;
        int packetSend = 0;
        int initial = -1;
        
        while(packetACK < packets.length){
            int sp = 0;
            int first = initial + 1 + sp;	
            
            // first send all packets
            if(first < packets.length){
                sp = sendPacket(packets, first, eAddress, ePort, (windowSize-sp), seqWrite);
            }
            packetSend += sp;
            
            //start to check how many packets are acked
            int acked = 0;
            do{
                acked = 0;
                int recvack = receACK(sendPort, ackWrite);
                //timeout, reset the nunmer of sent packet to 0
                if(recvack == -1){			
                    packetSend = 0;
                }else{
                    while(((seqMod + initial + acked) % seqMod) != recvack){
                        acked++;
                    }
                    if(acked <= windowSize){
                        //move to the next packets need sending
                        initial = initial + acked;
                        //check how many packet need acking 		
                        packetSend = packetSend - acked;
                        //renew the number of acked packet      		
                        packetACK = packetACK + acked;		
                    }
                }
            } while(acked > windowSize);
        }
        
        //start to send EOT packet
        packet eots[] = new packet[1];
        eots[0] = packet.createEOT((packets.length) % seqMod);
        sendPacket(eots, 0, eAddress, ePort, 1, seqWrite);
        
        while(true){
            int recvack = receACK(sendPort, ackWrite);
            if(recvack == eots[0].getSeqNum()){
                seqWrite.close();
                ackWrite.close();
                return;
            }
        }
        
    }
    
}