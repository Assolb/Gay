package com.narutocraft.network;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class PacketManager
{
   //��������� ���������������
   private static HashMap<Class<? extends BasePacket>, Byte> discriminators;
   //��������� �������-�������
   private static HashMap<Byte, Class<? extends BasePacket>> classes;

   public PacketManager()
   {
       //�������������� ������ ����������
       discriminators = new HashMap<Class<? extends BasePacket>, Byte>();
       classes = new HashMap<Byte, Class<? extends BasePacket>>();

       //��������� �������������� ��� ����� �������
       initDiscriminators();
   }

   private static void initDiscriminators()
   {
       //������� ����� � �����������, ������ ����� addDiscriminator() �������� ������ ����� :�
       for (EnumPacket type : EnumPacket.values())
       {
           addDiscriminator((byte) type.ordinal(), type.getPacketClass());
       }
   }

   //����� ��� ���������� ����� ���������������
   public static void addDiscriminator(byte discriminator, Class<? extends BasePacket> clazz)
   {
       //���������, �� ��������������� �� ��� ��������� �����
       if (!discriminators.containsKey(clazz) && !classes.containsKey(discriminator))
       {
           //��������� ���������� � ������ � ���� ��� �������������
           discriminators.put(clazz, discriminator);
           classes.put(discriminator, clazz);
       }
   }

   //����� ��� ��������� ��������������
   public static byte getDiscriminator(Class<? extends BasePacket> clazz)
   {
       return discriminators.get(clazz);
   }

   //����� ��� ��������� ������ ������ �� ��������������
   public static Class<? extends BasePacket> getDiscriminatorClass(byte discriminator)
   {
       return classes.get(discriminator);
   }

   //����� ���������� ����� ����� ��� ������� � ���� ������ � ���������� ���
   public static BasePacket getPacketFromBytes(byte[] bytes) throws BufferUnderflowException, InstantiationException, IllegalAccessException
   {
       ByteBuffer data = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
       byte discriminator = data.get();

       Class<? extends BasePacket> packetClass = getDiscriminatorClass(discriminator);
       BasePacket packet = packetClass.newInstance();

       packet.read(data);

       return packet;
   }

   //����� ���������� ��������� ����� � ����� ��� ��������
   public static byte[] getBytesFromPacket(BasePacket packet) throws BufferOverflowException
   {
       byte discriminator = getDiscriminator(packet.getClass());

       ByteBuffer buffer = ByteBuffer.allocate(packet.getSize() + Byte.SIZE);

       buffer.put(discriminator);
       packet.write(buffer);

       return buffer.array();
   }
}