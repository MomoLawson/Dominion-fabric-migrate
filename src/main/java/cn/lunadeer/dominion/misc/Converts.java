package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionReSizeEvent;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionSetMessageEvent;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import java.util.UUID;

public class Converts {
    public static class ConvertsText extends ConfigurationPart {
        public String mustBeOnline = "Player must be online: {0}";
        public String worldNotFound = "World not found: {0}";
    }

    public static ServerLevel toWorld(UUID worldUid) {
        if (Dominion.server == null) return null;
        for (ServerLevel level : Dominion.server.getAllLevels()) {
            if (level.dimension().location().toString().equals(worldUid.toString())) return level;
        }
        return null;
    }

    public static ServerLevel toWorld(String name) {
        if (Dominion.server == null) return null;
        try {
            Identifier worldId = Identifier.tryParse(name);
            if (worldId != null) {
                ResourceKey<Level> worldKey = ResourceKey.of(Registries.DIMENSION, worldId);
                return Dominion.server.getLevel(worldKey);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static UUID toWorldUid(String worldName) {
        return UUID.nameUUIDFromBytes(worldName.getBytes());
    }

    public static DominionSetMessageEvent.TYPE toMessageType(String arg) throws DominionException {
        try { return DominionSetMessageEvent.TYPE.valueOf(arg.toUpperCase()); }
        catch (Exception e) { throw new DominionException("Invalid message type: " + arg); }
    }

    public static DominionReSizeEvent.TYPE toResizeType(String arg) throws DominionException {
        try { return DominionReSizeEvent.TYPE.valueOf(arg.toUpperCase()); }
        catch (Exception e) { throw new DominionException("Invalid resize type: " + arg); }
    }

    public static DominionReSizeEvent.DIRECTION toDirection(String arg) throws DominionException {
        try { return DominionReSizeEvent.DIRECTION.valueOf(arg.toUpperCase()); }
        catch (Exception e) { throw new DominionException("Invalid direction: " + arg); }
    }

    public static int toIntegrity(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    public static DominionDTO toDominionDTO(String nameOrId) {
        if (nameOrId == null || nameOrId.isEmpty()) return null;
        try {
            int id = Integer.parseInt(nameOrId);
            return CacheManager.instance.getDominion(id);
        } catch (NumberFormatException e) {
            return CacheManager.instance.getDominion(nameOrId);
        }
    }

    public static ServerPlayer toPlayer(String name) {
        if (Dominion.server == null) return null;
        return Dominion.server.getPlayerList().getPlayerByName(name);
    }

    public static UUID toPlayerUuid(String name) {
        ServerPlayer player = toPlayer(name);
        return player != null ? player.getUUID() : null;
    }

    public static boolean isBedrockPlayer(UUID uuid) {
        return uuid.toString().startsWith("00000000");
    }

    public static int[] toColor(String hex) {
        if (hex == null || hex.isEmpty()) return new int[]{255, 255, 255};
        hex = hex.replace("#", "");
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new int[]{r, g, b};
        } catch (Exception e) {
            return new int[]{255, 255, 255};
        }
    }

    public static PlayerDTO toPlayerDTO(String name) {
        UUID uuid = toPlayerUuid(name);
        if (uuid == null) return null;
        return CacheManager.instance.getPlayer(uuid);
    }

    public static DominionReSizeEvent.DIRECTION toDirection(ServerPlayer player) {
        // Determine direction based on player facing
        float yaw = player.getYRot();
        if (yaw < 45 || yaw > 315) return DominionReSizeEvent.DIRECTION.SOUTH;
        if (yaw < 135) return DominionReSizeEvent.DIRECTION.WEST;
        if (yaw < 225) return DominionReSizeEvent.DIRECTION.NORTH;
        return DominionReSizeEvent.DIRECTION.EAST;
    }
}
