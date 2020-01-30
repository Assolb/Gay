package com.narutocraft.network;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.bukkit.Bukkit;

public class S0PacketSetHudStats extends AbstractPacketServer {

	   //�������������� ��������� ����� ������
	  String stat;
	  String value;

	  //��������� ��� ������ ��� �������� ������
	  public S0PacketSetHudStats(String stat, String value)
	  {
		  this.stat = stat;
		  this.value = value;
	  }

	  //���������� ��� � ���� ������
	  @Override
	  public void write(ByteBuffer data) throws BufferOverflowException {
		  writeString(stat, data);
		  writeString(value, data);
		  Bukkit.getLogger().info("Hi");
	  }

	  @Override
	  public int getSize() {
		  return stat.getBytes().length + value.getBytes().length + Byte.SIZE; 
	  }
}