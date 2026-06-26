package cn.lunadeer.dominion.api.events.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for setting group flags.
 */
public class GroupSetFlagEvent {

    private final CommandSourceStack operator;
    private final DominionDTO dominion;
    private final PriFlag flag;
    private final boolean oldValue;
    private boolean newValue;
    private final GroupDTO group;
    private final CompletableFuture<GroupDTO> future = new CompletableFuture<>();

    public GroupSetFlagEvent(@NotNull CommandSourceStack operator,
                             @NotNull DominionDTO dominion,
                             @NotNull GroupDTO group,
                             @NotNull PriFlag flag,
                             boolean newValue) {
        this.operator = operator;
        this.dominion = dominion;
        this.flag = flag;
        this.oldValue = group.getFlagValue(flag);
        this.newValue = newValue;
        this.group = group;
    }

    public CommandSourceStack getOperator() { return operator; }
    public @NotNull DominionDTO getDominion() { return dominion; }
    public PriFlag getFlag() { return flag; }
    public boolean getOldValue() { return oldValue; }
    public boolean getNewValue() { return newValue; }
    public void setNewValue(boolean newValue) { this.newValue = newValue; }
    public GroupDTO getGroup() { return group; }

    public CompletableFuture<GroupDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterSet(Consumer<GroupDTO> consumer) {
        return future.thenAccept(consumer);
    }
}
