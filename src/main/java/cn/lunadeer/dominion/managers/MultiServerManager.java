package cn.lunadeer.dominion.managers;

import cn.lunadeer.dominion.configuration.Configuration;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.storage.repository.ServerRepository;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.Misc;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Multi-server manager for cross-server dominion management.
 * Ported from Bukkit to Fabric.
 *
 * In Fabric, we use plugin messaging via the standard Minecraft
 * plugin channel system instead of BungeeCord channels.
 */
public class MultiServerManager {

    public static class MultiServerManagerText extends ConfigurationPart {
        public String getIdByNameError = "Server name ({0}) does not exist.";
        public String getNameByIdError = "Server ID ({0}) does not exist.";
        public String warnUpdateServerName = "There is already a server with ID {0} and name {1}, but the current server name is {2}. Updating the name to {2}.";
    }

    public static MultiServerManager instance;
    private final MinecraftServer server;
    private final Map<Integer, String> cachedServerMap = new HashMap<>();

    public MultiServerManager(MinecraftServer server) {
        instance = this;
        this.server = server;

        // Register plugin channel for cross-server communication
        // In Fabric, plugin channels are handled differently than Bukkit.
        // For BungeeCord/Velocity compatibility, we use the standard
        // "bungeecord:main" channel or custom channels.

        try {
            String name = ServerRepository.getServerName(Configuration.multiServer.serverId);
            if (name == null) {
                ServerRepository.insertServer(Configuration.multiServer.serverId, Configuration.multiServer.serverName);
            } else {
                if (!name.equals(Configuration.multiServer.serverName)) {
                    XLogger.warn(Language.multiServerManagerText.warnUpdateServerName,
                            Configuration.multiServer.serverId, name, Configuration.multiServer.serverName);
                    ServerRepository.updateServerName(Configuration.multiServer.serverId, Configuration.multiServer.serverName);
                }
            }
            cachedServerMap.put(Configuration.multiServer.serverId, Configuration.multiServer.serverName);
        } catch (Exception e) {
            XLogger.error(e);
        }
    }

    /**
     * Connect a player to another server via plugin messaging.
     *
     * @param player     The player to transfer
     * @param serverName The target server name
     */
    public void connectToServer(@NotNull ServerPlayer player, @NotNull String serverName) {
        // In Fabric with Velocity/BungeeCord, server transfer is done via
        // the "bungeecord:main" plugin channel or the built-in transfer packet (1.20.2+).
        // For MC 26.1.2, we can use the native server transfer packet:
        //   ServerPlayNetworking.send(player, ...);
        // Or use the BungeeCord plugin channel for compatibility.

        // TODO: Implement actual server transfer when plugin messaging is ported.
        // For now, this is a placeholder.
        XLogger.warn("Multi-server connect to {0} not yet implemented in Fabric.", serverName);
    }

    public String getServerName(int serverId) throws Exception {
        if (cachedServerMap.containsKey(serverId)) {
            return cachedServerMap.get(serverId);
        }
        String name = ServerRepository.getServerName(serverId);
        if (name == null) {
            throw new Exception(Misc.formatString(Language.multiServerManagerText.getNameByIdError, serverId));
        }
        cachedServerMap.put(serverId, name);
        return cachedServerMap.get(serverId);
    }

    public Integer getServerId(@NotNull String serverName) throws Exception {
        if (cachedServerMap.containsValue(serverName)) {
            return cachedServerMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(serverName))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow(() -> new Exception(Misc.formatString(Language.multiServerManagerText.getIdByNameError, serverName)));
        }
        Integer id = ServerRepository.getServerId(serverName);
        if (id == null) {
            throw new Exception(Misc.formatString(Language.multiServerManagerText.getIdByNameError, serverName));
        }
        cachedServerMap.put(id, serverName);
        return id;
    }
}
