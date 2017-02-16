package router;

import java.io.*;  
import java.net.*;  
import java.util.*;
import java.nio.*;

class pkt_HELLO { 
	public int router_id; 
	public int link_id;
	//public boolean hello = true;
	

	public pkt_HELLO(int rid, int lid) {
		this.router_id = rid;
		this.link_id = lid;
	}

	public byte[] convertByte() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(router_id);
		buffer.putInt(link_id);
		return buffer.array();
	}
	
	public static pkt_HELLO getData(byte[] UDPdata) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(UDPdata);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int rid = buffer.getInt();
		int lid = buffer.getInt();
		return new pkt_HELLO(rid, lid);
	}
} 
