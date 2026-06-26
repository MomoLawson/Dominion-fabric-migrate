package cn.lunadeer.dominion.utils.stui;
import cn.lunadeer.dominion.utils.stui.components.Line;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import java.util.*;
public class ListView extends View {
    private final List<Line> lines = new ArrayList<>();
    private final String commandPrefix;
    private final String page;
    public ListView(String title, String commandPrefix, String page) { super(title); this.commandPrefix = commandPrefix; this.page = page; }
    public ListView addLine(Line line) { lines.add(line); return this; }
    public void show(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§e=== " + title + " ==="));
        for (Line line : lines) {
            player.sendSystemMessage(line.build());
        }
    }
}
