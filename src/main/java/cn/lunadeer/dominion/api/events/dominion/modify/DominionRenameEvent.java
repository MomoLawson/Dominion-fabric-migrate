package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for Dominion rename operations.
 */
public class DominionRenameEvent extends DominionModifyEvent {

    private String newName;
    private final String oldName;

    public DominionRenameEvent(@NotNull CommandSourceStack operator, @NotNull DominionDTO dominion, @NotNull String newName) {
        super(operator, dominion);
        this.newName = newName;
        this.oldName = dominion.getName();
    }

    public @NotNull String getNewName() { return newName; }
    public @NotNull String getOldName() { return oldName; }
    public void setNewName(@NotNull String newName) { this.newName = newName; }
}
