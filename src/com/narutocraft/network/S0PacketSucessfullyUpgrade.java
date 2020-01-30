package com.narutocraft.network;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class S0PacketSucessfullyUpgrade extends AbstractPacketServer {

	private String b = "";
	
	public S0PacketSucessfullyUpgrade() {
		// TODO Auto-generated constructor stub
	}
	
	public S0PacketSucessfullyUpgrade(int b) {
		
		this.b = Integer.toString(b);
	}
	
	@Override
	public void write(ByteBuffer data) throws BufferOverflowException {
		writeString(b, data);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return b.getBytes().length;
	}
	

}
