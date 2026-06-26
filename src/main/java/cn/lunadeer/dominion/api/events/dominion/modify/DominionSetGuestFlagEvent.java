package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for setting guest flags on a Dominion.
 */
public class DominionSetGuestFlagEvent extends DominionModifyEvent {

    private final PriFlag flag;
    private final boolean oldValue;
    private boolean newValue;

    public DominionSetGuestFlagEvent(@NotNull CommandSourceStack operator, @NotNull DominionDTO dominion, @NotNull PriFlag flag, boolean newValue) {
        super(operator, dominion);
        this.flag = flag;
        this.oldValue = dominion.getGuestFlagValue(flag);
        this.newValue = newValue;
    }

    public PriFlag getFlag() { return flag; }
    public boolean getOldValue() { return oldValue; }
    public boolean getNewValue() { return newValue; }
    public void setNewValue(boolean newValue) { this.newValue = newValue; }
}
