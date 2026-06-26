package cn.lunadeer.dominion.api.events.member;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for member removal.
 */
public class MemberRemovedEvent {

    private final CommandSourceStack operator;
    private DominionDTO dominion;
    private MemberDTO member;
    private final CompletableFuture<MemberDTO> future = new CompletableFuture<>();

    public MemberRemovedEvent(@NotNull CommandSourceStack operator,
                              @NotNull DominionDTO dominion,
                              @NotNull MemberDTO member) {
        this.operator = operator;
        this.dominion = dominion;
        this.member = member;
    }

    public CommandSourceStack getOperator() { return operator; }

    public void setDominion(@NotNull DominionDTO dominion) { this.dominion = dominion; }
    public @NotNull DominionDTO getDominion() { return dominion; }

    public void setMember(@NotNull MemberDTO member) { this.member = member; }
    public @NotNull MemberDTO getMember() { return member; }

    public CompletableFuture<MemberDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterRemoved(Consumer<MemberDTO> consumer) {
        return future.thenAccept(consumer);
    }
}
