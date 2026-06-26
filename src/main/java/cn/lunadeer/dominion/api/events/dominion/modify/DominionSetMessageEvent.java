package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for setting messages on a Dominion.
 */
public class DominionSetMessageEvent extends DominionModifyEvent {

    public enum TYPE {
        ENTER,
        LEAVE,
    }

    private final String oldMessage;
    private final TYPE type;
    private String newMessage;

    public DominionSetMessageEvent(@NotNull CommandSourceStack operator,
                                   @NotNull DominionDTO dominion,
                                   @NotNull TYPE type,
                                   @NotNull String newMessage) {
        super(operator, dominion);
        this.oldMessage = type == TYPE.ENTER ? dominion.getJoinMessage() : dominion.getLeaveMessage();
        this.type = type;
        this.newMessage = newMessage;
    }

    public @NotNull String getOldMessage() { return oldMessage; }
    public @NotNull TYPE getType() { return type; }
    public @NotNull String getNewMessage() { return newMessage; }
    public void setNewMessage(@NotNull String newMessage) { this.newMessage = newMessage; }
}
