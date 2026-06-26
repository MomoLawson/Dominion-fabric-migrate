package cn.lunadeer.dominion.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

/**
 * Renders dominion borders using particles.
 */
public class BorderRenderUtil {

    public static void renderBorder(ServerPlayer player, ServerLevel world,
                                     int x1, int y1, int z1, int x2, int y2, int z2) {
        // Render border using particles
        for (int x = x1; x <= x2; x++) {
            spawnParticle(world, player, x, y1, z1);
            spawnParticle(world, player, x, y1, z2);
            spawnParticle(world, player, x, y2, z1);
            spawnParticle(world, player, x, y2, z2);
        }
        for (int z = z1; z <= z2; z++) {
            spawnParticle(world, player, x1, y1, z);
            spawnParticle(world, player, x2, y1, z);
            spawnParticle(world, player, x1, y2, z);
            spawnParticle(world, player, x2, y2, z);
        }
        for (int y = y1; y <= y2; y++) {
            spawnParticle(world, player, x1, y, z1);
            spawnParticle(world, player, x2, y, z1);
            spawnParticle(world, player, x1, y, z2);
            spawnParticle(world, player, x2, y, z2);
        }
    }

    private static void spawnParticle(ServerLevel world, ServerPlayer player, double x, double y, double z) {
        world.spawnParticles(player, ParticleTypes.HAPPY_VILLAGER, true, x + 0.5, y + 0.5, z + 0.5, 1, 0, 0, 0, 0);
    }
}
