package com.narutocraft.network;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.bukkit.Bukkit;

public class S0PacketSendTitle extends AbstractPacketServer {

	   //�������������� ��������� ����� ������
	  String title = "";
	  int color = 0;

	  //��������� ��� ������ ��� �������� ������
	  public S0PacketSendTitle(String title, int color)
	  {
		  this.title = title;
		  this.color = color;
	  }

	  //���������� ��� � ���� ������
	  @Override
	  public void write(ByteBuffer data) throws BufferOverflowException {
		  writeString(title, data);
		  data.putInt(color);
	  }

	  @Override
	  public int getSize() {
		  return title.getBytes().length + Integer.toString(color).getBytes().length + Byte.SIZE; 
	  }
}