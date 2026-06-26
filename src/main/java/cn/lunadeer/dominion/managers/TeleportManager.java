package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.PermissionHelper;
import cn.lunadeer.dominion.utils.SafeLocationFinder;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {
    public static class TeleportManagerText extends ConfigurationPart {
        public String teleporting = "Teleporting...";
        public String cooldown = "Please wait {0} seconds before teleporting again.";
    }

    private static final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public static boolean teleportToDominion(ServerPlayer player, DominionDTO dominion) {
        if (dominion == null) return false;
        int[] tp = new int[]{dominion.getTpLocationX(), dominion.getTpLocationY(), dominion.getTpLocationZ()};
        if (tp[0] == 0 && tp[1] == 0 && tp[2] == 0) {
            tp[0] = (dominion.getX1() + dominion.getX2()) / 2;
            tp[1] = (dominion.getY1() + dominion.getY2()) / 2;
            tp[2] = (dominion.getZ1() + dominion.getZ2()) / 2;
        }
        ServerLevel level = cn.lunadeer.dominion.misc.Converts.toWorld(dominion.getWorldUid());
        if (level == null) return false;
        int[] safe = SafeLocationFinder.findSafeLocation(level, tp[0], tp[1], tp[2]);
        player.teleportTo(safe[0] + 0.5, safe[1], safe[2] + 0.5);
        return true;
    }
}
