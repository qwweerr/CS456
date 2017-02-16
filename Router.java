package router;

import java.io.*;  
import java.net.*;  
import java.util.*;
import java.nio.*;

public class Router {

	public static int NBR_ROUTER = 5;
	//use infinit to represents connection between two routers when initializa
	public static int infinite = 2147483647;
	public static int routerId; 
	public static link[] links;
	//store the routerid, linkid and cost to get the ith node  
	public static link[] routerTable; 
	public static linkcost[][] neighbour;

	public static PrintWriter log;

	// store lspdu
	public static ArrayList<pkt_LSPDU> lspdus;
	//data size of initial message
	public static int initsize = 4;
	//data size of lsppdu
	public static int lspsize = 20;
	//data size of hello message
	public static int hellosize = 8;
	//UDP
	public static DatagramSocket socket;
	public static int rPort;
	public static InetAddress nseAddress;
	public static int nsePort;

	public static void sendLSPDU(int link_id, pkt_LSPDU pkt) throws Exception{
		pkt.setDestination(routerId, link_id);
    	DatagramPacket packet = new DatagramPacket(pkt.convertByte(), lspsize, nseAddress, nsePort);
    	socket.send(packet);
    	log.println("R"+routerId+" sends lspdu: sender: "+pkt.sender+" router_id: "+pkt.router_id+" link_id: "+pkt.link_id+" cost: "+pkt.cost+" via: "+pkt.via);
	}



	public static void dijkstra(pkt_LSPDU lsp_pkt){
		boolean updated = false;
		int index = 0;
		while(index < lspdus.size()) {
			pkt_LSPDU exit_pkt = lspdus.get(index);
			
			if (lsp_pkt.link_id == exit_pkt.link_id){
				updated = true;
				neighbour[lsp_pkt.router_id-1][exit_pkt.router_id-1] = new linkcost(lsp_pkt.link_id, lsp_pkt.cost);
				neighbour[exit_pkt.router_id-1][lsp_pkt.router_id-1] = new linkcost(lsp_pkt.link_id, lsp_pkt.cost);
			}
			index++;
		}

		if (updated){
			int[] dist = new int[NBR_ROUTER];
			int[] new_router_id = new int[NBR_ROUTER];
			ArrayList<Integer> list = new ArrayList<Integer>();
			int i = 0;
			while(i < NBR_ROUTER){
				if(i != routerId - 1){
					dist[i] = infinite;
				}else{
					dist[i] = 0;
				}
				new_router_id[i] = i;
				list.add(i);
				i++;
			}

			while(list.size() > 0){
				int min = infinite;
				int n = 0;
				int x=0;
				while(x < list.size()){
					int router = list.get(x);
					if (min > dist[router]){
						min = dist[router];
						n = x;
					}
					i++;
				}
				int rid = list.get(n);
				list.remove(n);

				int j=0;
				while(j < NBR_ROUTER){
					if (neighbour[j][rid].cost < infinite){
						int distance = dist[rid] + neighbour[j][rid].cost;
						if (distance < dist[j]){
							dist[i] = distance;
							if (routerId - 1 != rid){
								new_router_id[j] = new_router_id[rid];
							}
						}
					}
					j++;
				}
			}

			int m = 0;
			while(index < NBR_ROUTER){
				int nextId = new_router_id[m];
				link routingLink = new link(nextId + 1, neighbour[routerId -1][nextId].link, dist[m]);
				routerTable[m] = routingLink;
				m++;
			}
		}
	}



	public static void writelog(){
		int i = 0;
		while(i < NBR_ROUTER){
			if(routerTable[i].cost == infinite){
				int x = i + 1;
				log.println("R"+ routerId +" -> R"+ x +" -> INF, INF");
			}else if(i == routerId -1){
				int y = i + 1;
				log.println("R"+ routerId +" -> R"+ y +" -> INF, INF");
			}else{
				int z = i + 1;
				log.println("R"+ routerId + " -> R" + z + " -> R"+routerTable[i].router_id+" cost: "+ routerTable[i].cost);
			}
			i++;
		}
	}


	public static void sort(){
		int i = 0;
		while(i < NBR_ROUTER){
			ArrayList<pkt_LSPDU> path = new ArrayList<pkt_LSPDU>();
			int index = 0;
			while(index < lspdus.size()){
				pkt_LSPDU lspduPkt = lspdus.get(index);
				if(lspduPkt.router_id == i + 1){
					path.add(lspduPkt);
				}
				index++;
			}
			int sum = i+1;
			log.println("R"+routerId+" -> R"+ sum +" nbr_link " + path.size());
			int j = 0;
			while(j < path.size()){
				pkt_LSPDU lspduPkt = path.get(j);
				log.println("R"+routerId+" -> R"+ sum +" link: "+ lspduPkt.link_id +" cost: "+lspduPkt.cost);
				j++;
			}
			i++;
		}
	}



