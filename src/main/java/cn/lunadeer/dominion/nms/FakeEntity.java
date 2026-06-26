package cn.lunadeer.dominion.nms;

import net.minecraft.server.level.ServerPlayer;

/**
 * Interface for client-side fake entities (display entities).
 */
public interface FakeEntity {
    void spawn(ServerPlayer player);
    void despawn(ServerPlayer player);
    void setPosition(double x, double y, double z);
    void setRotation(float yaw, float pitch);
    void setGlowing(boolean glowing);
    void setGlowColor(int color);
    int getEntityId();
}
