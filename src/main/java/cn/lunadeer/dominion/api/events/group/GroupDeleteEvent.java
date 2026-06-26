package cn.lunadeer.dominion.api.events.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for group deletion.
 */
public class GroupDeleteEvent {

    private final CommandSourceStack operator;
    private DominionDTO dominion;
    private GroupDTO group;

    public GroupDeleteEvent(@NotNull CommandSourceStack operator,
                            @NotNull DominionDTO dominion,
                            @NotNull GroupDTO group) {
        this.operator = operator;
        this.dominion = dominion;
        this.group = group;
    }

    public CommandSourceStack getOperator() { return operator; }

    public void setDominion(@NotNull DominionDTO dominion) { this.dominion = dominion; }
    public @NotNull DominionDTO getDominion() { return dominion; }

    public void setGroup(@NotNull GroupDTO group) { this.group = group; }
    public @NotNull GroupDTO getGroup() { return group; }
}
