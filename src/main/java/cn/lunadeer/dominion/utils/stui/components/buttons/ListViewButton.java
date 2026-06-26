package cn.lunadeer.dominion.utils.stui.components.buttons;

import cn.lunadeer.dominion.utils.command.CommandManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Fabric port of ListViewButton. Similar to FunctionalButton but the callback
 * also receives a page number. Uses the same static-map + UUID pattern.
 */
public class ListViewButton extends PermissionButton {

    private BiConsumer<ServerPlayer, Integer> function;
    private int page;

    /** Static registry: UUID -> (player, page) callback */
    private static final Map<UUID, BiConsumer<ServerPlayer, Integer>> FUNCTION_MAP = new ConcurrentHashMap<>();

    public ListViewButton setFunction(BiConsumer<ServerPlayer, Integer> function) {
        this.function = function;
        return this;
    }

    public ListViewButton setPage(int page) {
        this.page = page;
        return this;
    }

    /**
     * Executes and removes the stored function for the given uuid.
     */
    public static boolean execute(UUID uuid, ServerPlayer player, int page) {
        BiConsumer<ServerPlayer, Integer> fn = FUNCTION_MAP.remove(uuid);
        if (fn != null) {
            fn.accept(player, page);
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
        String command = CommandManager.getRootCommand() + " _tui_lsv_btn " + uuid + " " + page;
        ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
        return buildGeneric(click);
    }
}
