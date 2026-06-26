package cn.lunadeer.dominion.utils.stui.components.buttons;

import cn.lunadeer.dominion.utils.stui.ViewStyles;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

/**
 * Fabric port of Button. Builds a clickable MutableComponent element with
 * hover and click events using Minecraft's native text API.
 */
public abstract class Button {
    protected String text;
    protected String hover;
    protected boolean disabled;
    protected Style color = ViewStyles.ACTION;

    public Button() {}

    public Button setText(String text) {
        this.text = text;
        return this;
    }

    public Button setHover(String hover) {
        this.hover = hover;
        return this;
    }

    public Button setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Button setColor(Style color) {
        this.color = color;
        return this;
    }

    public abstract MutableComponent build();

    protected MutableComponent buildGeneric(ClickEvent clickEvent) {
        Style style;
        if (disabled) {
            style = Style.EMPTY.withColor(ViewStyles.SECONDARY).withStrikethrough(true);
        } else {
            style = Style.EMPTY.withColor(color);
        }

        if (hover != null && !hover.isEmpty()) {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hover)));
        }

        if (!disabled && clickEvent != null) {
            style = style.withClickEvent(clickEvent);
        }

        return Component.literal(text).setStyle(style);
    }
}
