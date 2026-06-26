package cn.lunadeer.dominion.nms;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class FabricFakeBlockDisplay implements FakeEntity {
    private final int entityId;
    private final ServerLevel level;
    private double x, y, z;
    private boolean glowing = false;

    public FabricFakeBlockDisplay(ServerLevel level, BlockPos pos, BlockState state) {
        this.entityId = EntityIdAllocator.nextId();
        this.level = level;
        this.x = pos.getX() + 0.5;
        this.y = pos.getY();
        this.z = pos.getZ() + 0.5;
    }

    @Override public void spawn(ServerPlayer player) {}
    @Override public void despawn(ServerPlayer player) {}
    @Override public void setPosition(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    @Override public void setRotation(float yaw, float pitch) {}
    @Override public void setGlowing(boolean glowing) { this.glowing = glowing; }
    @Override public void setGlowColor(int color) {}
    @Override public int getEntityId() { return entityId; }
}
