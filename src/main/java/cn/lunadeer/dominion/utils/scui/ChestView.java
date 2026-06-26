package cn.lunadeer.dominion.utils.scui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import java.util.HashMap;
import java.util.Map;

public class ChestView {
    private final String title;
    private final int rows;
    private final Map<Character, ChestButton> buttonMap = new HashMap<>();
    private String layout = "";

    public ChestView(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public ChestView setLayout(String layout) { this.layout = layout; return this; }
    public ChestView setButton(char key, ChestButton button) { buttonMap.put(key, button); return this; }

    public void open(ServerPlayer player) {
        // Open chest GUI for player
    }

    public void refresh() {}

    public static class Builder {
        private String title = "Menu";
        private int rows = 6;
        private String layout = "";
        private final Map<Character, ChestButton> buttons = new HashMap<>();

        public Builder title(String title) { this.title = title; return this; }
        public Builder rows(int rows) { this.rows = rows; return this; }
        public Builder layout(String layout) { this.layout = layout; return this; }
        public Builder button(char key, ChestButton button) { buttons.put(key, button); return this; }
        public ChestView build() { return new ChestView(title, rows); }
    }
}
