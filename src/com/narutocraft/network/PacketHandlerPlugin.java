package com.narutocraft.network;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.narutocraft.main.NarutoCraft1;

public class PacketHandlerPlugin implements PluginMessageListener
{
   //������ ���, ��������� �������� ������
   private static NarutoCraft1 plugin;
   public PacketHandlerPlugin(NarutoCraft1 plugin) {
       this.plugin = plugin;
   }

   @Override
   public void onPluginMessageReceived(String channel, Player player, byte[] bytes)
   {
      if(channel.equals("narutocraftserver")) { //��������� �����

          BasePacket packet; //����� �������� ������ � �������, �������� �����

          try{
              //PacketManager �� �������� ���� �����
              packet = PacketManager.getPacketFromBytes(bytes);
              
              if (packet instanceof C0PacketGetLvlExp)
              {      
              
              }

              //���������, ����� ����� �� ��������
              
          } catch (Exception e){
              e.printStackTrace();
          }
      }
   }

   //�����, ������������� ����� ������
  public void sendPacketToPlayer(Player player, BasePacket packet)
   {
       try {
           //��������� ��������� ����� � �����
           byte[] bytes = PacketManager.getBytesFromPacket(packet);

           //����������
           player.sendPluginMessage(plugin, "narutocraftserver", bytes);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}