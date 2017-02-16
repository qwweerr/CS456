import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;

class Server {
	public static void main(String argv[]) throws Exception{
		if(argv.length != 1){
			System.err.println("Usage: server <req_code>");
	    	System.exit(0);
		}
		int r_port = Integer.parseInt(argv[0]);
		String clientSentence;
		String sentence;
		int n_port;
		

		ServerSocket welcomeSocket = new ServerSocket(0);

		n_port = welcomeSocket.getLocalPort();
		System.out.println("SERVER_PORT= " + n_port);
		System.out.println("SERVER_ADDRESS: " + InetAddress.getLocalHost().getHostName());
		System.out.println("SERVER_R_PORT: " + r_port);
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			System.out.println("client: " + clientSentence);
			if(Integer.parseInt(clientSentence) == r_port){
				DatagramSocket serverSocket = new DatagramSocket(0);

				byte[] receiveData = new byte[1024];
				byte[] sendData = new byte[1024];
				r_port = serverSocket.getLocalPort();
				sentence = String.valueOf(r_port) + '\n';
				outToClient.writeBytes(sentence);
			
		
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				
				serverSocket.receive(receivePacket);
				
				String sentence1 = new String(receivePacket.getData());
				
				InetAddress IPAddress = receivePacket.getAddress();
				
				int port = receivePacket.getPort();
				String reverseSentence = new StringBuffer(sentence1).reverse().toString();

				sendData = reverseSentence.getBytes();

				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				
				serverSocket.send(sendPacket);
				
				System.out.println("Reversed Sentence sent: " + reverseSentence);
			}else{
				System.out.println("Worng req_code");
			}
		}
	}
}