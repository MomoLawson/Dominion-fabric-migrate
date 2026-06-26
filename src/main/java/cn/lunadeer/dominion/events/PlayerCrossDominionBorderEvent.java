package cn.lunadeer.dominion.events;

import java.util.UUID;

public class PlayerCrossDominionBorderEvent {
    private final UUID playerUuid;
    private final int fromDomId;
    private final int toDomId;
    private boolean cancelled = false;

    public PlayerCrossDominionBorderEvent(UUID playerUuid, int fromDomId, int toDomId) {
        this.playerUuid = playerUuid;
        this.fromDomId = fromDomId;
        this.toDomId = toDomId;
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public int getFromDomId() { return fromDomId; }
    public int getToDomId() { return toDomId; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
