package cn.lunadeer.dominion.utils.stui.inputter;

import cn.lunadeer.dominion.utils.stui.TextUserInterfaceManager;
import cn.lunadeer.dominion.utils.stui.ViewStyles;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fabric port of Inputter. Intercepts the next chat message from a player
 * and routes it to an InputterRunner.
 *
 * Registration: call {@link #register(ServerPlayer, InputterRunner)} to
 * set up an input prompt. The next chat message from that player will be
 * consumed and forwarded to the runner.
 *
 * The hook into the chat system should be done in the mod's chat event handler
 * (e.g. via Fabric's ServerMessageEvents.ALLOW_CHAT or a command-based approach).
 * Call {@link #handleChat(ServerPlayer, String)} from there.
 */
public class Inputter {

    /** Map of player UUID -> active InputterRunner */
    private static final Map<UUID, InputterRunner> INPUTTER_MAP = new ConcurrentHashMap<>();

    /**
     * Registers an inputter for the given player. If one is already registered,
     * it is replaced.
     */
    public static void register(ServerPlayer player, InputterRunner runner) {
        runner.setPlayer(player);
        INPUTTER_MAP.put(player.getUUID(), runner);
        runner.show();
    }

    /**
     * Cancels any active inputter for the given player.
     */
    public static void cancel(ServerPlayer player) {
        INPUTTER_MAP.remove(player.getUUID());
    }

    /**
     * Checks if the player has an active inputter.
     */
    public static boolean hasInputter(ServerPlayer player) {
        return INPUTTER_MAP.containsKey(player.getUUID());
    }

    /**
     * Handles an incoming chat message from a player. If the player has an
     * active inputter, the message is consumed (returns true) and forwarded
     * to the runner. Otherwise returns false so normal processing continues.
     *
     * @param player the player who sent the message
     * @param message the raw chat message text
     * @return true if the message was consumed by an inputter
     */
    public static boolean handleChat(ServerPlayer player, String message) {
        InputterRunner runner = INPUTTER_MAP.remove(player.getUUID());
        if (runner != null) {
            // Check for cancel keywords
            if ("cancel".equalsIgnoreCase(message.trim())
                    || "c".equalsIgnoreCase(message.trim())) {
                MutableComponent cancelled = Component.literal("Input cancelled.")
                        .setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY));
                TextUserInterfaceManager.getInstance().sendMessage(player, cancelled);
                return true;
            }
            try {
                runner.run(message);
            } catch (Exception e) {
                MutableComponent error = Component.literal("Error: " + e.getMessage())
                        .setStyle(Style.EMPTY.withColor(ViewStyles.SEVERE));
                TextUserInterfaceManager.getInstance().sendMessage(player, error);
            }
            return true;
        }
        return false;
    }
}
