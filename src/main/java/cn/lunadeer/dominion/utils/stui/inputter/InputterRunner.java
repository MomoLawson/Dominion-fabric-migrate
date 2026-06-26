package cn.lunadeer.dominion.utils.stui.inputter;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import cn.lunadeer.dominion.utils.stui.TextUserInterfaceManager;
import cn.lunadeer.dominion.utils.stui.ViewStyles;

/**
 * Fabric port of InputterRunner. Abstract base class for handling text input
 * from players via chat interception. Uses ServerPlayer and Minecraft
 * native text API.
 */
public abstract class InputterRunner {
    protected ServerPlayer player;
    protected String tip;
    protected MutableComponent promptText;

    public InputterRunner() {}

    public InputterRunner(ServerPlayer player, String tip) {
        this.player = player;
        this.tip = tip;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    /**
     * Sends the input prompt to the player.
     */
    public void show() {
        if (tip != null && !tip.isEmpty()) {
            promptText = Component.literal(tip).setStyle(Style.EMPTY.withColor(ViewStyles.ACTION));
            TextUserInterfaceManager.getInstance().sendMessage(player, promptText);
        }
    }

    /**
     * Called when the player submits text. Implementations should process the
     * input and perform the desired action.
     *
     * @param input the text the player typed
     */
    public abstract void run(String input);
}
