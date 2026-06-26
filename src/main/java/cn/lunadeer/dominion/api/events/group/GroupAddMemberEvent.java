package cn.lunadeer.dominion.api.events.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for adding a member to a group.
 */
public class GroupAddMemberEvent {

    private final CommandSourceStack operator;
    private final DominionDTO dominion;
    private final GroupDTO group;
    private MemberDTO member;
    private final CompletableFuture<MemberDTO> future = new CompletableFuture<>();

    public GroupAddMemberEvent(@NotNull CommandSourceStack operator,
                               @NotNull DominionDTO dominion,
                               @NotNull GroupDTO group,
                               @NotNull MemberDTO member) {
        this.operator = operator;
        this.dominion = dominion;
        this.group = group;
        this.member = member;
    }

    public CommandSourceStack getOperator() { return operator; }
    public @NotNull GroupDTO getGroup() { return group; }
    public @NotNull MemberDTO getMember() { return member; }
    public void setMember(@NotNull MemberDTO member) { this.member = member; }
    public @NotNull DominionDTO getDominion() { return dominion; }

    public CompletableFuture<MemberDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterAdded(Consumer<MemberDTO> consumer) {
        return future.thenAccept(consumer);
    }
}
