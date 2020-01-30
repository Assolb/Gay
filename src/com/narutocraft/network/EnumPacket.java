package com.narutocraft.network;

public enum EnumPacket {
	  //���������� ������
	  GUILD_NAME(S0PacketSendTitle.class),
	  GUILD_SETHUDSTATS(S0PacketSetHudStats.class),//���� ����� ����� ���������� ����� � ������
	  GUILD_SENDLVLEXP(S0PacketSendLvlExp.class),
	  GUILD_SUCCESSFULLYUPGRADE(S0PacketSucessfullyUpgrade.class),
	  
	   //��������� ������
	  GUILD_GETLVLEXP(C0PacketGetLvlExp.class),
	  GUILD_TRYINGUPGRADE(C0PacketTryingUpgrade.class);

	  private Class<? extends BasePacket> packetClass;
	  private EnumPacket(Class<? extends BasePacket> packetClass) { this.packetClass = packetClass; }
	  public Class<? extends BasePacket> getPacketClass() { return packetClass; }
}