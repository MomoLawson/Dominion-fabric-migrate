package cn.lunadeer.dominion.misc;
import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.api.dtos.flag.*;
import cn.lunadeer.dominion.api.events.dominion.modify.*;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import java.util.UUID;
public class Converts {
    public static class ConvertsText extends ConfigurationPart { public String mustBeOnline = "Must be online: {0}"; public String worldNotFound = "World not found: {0}"; }
    public static ServerLevel toWorld(UUID worldUid) { return null; }
    public static ServerLevel toWorld(String name) { return null; }
    public static UUID toWorldUid(String worldName) { return UUID.nameUUIDFromBytes(worldName.getBytes()); }
    public static DominionSetMessageEvent.TYPE toMessageType(String arg) throws DominionException { return DominionSetMessageEvent.TYPE.valueOf(arg.toUpperCase()); }
    public static DominionReSizeEvent.TYPE toResizeType(String arg) throws DominionException { return DominionReSizeEvent.TYPE.valueOf(arg.toUpperCase()); }
    public static DominionReSizeEvent.DIRECTION toDirection(String arg) throws DominionException { return DominionReSizeEvent.DIRECTION.valueOf(arg.toUpperCase()); }
    public static DominionReSizeEvent.DIRECTION toDirection(ServerPlayer player) { return DominionReSizeEvent.DIRECTION.SOUTH; }
    public static int toIntegrity(String s) { try { return Integer.parseInt(s); } catch (Exception e) { return 0; } }
    public static boolean toBoolean(String s) { return "true".equalsIgnoreCase(s); }
    public static int[] toColor(String hex) { return new int[]{255, 255, 255}; }
    public static boolean isBedrockPlayer(UUID uuid) { return uuid.toString().startsWith("00000000"); }
    public static DominionDTO toDominionDTO(String nameOrId) { try { return CacheManager.instance.getDominion(Integer.parseInt(nameOrId)); } catch (Exception e) { return CacheManager.instance.getDominion(nameOrId); } }
    public static ServerPlayer toPlayer(String name) { return Dominion.server != null ? Dominion.server.getPlayerList().getPlayerByName(name) : null; }
    public static UUID toPlayerUuid(String name) { ServerPlayer p = toPlayer(name); return p != null ? p.getUUID() : null; }
    public static PlayerDTO toPlayerDTO(String name) { UUID u = toPlayerUuid(name); return u != null ? CacheManager.instance.getPlayer(u) : null; }
    public static PlayerDTO toPlayerDTO(UUID uuid) { return CacheManager.instance.getPlayer(uuid); }
    public static PriFlag toPriFlag(String name) { for (PriFlag f : Flags.getAllPriFlags()) { if (f.getFlagName().equalsIgnoreCase(name)) return f; } return null; }
    public static EnvFlag toEnvFlag(String name) { for (EnvFlag f : Flags.getAllEnvFlags()) { if (f.getFlagName().equalsIgnoreCase(name)) return f; } return null; }
    public static GroupDTO toGroupDTO(DominionDTO d, String name) { return null; }
    public static GroupDTO toGroupDTO(int id) { return CacheManager.instance.getGroup(id); }
    public static MemberDTO toMemberDTO(DominionDTO d, String name) { return null; }
}
