package cn.lunadeer.dominion.utils.stui.components.buttons;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;

/**
 * Simple button that triggers a command via RUN_COMMAND click event.
 */
public class CommandButton extends PermissionButton {

    private String command;

    public CommandButton setCommand(String command) {
        this.command = command;
        return this;
    }

    @Override
    public MutableComponent build() {
        ClickEvent click = new ClickEvent.RunCommand(command);
        return buildGeneric(click);
    }
}
