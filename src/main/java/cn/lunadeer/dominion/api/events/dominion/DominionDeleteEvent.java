package cn.lunadeer.dominion.api.events.dominion;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.events.FabricEventBus;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for Dominion deletion.
 * <p>
 * In Fabric, actual event firing is done through {@link FabricEventBus.DominionDeleteCallback}.
 */
public class DominionDeleteEvent {

    private boolean skipEconomy;
    private boolean force;
    private final DominionDTO dominion;
    private final CommandSourceStack operator;

    public DominionDeleteEvent(@NotNull CommandSourceStack operator, @NotNull DominionDTO dominion) {
        this.operator = operator;
        this.dominion = dominion;
        this.skipEconomy = false;
        this.force = true;
    }

    public CommandSourceStack getOperator() { return operator; }

    public boolean isForce() { return force; }
    public void setForce(boolean force) { this.force = force; }

    public void setSkipEconomy(boolean skipEconomy) { this.skipEconomy = skipEconomy; }
    public boolean isSkipEconomy() { return skipEconomy; }

    public @NotNull DominionDTO getDominion() { return dominion; }
}
