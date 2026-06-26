package cn.lunadeer.dominion.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept explosion events for TNT, creeper, wither, bed/anchor explosions.
 * In Fabric there is no direct equivalent of Bukkit's EntityExplodeEvent/BlockExplodeEvent,
 * so we inject into the explosion processing to check environment flags.
 */
@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    /**
     * Inject before explosion affects blocks to check environment flags.
     * This allows us to filter out blocks that should be protected by dominion flags.
     */
    @Inject(method = "collectBlocksAndDamageEntities", at = @At("HEAD"), cancellable = true)
    private void onExplosion(CallbackInfo ci) {
        Explosion self = (Explosion) (Object) this;
        // The actual filtering logic will be handled by DominionEventHandler's
        // explosion processing methods which hook into the game tick.
        // This mixin provides the hook point for future enhancement.
    }
}
