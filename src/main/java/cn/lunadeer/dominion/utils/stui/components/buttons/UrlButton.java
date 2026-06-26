package cn.lunadeer.dominion.utils.stui.components.buttons;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;

/**
 * Button that opens a URL in the player's browser.
 */
public class UrlButton extends PermissionButton {

    private String url;

    public UrlButton setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public MutableComponent build() {
        ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
        return buildGeneric(click);
    }
}
