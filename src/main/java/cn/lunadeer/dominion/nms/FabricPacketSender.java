package cn.lunadeer.dominion.nms;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public class FabricPacketSender implements NMSPacketSender {
    @Override
    public void sendPacket(ServerPlayer player, Packet<?> packet) {
        player.networkHandler.sendPacket(packet);
    }
}
