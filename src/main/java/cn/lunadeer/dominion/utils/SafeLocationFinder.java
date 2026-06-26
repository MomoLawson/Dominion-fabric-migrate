package cn.lunadeer.dominion.utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
public class SafeLocationFinder {
    public static int[] findSafeLocation(ServerLevel level, int x, int y, int z) {
        for (int dy = 0; dy < 10; dy++) {
            BlockPos pos = new BlockPos(x, y + dy, z);
            if (level.getBlockState(pos).isAir()) return new int[]{x, y + dy, z};
        }
        return new int[]{x, y, z};
    }
}