	public static void main(String[] args) throws Exception{
		if (args.length < 4) {
	      System.out.println("You should input four parameters");
	      System.exit(1);
	    }
		routerTable = new link[NBR_ROUTER];
	    lspdus = new ArrayList<pkt_LSPDU>();
	    neighbour = new linkcost[NBR_ROUTER][NBR_ROUTER];
	    socket = new DatagramSocket();
	    routerId = Integer.parseInt(args[0]);
	    nseAddress = InetAddress.getByName(args[1]);
	    nsePort = Integer.parseInt(args[2]);
	    rPort = Integer.parseInt(args[3]);
	    
	    int i = 0;
	    while(i < NBR_ROUTER){
	    	int j = 0;
	    	while(j < NBR_ROUTER){
	    		neighbour[i][j] = new linkcost(-1, infinite);
	    		j++;
	    	}
	    	neighbour[i][i] = new linkcost(-1, 0);
	    	i++;
	    }

	    int index=0;
	    while(index < routerTable.length) {
	    	routerTable[index] = new link(index+1, -1, infinite);
	    	index++;
	    }

	    log = new PrintWriter(new FileWriter(String.format("router%d.log", routerId)), true);

		//send initial message
		pkt_INIT pkt = new pkt_INIT(routerId);
    	DatagramPacket packet = new DatagramPacket(pkt.convertByte(), initsize, nseAddress, nsePort); 
    	socket.send(packet);
    	log.println("R"+ routerId +" sends init: router_id: " + routerId);



		//get db
		byte[] data = new byte[512];
	    DatagramPacket packet1 = new DatagramPacket(data, data.length);  
	    socket.receive(packet1);
	    circuit_DB DB = circuit_DB.getData(packet1.getData());

	    links = new link[DB.nbr_link];

	    index = 0;
	    while(index < DB.nbr_link){
	    	link l = new link(DB.linkcost[index]);
	    	links[index] = l;
	    	pkt_LSPDU lspdu = new pkt_LSPDU(routerId, l.link_id, l.cost);
	    	lspdus.add(lspdu);
	    	index++;
	    }
	    log.println("R" + routerId + " gets circuit_db: nbr_link: " + DB.nbr_link);

	    sort();
		

		//send hello message
		i = 0;
		while(i < links.length) {
			pkt_HELLO hpkt = new pkt_HELLO(routerId, links[i].link_id);
	    	DatagramPacket packet2 = new DatagramPacket(hpkt.convertByte(), hellosize, nseAddress, nsePort); 
	    	socket.send(packet2);
	    	log.println("R"+routerId+" sends hello: router_id: "+routerId+" link_id: "+links[i].link_id);
    		i++;
		}

		while(true){
			//receive packet
			byte[] data1 = new byte[512];
		    DatagramPacket packet3 = new DatagramPacket(data1, data1.length);  
		    socket.receive(packet3);
		    //packet is pkt_HELLO
			int size = packet3.getLength();
			if(size == hellosize){
				pkt_HELLO hpkt1 = pkt_HELLO.getData(packet3.getData());
				int rid = hpkt1.router_id;
				int lid = hpkt1.link_id;

				//send lspdu to neighbour
				index = 0;
				while(index < lspdus.size()){
					pkt_LSPDU lspduPkt = lspdus.get(index);
					sendLSPDU(lid, lspduPkt);
					index++;
				}

				//check which link_id connects to this neighbour
				i = 0;
				while(i < links.length){
					if (links[i].link_id == lid){
						links[i].router_id = rid;
					}
					i++;
				}
				log.println("R"+ routerId + " gets hello: router_id: "+ hpkt1.router_id +" link_id: " + hpkt1.link_id);
	    	}
	    	//packet is pkt_LSPDU
	    	if(size == lspsize){
	    		pkt_LSPDU lpkt = pkt_LSPDU.getData(packet3.getData());
	    		index = 0;
				while(index < lspdus.size()){
					if (lspdus.get(index).link_id == lpkt.link_id && lspdus.get(index).router_id == lpkt.router_id){
						return;
					}
					index++;
				}

				//update the neighbour Matrix and routerTable
				dijkstra(lpkt);

				lspdus.add(lpkt);
				
				//send LSPDU's to neighbours
				int slink_id = lpkt.link_id;
				i=0;
				while(i < links.length){
					if (links[i].link_id != slink_id){
		 				sendLSPDU(links[i].link_id, lpkt);
					}
					i++;
				}

				log.println("R"+ routerId +" gets lspdu: sender: "+ lpkt.sender +" router_id: "+ lpkt.router_id +" link_id: "+ lpkt.link_id +" cost: "+ lpkt.cost +" via: " + lpkt.via);

		    	sort();
		    	writelog();
	    	}
		}
	}

}

