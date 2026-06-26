package cn.lunadeer.dominion.nms;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.Packet;

/**
 * Sends raw packets to players.
 */
public interface NMSPacketSender {
    void sendPacket(ServerPlayer player, Packet<?> packet);
}
