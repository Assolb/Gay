package com.narutocraft.network;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public abstract class AbstractPacketClient extends BasePacket
{
   @Override
   public final void write(ByteBuffer data) throws BufferUnderflowException {}

   //При отправке нам необходимо указывать размер отправляемого пакета
   @Override
   public final int getSize()
   {
       return 0;
   }
}
