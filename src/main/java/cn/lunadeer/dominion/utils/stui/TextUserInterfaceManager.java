package cn.lunadeer.dominion.utils.stui;

import cn.lunadeer.dominion.Dominion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

/**
 * Fabric port of TextUserInterfaceManager. In Fabric, messages are sent
 * directly via player.sendMessage(), no BukkitAudiences adapter needed.
 */
public class TextUserInterfaceManager {

    private static TextUserInterfaceManager instance;

    public static TextUserInterfaceManager getInstance() {
        if (instance == null) {
            instance = new TextUserInterfaceManager();
        }
        return instance;
    }

    public TextUserInterfaceManager() {
        instance = this;
    }

    public void sendMessage(ServerPlayer player, Text msg) {
        player.sendMessage(msg, false);
    }
}
