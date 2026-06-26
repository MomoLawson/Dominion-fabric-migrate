package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for setting environment flags on a Dominion.
 */
public class DominionSetEnvFlagEvent extends DominionModifyEvent {

    private final EnvFlag flag;
    private final boolean oldValue;
    private boolean newValue;

    public DominionSetEnvFlagEvent(@NotNull CommandSourceStack operator, @NotNull DominionDTO dominion, @NotNull EnvFlag flag, boolean newValue) {
        super(operator, dominion);
        this.flag = flag;
        this.oldValue = dominion.getEnvFlagValue(flag);
        this.newValue = newValue;
    }

    public EnvFlag getFlag() { return flag; }
    public boolean getOldValue() { return oldValue; }
    public boolean getNewValue() { return newValue; }
    public void setNewValue(boolean newValue) { this.newValue = newValue; }
}
