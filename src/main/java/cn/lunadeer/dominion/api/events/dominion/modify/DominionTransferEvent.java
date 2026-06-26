package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for transferring a Dominion to a new owner.
 */
public class DominionTransferEvent extends DominionModifyEvent {

    private PlayerDTO newOwner;
    private final PlayerDTO oldOwner;
    private boolean force;

    public DominionTransferEvent(@NotNull CommandSourceStack operator, @NotNull DominionDTO dominion, @NotNull PlayerDTO newOwner) {
        super(operator, dominion);
        this.newOwner = newOwner;
        this.oldOwner = dominion.getOwnerDTO();
        this.force = true;
    }

    public boolean isForce() { return force; }
    public void setForce(boolean force) { this.force = force; }

    public @NotNull PlayerDTO getNewOwner() { return newOwner; }
    public @NotNull PlayerDTO getOldOwner() { return oldOwner; }
    public void setNewOwner(PlayerDTO newOwner) { this.newOwner = newOwner; }
}
