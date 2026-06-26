package cn.lunadeer.dominion.api.events.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.utils.ColorParser;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for group rename.
 */
public class GroupRenamedEvent {

    private final CommandSourceStack operator;
    private final DominionDTO dominion;
    private GroupDTO group;
    private final String oldName;
    private String newName;
    private final CompletableFuture<GroupDTO> future = new CompletableFuture<>();

    public GroupRenamedEvent(@NotNull CommandSourceStack operator,
                             @NotNull DominionDTO dominion,
                             @NotNull GroupDTO group,
                             @NotNull String newName) {
        this.operator = operator;
        this.dominion = dominion;
        this.group = group;
        this.oldName = group.getNameRaw();
        this.newName = newName;
    }

    public CommandSourceStack getOperator() { return operator; }
    public @NotNull DominionDTO getDominion() { return dominion; }
    public @NotNull GroupDTO getGroup() { return group; }
    public void setGroup(@NotNull GroupDTO group) { this.group = group; }

    public void setNewName(@NotNull String newName) { this.newName = newName; }
    public String getNewNameColored() { return newName; }
    public String getNewNamePlain() { return ColorParser.getPlainText(newName); }
    public String getOldNameColored() { return oldName; }
    public String getOldNamePlain() { return ColorParser.getPlainText(oldName); }

    public CompletableFuture<GroupDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterCreated(Consumer<GroupDTO> consumer) {
        return future.thenAccept(consumer);
    }
}
