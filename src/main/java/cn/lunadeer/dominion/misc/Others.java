package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.utils.PermissionHelper;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import java.util.Map;
import java.util.UUID;

public class Others {
    public static class OthersText extends ConfigurationPart {
        public String noPermission = "You don't have permission to do that.";
    }

    public static boolean bypassLimit(ServerPlayer player) {
        return PermissionHelper.hasPermissionLevel(player, 4);
    }

    public static boolean checkPrivilegeFlag(UUID worldUid, int x, int y, int z, PriFlag flag, ServerPlayer player) {
        if (!flag.isEnabled()) return true;
        if (player != null && PermissionHelper.hasPermissionLevel(player, 4) && Configuration.adminBypass) return true;
        DominionDTO dominion = CacheManager.instance.getDominion(worldUid, x, y, z);
        if (dominion == null) return true;
        if (dominion.getOwner().equals(player.getUUID())) return true;
        return dominion.isGuestFlag(flag.getFlagName());
    }

    public static boolean checkEnvironmentFlag(UUID worldUid, int x, int y, int z, EnvFlag flag) {
        if (!flag.isEnabled()) return true;
        DominionDTO dominion = CacheManager.instance.getDominion(worldUid, x, y, z);
        if (dominion == null) return true;
        return dominion.isEnvFlag(flag.getFlagName());
    }

    public static boolean isInDominion(DominionDTO dominion, UUID worldUid, int x, int y, int z) {
        if (dominion == null) return false;
        if (!dominion.getWorldUid().equals(worldUid)) return false;
        return x >= dominion.getX1() && x <= dominion.getX2() &&
               y >= dominion.getY1() && y <= dominion.getY2() &&
               z >= dominion.getZ1() && z <= dominion.getZ2();
    }

    public static boolean isPaper() { return false; }

    public static BlockPos[] autoPoints(ServerPlayer player) {
        int radius = Configuration.autoCreateRadius;
        BlockPos pos = player.blockPosition();
        return new BlockPos[]{
            new BlockPos(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius),
            new BlockPos(pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius)
        };
    }

    public static int[][] getSelectedPoints(ServerPlayer player) {
        Map<Integer, int[]> points = Dominion.pointsSelect.get(player.getUUID());
        if (points == null || points.size() < 2) return null;
        return new int[][]{points.get(0), points.get(1)};
    }

    public static boolean checkPrivilegeFlagSilence(DominionDTO dominion, PriFlag flag, ServerPlayer player) {
        if (dominion == null) return true;
        if (PermissionHelper.hasPermissionLevel(player, 4) && Configuration.adminBypass) return true;
        if (dominion.getOwner().equals(player.getUUID())) return true;
        return dominion.isGuestFlag(flag.getFlagName());
    }

    public static int toDominionDTO(String name) {
        DominionDTO d = CacheManager.instance.getDominion(name);
        return d != null ? d.getId() : -1;
    }

    public static void autoClean() {}
}
