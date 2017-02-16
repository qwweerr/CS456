package router;

import java.io.*;  
import java.net.*;  
import java.util.*;
import java.nio.*;

class pkt_INIT { 
	public int router_id;
	//public boolean init = true;
	//
	public pkt_INIT(int rid) {
		this.router_id = rid;
	}

	public byte[] convertByte() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(router_id);
		return buffer.array();
	}
	
	public static pkt_INIT getData(byte[] UDPdata) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(UDPdata);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int rid = buffer.getInt();
		return new pkt_INIT(rid);
	}
} 