import java.io.*;
import java.net.*;
import java.lang.*; 

class Client { 
	public static void main(String argv[]) throws Exception{
		if(argv.length != 4){
			System.err.println("Usage: client <server_address> <n_port> <req_code> <msg>");
	    	System.exit(0);
		}
		String server_address = argv[0];
		int n_port = Integer.parseInt(argv[1]);
		String r_port = argv[2];
		String msg = argv[3];
		
		String sentence;
		Socket clientSocket = new Socket(server_address, n_port);

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		outToServer.writeBytes(r_port + '\n');
		System.out.println("sending req_code: " + r_port);
		
		sentence = inFromServer.readLine();
		outToServer.writeBytes(sentence + '\n');

		System.out.println("receiving new r_port: " + sentence);
		clientSocket.close();
	


		String message = msg;
		int randomport = Integer.parseInt(sentence);

		DatagramSocket clientSocket1 = new DatagramSocket();

		InetAddress IPAddress = InetAddress.getByName(server_address);

		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		
		sendData = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, randomport);
		clientSocket1.send(sendPacket);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket1.receive(receivePacket);

		String modifiedSentence = new String(receivePacket.getData());
		
		System.out.println("FROM SERVER:" + modifiedSentence);
		
		clientSocket1.close();
	}
}