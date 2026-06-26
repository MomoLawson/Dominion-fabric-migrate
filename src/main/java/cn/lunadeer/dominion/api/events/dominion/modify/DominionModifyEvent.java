package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.events.FabricEventBus;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Base event data object for Dominion modifications.
 * <p>
 * In Fabric, actual event firing is done through the corresponding {@link FabricEventBus} callbacks.
 */
public class DominionModifyEvent {

    private DominionDTO dominion;
    private final CommandSourceStack operator;
    private final CompletableFuture<DominionDTO> future = new CompletableFuture<>();

    public DominionModifyEvent(@NotNull CommandSourceStack operator, @NotNull DominionDTO dominion) {
        this.operator = operator;
        this.dominion = dominion;
    }

    public CommandSourceStack getOperator() { return operator; }

    public CompletableFuture<DominionDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterModified(Consumer<DominionDTO> consumer) {
        return future.thenAccept(consumer);
    }

    public @NotNull DominionDTO getDominion() { return dominion; }
    public void setDominion(@NotNull DominionDTO dominion) { this.dominion = dominion; }
}
