package router;

import java.io.*;  
import java.net.*;  
import java.util.*;
import java.nio.*;

class circuit_DB { 
	public int nbr_link; 
	public linkcost[] linkcost;

	public circuit_DB(int nbr_link, linkcost[] linkcost) {
		this.nbr_link = nbr_link;
		this.linkcost = linkcost;
	}

	public static circuit_DB getData(byte[] UDPdata) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(UDPdata);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

	    int nbr_link = buffer.getInt();
	    linkcost[] lc = new linkcost[nbr_link];

	    for (int i=0; i < nbr_link; i++) {
	    	int link_id = buffer.getInt();
	    	int cost = buffer.getInt();
	    	lc[i] = new linkcost(link_id, cost);
	    }

	    return new circuit_DB(nbr_link, lc);
	}
} 