package cn.lunadeer.dominion.utils.stui;

import cn.lunadeer.dominion.utils.stui.components.Line;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric port of View. Renders a decorated text panel with title, subtitle,
 * navigator, content lines, actionbar, edge, and divide_line using
 * Minecraft's native net.minecraft.text.* API.
 */
public class View {
    private String title = "";
    private String subtitle = "";
    private String navigator = "";
    private final List<Line> contentLines = new ArrayList<>();
    private String actionbar = "";

    // Decorative elements
    private static final MutableComponent edge = Component.literal(" | ")
            .setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY));
    private static final MutableComponent divideLine = Component.literal(
            "=========================================================")
            .setStyle(Style.EMPTY.withColor(ViewStyles.PRIMARY));

    public View setTitle(String title) {
        this.title = title;
        return this;
    }

    public View setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public View setNavigator(String navigator) {
        this.navigator = navigator;
        return this;
    }

    public View addContentLine(Line line) {
        contentLines.add(line);
        return this;
    }

    public View setActionbar(String actionbar) {
        this.actionbar = actionbar;
        return this;
    }

    /**
     * Builds and sends the entire view to the player.
     */
    public void show(ServerPlayer player) {
        TextUserInterfaceManager manager = TextUserInterfaceManager.getInstance();

        // Title line
        MutableComponent titleText = Component.literal(title)
                .setStyle(Style.EMPTY.withColor(ViewStyles.PRIMARY));
        manager.sendMessage(player, titleText);

        // Subtitle line (if non-empty)
        if (subtitle != null && !subtitle.isEmpty()) {
            MutableComponent subtitleText = Component.literal(subtitle)
                    .setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY));
            manager.sendMessage(player, subtitleText);
        }

        // Navigator line (if non-empty)
        if (navigator != null && !navigator.isEmpty()) {
            MutableComponent navText = Component.literal(navigator)
                    .setStyle(Style.EMPTY.withColor(ViewStyles.ACTION));
            manager.sendMessage(player, navText);
        }

        // Divider
        manager.sendMessage(player, divideLine);

        // Content lines
        for (Line line : contentLines) {
            MutableComponent built = line.build();
            // Prepend edge decoration
            MutableComponent decorated = Text.empty().append(edge).append(built);
            manager.sendMessage(player, decorated);
        }

        // Bottom divider
        manager.sendMessage(player, divideLine);

        // Actionbar (if non-empty)
        if (actionbar != null && !actionbar.isEmpty()) {
            MutableComponent actionbarText = Component.literal(actionbar)
                    .setStyle(Style.EMPTY.withColor(ViewStyles.ACTION));
            player.sendMessage(actionbarText, true); // true = actionbar overlay
        }
    }
}
