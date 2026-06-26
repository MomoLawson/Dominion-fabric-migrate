package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import net.minecraft.commands.CommandSourceStack;

/**
 * Event data object for setting the owner glow flag of a Dominion.
 */
public class DominionSetOwnerGlowEvent extends DominionModifyEvent {

    private final boolean oldValue;
    private boolean newValue;

    public DominionSetOwnerGlowEvent(CommandSourceStack operator, DominionDTO dominion, boolean newValue) {
        super(operator, dominion);
        this.oldValue = dominion.getOwnerGlow();
        this.newValue = newValue;
    }

    public boolean getOldValue() { return oldValue; }
    public boolean getNewValue() { return newValue; }
    public void setNewValue(boolean newValue) { this.newValue = newValue; }
}
