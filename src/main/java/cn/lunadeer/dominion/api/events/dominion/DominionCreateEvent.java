package cn.lunadeer.dominion.api.events.dominion;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.events.FabricEventBus;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Event data object for Dominion creation.
 * <p>
 * This class carries all the data about a dominion creation event.
 * In Fabric, actual event firing is done through {@link FabricEventBus.DominionCreateCallback}.
 * This class is kept for compatibility with code that constructs creation event data.
 */
public class DominionCreateEvent {

    private boolean skipEconomy;
    private String name;
    private UUID worldUid;
    private CuboidDTO cuboid;
    private DominionDTO parent;
    private UUID owner;
    private final CommandSourceStack operator;
    private final CompletableFuture<DominionDTO> future = new CompletableFuture<>();

    public DominionCreateEvent(@NotNull CommandSourceStack operator,
                               @NotNull String name, @NotNull UUID owner,
                               @NotNull UUID worldUid, @NotNull CuboidDTO cuboid,
                               @Nullable DominionDTO parent) {
        this.skipEconomy = false;
        this.name = name;
        this.worldUid = worldUid;
        this.cuboid = cuboid;
        this.parent = parent;
        this.owner = owner;
        this.operator = operator;
    }

    public CommandSourceStack getOperator() { return operator; }

    public void setSkipEconomy(boolean skipEconomy) { this.skipEconomy = skipEconomy; }
    public boolean isSkipEconomy() { return skipEconomy; }

    public @NotNull String getName() { return name; }
    public void setName(@NotNull String name) { this.name = name; }

    public @NotNull CuboidDTO getCuboid() { return cuboid; }
    public void setCuboid(@NotNull CuboidDTO cuboid) { this.cuboid = cuboid; }

    public @NotNull UUID getWorldUid() { return worldUid; }
    public void setWorldUid(@NotNull UUID worldUid) { this.worldUid = worldUid; }

    public @Nullable DominionDTO getParent() { return parent; }
    public void setParent(@Nullable DominionDTO parent) { this.parent = parent; }

    public @NotNull UUID getOwner() { return owner; }
    public void setOwner(@NotNull UUID owner) { this.owner = owner; }

    public CompletableFuture<DominionDTO> getFutureToComplete() { return future; }

    public CompletableFuture<Void> afterCreated(Consumer<DominionDTO> consumer) {
        return future.thenAccept(consumer);
    }

    /**
     * @deprecated Use {@link #afterCreated(Consumer)} instead.
     */
    @Deprecated(since = "4.6.0", forRemoval = true)
    public @Nullable DominionDTO getDominion() { return null; }

    /**
     * @deprecated Will be removed in future versions.
     */
    @Deprecated(since = "4.6.0", forRemoval = true)
    public void setDominion(@NotNull DominionDTO dominion) { }
}
