package cn.lunadeer.dominion.utils.stui.components;

import cn.lunadeer.dominion.utils.stui.ViewStyles;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private String d = " - ";
    private final List<Component> elements = new ArrayList<>();

    public Line() {}

    public static Line create() { return new Line(); }

    public Line setDivider(String d) {
        this.d = d;
        return this;
    }

    public List<Component> getElements() { return elements; }

    public Line append(Component component) {
        elements.add(component);
        return this;
    }

    public Line append(String component) {
        elements.add(Component.literal(component));
        return this;
    }

    public Line append(MutableComponent component) {
        elements.add(component);
        return this;
    }

    public MutableComponent build() {
        MutableComponent divider = Component.literal(d).setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY));
        MutableComponent result = Component.empty();
        for (int i = 0; i < elements.size(); i++) {
            result.append(elements.get(i));
            if (i != elements.size() - 1) {
                result.append(divider);
            }
        }
        return result;
    }
}
