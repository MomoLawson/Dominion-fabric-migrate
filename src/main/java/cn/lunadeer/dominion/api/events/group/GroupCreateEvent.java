package cn.lunadeer.dominion.api.events.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.utils.ColorParser;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for group creation.
 */
public class GroupCreateEvent {

    private final CommandSourceStack operator;
    private DominionDTO dominion;
    private String groupName;
    private final CompletableFuture<GroupDTO> future = new CompletableFuture<>();

    public GroupCreateEvent(@NotNull CommandSourceStack operator,
                            @NotNull DominionDTO dominion,
                            @NotNull String groupName) {
        this.operator = operator;
        this.dominion = dominion;
        this.groupName = groupName;
    }

    public CommandSourceStack getOperator() { return operator; }

    public void setDominion(@NotNull DominionDTO dominion) { this.dominion = dominion; }
    public @NotNull DominionDTO getDominion() { return dominion; }

    public void setGroupNameColored(String groupName) { this.groupName = groupName; }
    public String getGroupNameColored() { return groupName; }
    public String getGroupNamePlain() { return ColorParser.getPlainText(groupName); }

    public CompletableFuture<GroupDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterCreated(Consumer<GroupDTO> consumer) {
        return future.thenAccept(consumer);
    }

    @Deprecated(since = "4.6.0", forRemoval = true)
    public void setGroup(@NotNull GroupDTO group) { }

    @Deprecated(since = "4.6.0", forRemoval = true)
    public @Nullable GroupDTO getGroup() { return null; }
}
