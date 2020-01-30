package com.narutocraft.network;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class S0PacketSendLvlExp extends AbstractPacketServer{

	public String lvlExp = "";
	
	public S0PacketSendLvlExp() {
		// TODO Auto-generated constructor stub
	}
	
	public S0PacketSendLvlExp(int lvlExp)
	{
		this.lvlExp = Integer.toString(lvlExp);
	}
	
	@Override
	public void write(ByteBuffer data) throws BufferOverflowException {
		writeString(lvlExp, data);
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return lvlExp.getBytes().length;
	}

}
