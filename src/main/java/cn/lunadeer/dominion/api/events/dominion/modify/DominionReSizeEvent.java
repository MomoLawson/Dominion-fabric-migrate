package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.events.FabricEventBus;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for Dominion resize operations.
 * <p>
 * In Fabric, actual event firing is done through {@link FabricEventBus.DominionReSizeCallback}.
 */
public class DominionReSizeEvent extends DominionModifyEvent {

    public enum TYPE {
        EXPAND,
        CONTRACT
    }

    public enum DIRECTION {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        UP,
        DOWN
    }

    private boolean skipEconomy;
    private final CuboidDTO oldCuboid;
    private final TYPE type;
    private DIRECTION direction;
    private int size;

    public DominionReSizeEvent(@NotNull CommandSourceStack operator,
                               @NotNull DominionDTO dominion,
                               @NotNull TYPE type,
                               @NotNull DIRECTION direction,
                               int size) {
        super(operator, dominion);
        this.skipEconomy = false;
        this.oldCuboid = dominion.getCuboid();
        this.type = type;
        this.direction = direction;
        this.size = size;
    }

    public void setSkipEconomy(boolean skipEconomy) { this.skipEconomy = skipEconomy; }
    public boolean isSkipEconomy() { return skipEconomy; }

    public CuboidDTO getOldCuboid() { return oldCuboid; }
    public TYPE getType() { return type; }
    public DIRECTION getDirection() { return direction; }
    public int getSize() { return size; }

    public void setDirection(DIRECTION direction) { this.direction = direction; }
    public void setSize(int size) { this.size = size; }

    public CuboidDTO getNewCuboid() {
        CuboidDTO newCuboid = new CuboidDTO(getOldCuboid());
        int addSize = size * (type == TYPE.EXPAND ? 1 : -1);
        switch (direction) {
            case UP -> newCuboid.addUp(addSize);
            case DOWN -> newCuboid.addDown(addSize);
            case NORTH -> newCuboid.addNorth(addSize);
            case SOUTH -> newCuboid.addSouth(addSize);
            case EAST -> newCuboid.addEast(addSize);
            case WEST -> newCuboid.addWest(addSize);
        }
        return newCuboid;
    }
}
