package cn.lunadeer.dominion.utils.stui.components.buttons;

import cn.lunadeer.dominion.utils.command.CommandManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import net.minecraft.server.level.ServerPlayer;

/**
 * Fabric port of FunctionalButton. Instead of registering a sub-command on
 * the Bukkit command tree, we store the runnable in a static map keyed by UUID
 * and create a ClickEvent that triggers {@code /dominion _tui_btn <uuid>}.
 */
public class FunctionalButton extends PermissionButton {

    /**
     * The callback receives the player who clicked the button.
     */
    private Consumer<ServerPlayer> function;

    /** Static registry: UUID -> callback */
    private static final Map<UUID, Consumer<ServerPlayer>> FUNCTION_MAP = new ConcurrentHashMap<>();

    public FunctionalButton setFunction(Consumer<ServerPlayer> function) {
        this.function = function;
        return this;
    }

    /**
     * Executes and removes the stored function for the given uuid.
     *
     * @return true if a function was found and executed
     */
    public static boolean execute(UUID uuid, ServerPlayer player) {
        Consumer<ServerPlayer> fn = FUNCTION_MAP.remove(uuid);
        if (fn != null) {
            fn.accept(player);
            return true;
        }
        return false;
    }

    @Override
    public MutableComponent build() {
        UUID uuid = UUID.randomUUID();
        if (function != null) {
            FUNCTION_MAP.put(uuid, function);
        }
        String command = CommandManager.getRootCommand() + " _tui_btn " + uuid;
        ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        return buildGeneric(click);
    }
}
