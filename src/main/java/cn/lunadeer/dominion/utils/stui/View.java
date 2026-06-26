package cn.lunadeer.dominion.utils.stui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import java.util.*;
public class View {
    protected String title;
    public View(String title) { this.title = title; }
    public void show(ServerPlayer player) {
        for (String line : buildLines()) {
            player.sendSystemMessage(Component.literal(line));
        }
    }
    protected List<String> buildLines() { return Collections.emptyList(); }
}
