package cn.lunadeer.dominion.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.PermissionSet;

public class PermissionHelper {
    public static boolean hasPermissionLevel(ServerPlayer player, int level) {
        if (player == null) return false;
        PermissionSet perms = player.permissions();
        return perms.hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
    }
}
