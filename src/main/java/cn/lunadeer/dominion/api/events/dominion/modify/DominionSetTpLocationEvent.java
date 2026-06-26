package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for setting the teleport location of a Dominion.
 * <p>
 * Teleport location is stored as x/y/z integers instead of Bukkit Location.
 */
public class DominionSetTpLocationEvent extends DominionModifyEvent {

    private final int oldTpX;
    private final int oldTpY;
    private final int oldTpZ;
    private int newTpX;
    private int newTpY;
    private int newTpZ;

    public DominionSetTpLocationEvent(@NotNull CommandSourceStack operator,
                                      @NotNull DominionDTO dominion,
                                      int x, int y, int z) {
        super(operator, dominion);
        this.oldTpX = dominion.getTpLocationX();
        this.oldTpY = dominion.getTpLocationY();
        this.oldTpZ = dominion.getTpLocationZ();
        this.newTpX = x;
        this.newTpY = y;
        this.newTpZ = z;
    }

    public int getOldTpX() { return oldTpX; }
    public int getOldTpY() { return oldTpY; }
    public int getOldTpZ() { return oldTpZ; }

    public int getNewTpX() { return newTpX; }
    public int getNewTpY() { return newTpY; }
    public int getNewTpZ() { return newTpZ; }

    public void setNewTpLocation(int x, int y, int z) {
        this.newTpX = x;
        this.newTpY = y;
        this.newTpZ = z;
    }
}
