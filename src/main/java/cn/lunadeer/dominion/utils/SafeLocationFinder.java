package cn.lunadeer.dominion.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

/**
 * Finds safe locations for teleportation.
 */
public class SafeLocationFinder {

    public static int[] findSafeLocation(ServerLevel world, int x, int y, int z) {
        // Search up and down from the target for a safe location
        for (int dy = 0; dy < 10; dy++) {
            if (isSafeLocation(world, x, y + dy, z)) return new int[]{x, y + dy, z};
            if (dy > 0 && isSafeLocation(world, x, y - dy, z)) return new int[]{x, y - dy, z};
        }
        return new int[]{x, y, z};
    }

    private static boolean isSafeLocation(ServerLevel world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockPos above = new BlockPos(x, y + 1, z);
        return world.isAir(pos) && world.isAir(above) && !world.isAir(new BlockPos(x, y - 1, z));
    }
}
