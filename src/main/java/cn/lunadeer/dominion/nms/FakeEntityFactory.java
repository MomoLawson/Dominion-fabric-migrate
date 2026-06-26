package cn.lunadeer.dominion.nms;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

/**
 * Factory for creating client-side fake display entities.
 */
public interface FakeEntityFactory {
    FakeEntity createBlockDisplay(ServerLevel world, BlockPos pos, BlockState state);
}
