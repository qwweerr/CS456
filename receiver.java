import java.io.*;
import java.net.*;
import java.io.PrintWriter;

class receiver {
    
    static int seqMod = packet.SeqNumModulo;
    
    public static void main(String args[]) throws Exception{
        if (args.length != 4){
            System.out.println("You should input four parameters");
            System.exit(1);
        }
        String eAddress = null;
        String file = null;
        int ePort = 0;
        int recvPort = 0;
        
        eAddress = args[0];
        ePort = Integer.parseInt(args[1]);
        recvPort = Integer.parseInt(args[2]);
        file = args[3];
        
        PrintWriter seqWrite = new PrintWriter("arrival.log", "UTF-8");
        PrintWriter outputWrite = new PrintWriter(file, "UTF-8");
        
        DatagramSocket welcomeSocket = new DatagramSocket(recvPort);
        DatagramSocket clientSocket = new DatagramSocket();
        
        byte[] recevData = new byte[1024];
        
        
        int expectSeq = 0;
        int seq = 0;
        //receive first packet
        boolean first = false;  
        
        while(true){
            DatagramPacket rPacket = new DatagramPacket(recevData, recevData.length);
            welcomeSocket.receive(rPacket);
            packet recvPacket = packet.parseUDPdata(rPacket.getData());
            
            byte recvData[] = recvPacket.getData();
            seqWrite.println(recvPacket.getSeqNum());        
            packet ack = null;
            if(recvPacket.getSeqNum() == expectSeq){
                //receive seq number of packet equal to the excepted seq number
                first = true;                          
                seq = recvPacket.getSeqNum();	     
                expectSeq = (expectSeq + 1) % seqMod;
                //deliver the data
                if(recvPacket.getType() == 1){               
                    outputWrite.print(new String(recvPacket.getData()));
                }
            }else if(!first){
                continue;
            }else{
                //receive wrong seq, remain the last acked seq
                seq = (seqMod + (expectSeq - 1)) % seqMod;		
            }if(recvPacket.getType() == 2){ 
            //get eot packet                
                ack = packet.createEOT(seq);
                Thread.sleep(1000);                        
            }else{
                ack = packet.createACK(seq);
            }
            
            //send the packets
            InetAddress IPAddress = InetAddress.getByName(eAddress);
            byte acks[] = ack.getUDPdata();
            DatagramPacket sendPacket = new DatagramPacket(acks, acks.length, IPAddress, ePort);
            clientSocket.send(sendPacket);
            
            //close after sending eot
            if(ack.getType() == 2){
                seqWrite.close();
                outputWrite.close();
                break;
            }
        }
    }
}