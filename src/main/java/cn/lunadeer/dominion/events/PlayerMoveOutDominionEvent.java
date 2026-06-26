package cn.lunadeer.dominion.events;

import java.util.UUID;

public class PlayerMoveOutDominionEvent {
    private final UUID playerUuid;
    private final int domId;
    private boolean cancelled = false;

    public PlayerMoveOutDominionEvent(UUID playerUuid, int domId) {
        this.playerUuid = playerUuid;
        this.domId = domId;
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public int getDomId() { return domId; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
