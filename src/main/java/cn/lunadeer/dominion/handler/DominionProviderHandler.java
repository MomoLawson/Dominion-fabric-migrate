package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionReSizeEvent;
import cn.lunadeer.dominion.doos.DominionDOO;
import cn.lunadeer.dominion.utils.XLogger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.sql.SQLException;

public class DominionProviderHandler {
    public DominionProviderHandler() { XLogger.debug("DominionProviderHandler registered"); }

    public void resizeDominion(CommandSourceStack source, DominionDTO dominion, DominionReSizeEvent.TYPE type, DominionReSizeEvent.DIRECTION direction, int size) throws SQLException {
        // Resize dominion logic
        XLogger.info("Resizing dominion {0}", dominion.getName());
    }

    public void deleteDominion(CommandSourceStack source, DominionDTO dominion, boolean admin, boolean force) throws SQLException {
        // Delete dominion logic
        XLogger.info("Deleting dominion {0}", dominion.getName());
    }

    public void renameDominion(CommandSourceStack source, DominionDTO dominion, String newName) throws SQLException {
        dominion.setName(newName);
    }

    public void transferDominion(CommandSourceStack source, DominionDTO dominion, PlayerDTO newOwner, boolean force) throws SQLException {
        dominion.setOwner(newOwner.getUuid());
    }

    public void setDominionOwnerGlow(CommandSourceStack source, DominionDTO dominion, boolean glow) throws SQLException {
        dominion.setOwnerGlow(glow);
    }
}
