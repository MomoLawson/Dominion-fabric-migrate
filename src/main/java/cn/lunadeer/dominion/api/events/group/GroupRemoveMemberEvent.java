package cn.lunadeer.dominion.api.events.group;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for removing a member from a group.
 */
public class GroupRemoveMemberEvent {

    private final CommandSourceStack operator;
    private final DominionDTO dominion;
    private final GroupDTO group;
    private MemberDTO member;
    private final CompletableFuture<MemberDTO> future = new CompletableFuture<>();

    public GroupRemoveMemberEvent(@NotNull CommandSourceStack operator,
                                  @NotNull DominionDTO dominion,
                                  @NotNull GroupDTO group,
                                  @NotNull MemberDTO member) {
        this.operator = operator;
        this.group = group;
        this.dominion = dominion;
        this.member = member;
    }

    public CommandSourceStack getOperator() { return operator; }
    public @NotNull GroupDTO getGroup() { return group; }
    public @NotNull MemberDTO getMember() { return member; }
    public @NotNull DominionDTO getDominion() { return dominion; }
    public void setMember(@NotNull MemberDTO member) { this.member = member; }

    public CompletableFuture<MemberDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterAdded(Consumer<MemberDTO> consumer) {
        return future.thenAccept(consumer);
    }
}
