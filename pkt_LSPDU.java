package router;

import java.io.*;  
import java.net.*;  
import java.util.*;
import java.nio.*;

class pkt_LSPDU {
	
	public int sender; 
	public int router_id; 
	public int link_id;
	public int cost;
	public int via;
	//public boolean lsp = true;

	public pkt_LSPDU(int rid, int lid, int cost, int sender, int via) {
		this.router_id = rid;
		this.link_id = lid;
		this.cost = cost;
		this.sender = sender;
		this.via = via;
	}

	public pkt_LSPDU(int rid, int lid, int cost) {
		this.router_id = rid;
		this.link_id = lid;
		this.cost = cost;
	}

	public void setDestination(int sender, int via) {
		this.sender = sender;
		this.via = via; 
	}

	public byte[] convertByte() {
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(sender);
		buffer.putInt(router_id);
		buffer.putInt(link_id);
		buffer.putInt(cost);
		buffer.putInt(via);
		return buffer.array();
	}
	
	public static pkt_LSPDU getData(byte[] UDPdata) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(UDPdata);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int sender = buffer.getInt();
		int rid = buffer.getInt();
		int lid = buffer.getInt();
		int cost = buffer.getInt();
		int via = buffer.getInt();
		return new pkt_LSPDU(rid, lid, cost, sender, via);
	}
}