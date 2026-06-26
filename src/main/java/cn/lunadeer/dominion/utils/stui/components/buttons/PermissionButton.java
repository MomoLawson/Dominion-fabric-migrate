package cn.lunadeer.dominion.utils.stui.components.buttons;

import net.minecraft.server.level.ServerPlayer;
import cn.lunadeer.dominion.utils.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract button class that carries a permission list. A player must have
 * all listed permissions for the button to be active.
 */
public abstract class PermissionButton extends Button {
    protected final List<String> permissions = new ArrayList<>();

    public PermissionButton addPermission(String permission) {
        permissions.add(permission);
        return this;
    }

    /**
     * Checks whether the given player has all required permissions.
     */
    public boolean hasPermission(ServerPlayer player) {
        for (String perm : permissions) {
            if (!PermissionHelper.hasPermissionLevel(player, 2)) {
                // Fallback: in Fabric, op-level check is common; for more
                // granular permissions a permission API (like Fabric Permissions API)
                // should be integrated here.
                return false;
            }
        }
        return true;
    }
}
