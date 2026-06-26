package cn.lunadeer.dominion.api.events.member;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for member addition.
 */
public class MemberAddedEvent {

    private final CommandSourceStack operator;
    private DominionDTO dominion;
    private PlayerDTO player;
    private final CompletableFuture<MemberDTO> future = new CompletableFuture<>();

    public MemberAddedEvent(@NotNull CommandSourceStack operator,
                            @NotNull DominionDTO dominion,
                            @NotNull PlayerDTO player) {
        this.operator = operator;
        this.dominion = dominion;
        this.player = player;
    }

    public CommandSourceStack getOperator() { return operator; }

    public void setDominion(@NotNull DominionDTO dominion) { this.dominion = dominion; }
    public @NotNull DominionDTO getDominion() { return dominion; }

    public void setPlayer(@NotNull PlayerDTO player) { this.player = player; }
    public @NotNull PlayerDTO getPlayer() { return player; }

    public CompletableFuture<MemberDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterAdded(Consumer<MemberDTO> consumer) {
        return future.thenAccept(consumer);
    }

    @Deprecated(since = "4.6.0", forRemoval = true)
    public void setMember(@Nullable MemberDTO member) { }

    @Deprecated(since = "4.6.0", forRemoval = true)
    public @Nullable MemberDTO getMember() { return null; }
}
