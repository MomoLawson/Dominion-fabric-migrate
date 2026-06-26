package cn.lunadeer.dominion.nms;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

public class FabricFakeEntityFactory implements FakeEntityFactory {
    @Override
    public FakeEntity createBlockDisplay(ServerLevel level, BlockPos pos, BlockState state) {
        return new FabricFakeBlockDisplay(level, pos, state);
    }
}
