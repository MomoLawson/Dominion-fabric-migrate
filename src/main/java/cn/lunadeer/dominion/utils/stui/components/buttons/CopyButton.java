package cn.lunadeer.dominion.utils.stui.components.buttons;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;

/**
 * Button that copies the given text to the player's clipboard.
 * Uses ClickEvent.Action.COPY_TO_CLIPBOARD (supported in Minecraft 1.20.5+).
 */
public class CopyButton extends PermissionButton {

    private String copyText;

    public CopyButton setCopyText(String copyText) {
        this.copyText = copyText;
        return this;
    }

    @Override
    public MutableComponent build() {
        ClickEvent click = new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copyText);
        return buildGeneric(click);
    }
}
