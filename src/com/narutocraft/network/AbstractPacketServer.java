package com.narutocraft.network;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public abstract class AbstractPacketServer extends BasePacket
{
  @Override
  public void read(ByteBuffer data) throws BufferUnderflowException {}
}