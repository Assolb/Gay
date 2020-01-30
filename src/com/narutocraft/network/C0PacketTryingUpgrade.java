package com.narutocraft.network;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class C0PacketTryingUpgrade extends AbstractPacketClient {

	public int stat = 0;
	
	public C0PacketTryingUpgrade() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void read(ByteBuffer data) throws BufferUnderflowException {
		stat = data.getInt();
	}
	

}
