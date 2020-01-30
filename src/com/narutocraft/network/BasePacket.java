package com.narutocraft.network;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public abstract class BasePacket
{
   public abstract void write(ByteBuffer data) throws BufferOverflowException;

   public abstract void read(ByteBuffer data) throws BufferUnderflowException;

   //������ ��������� ���� �����. �������� �� ��������� ������� ������
   public abstract int getSize();

   public static void writeString(String string, ByteBuffer data) throws BufferOverflowException
   {
       byte[] stringBytes = string.getBytes();
       data.putInt(stringBytes.length);
       data.put(stringBytes);
   }

   public static String readString(ByteBuffer data) throws BufferUnderflowException
   {
       int length = data.getInt();
       byte[] stringBytes = new byte[length];
       data.get(stringBytes);

       return new String(stringBytes);
   }
}