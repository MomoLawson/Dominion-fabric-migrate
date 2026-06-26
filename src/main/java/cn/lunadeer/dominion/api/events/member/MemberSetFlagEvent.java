package cn.lunadeer.dominion.api.events.member;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for setting member flags.
 */
public class MemberSetFlagEvent {

    private final CommandSourceStack operator;
    private final DominionDTO dominion;
    private final PriFlag flag;
    private final boolean oldValue;
    private boolean newValue;
    private final MemberDTO member;
    private final CompletableFuture<MemberDTO> future = new CompletableFuture<>();

    public MemberSetFlagEvent(@NotNull CommandSourceStack operator,
                              @NotNull DominionDTO dominion,
                              @NotNull MemberDTO member,
                              @NotNull PriFlag flag,
                              boolean newValue) {
        this.operator = operator;
        this.dominion = dominion;
        this.flag = flag;
        this.oldValue = member.getFlagValue(flag);
        this.newValue = newValue;
        this.member = member;
    }

    public CommandSourceStack getOperator() { return operator; }
    public @NotNull DominionDTO getDominion() { return dominion; }
    public PriFlag getFlag() { return flag; }
    public boolean getOldValue() { return oldValue; }
    public boolean getNewValue() { return newValue; }
    public void setNewValue(boolean newValue) { this.newValue = newValue; }
    public MemberDTO getMember() { return member; }

    public CompletableFuture<MemberDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterSet(Consumer<MemberDTO> consumer) {
        return future.thenAccept(consumer);
    }
}
