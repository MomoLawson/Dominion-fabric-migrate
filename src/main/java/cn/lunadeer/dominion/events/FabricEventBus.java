package cn.lunadeer.dominion.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import org.jetbrains.annotations.Nullable;

/**
 * Fabric event bus equivalents for the Bukkit custom events used by Dominion.
 * Each callback interface maps to one or more Bukkit event classes.
 */
public class FabricEventBus {

    // ===== Player move in/out of dominion =====

    public interface PlayerMoveInDominionCallback {
        Event<PlayerMoveInDominionCallback> EVENT = EventFactory.createArrayBacked(
                PlayerMoveInDominionCallback.class,
                listeners -> (player, dominion) -> {
                    for (PlayerMoveInDominionCallback listener : listeners) {
                        listener.onMoveIn(player, dominion);
                    }
                }
        );
        void onMoveIn(ServerPlayer player, DominionDTO dominion);
    }

    public interface PlayerMoveOutDominionCallback {
        Event<PlayerMoveOutDominionCallback> EVENT = EventFactory.createArrayBacked(
                PlayerMoveOutDominionCallback.class,
                listeners -> (player, dominion) -> {
                    for (PlayerMoveOutDominionCallback listener : listeners) {
                        listener.onMoveOut(player, dominion);
                    }
                }
        );
        void onMoveOut(ServerPlayer player, @Nullable DominionDTO dominion);
    }

    public interface PlayerCrossDominionBorderCallback {
        Event<PlayerCrossDominionBorderCallback> EVENT = EventFactory.createArrayBacked(
                PlayerCrossDominionBorderCallback.class,
                listeners -> (player, from, to) -> {
                    for (PlayerCrossDominionBorderCallback listener : listeners) {
                        listener.onCross(player, from, to);
                    }
                }
        );
        void onCross(ServerPlayer player, @Nullable DominionDTO from, @Nullable DominionDTO to);
    }

    // ===== Flag registration =====

    @FunctionalInterface
    public interface FlagRegisterCallback {
        Event<FlagRegisterCallback> EVENT = EventFactory.createArrayBacked(
                FlagRegisterCallback.class,
                listeners -> (modId, flag, registerAction) -> {
                    for (FlagRegisterCallback listener : listeners) {
                        listener.onFlagRegister(modId, flag, registerAction);
                    }
                }
        );
        void onFlagRegister(String modId, cn.lunadeer.dominion.api.dtos.flag.Flag flag, Runnable registerAction);
    }

    // ===== Entity damage by entity (for PVP, animal/monster/villager killing, etc.) =====

    public interface EntityDamageByEntityCallback {
        Event<EntityDamageByEntityCallback> EVENT = EventFactory.createArrayBacked(
                EntityDamageByEntityCallback.class,
                listeners -> (attacker, victim, source, amount) -> {
                    boolean cancelled = false;
                    for (EntityDamageByEntityCallback listener : listeners) {
                        if (listener.onDamage(attacker, victim, source, amount)) {
                            cancelled = true;
                        }
                    }
                    return cancelled;
                }
        );
        /**
         * @return true to cancel the damage
         */
        boolean onDamage(Entity attacker, Entity victim, DamageSource source, float amount);
    }
}
