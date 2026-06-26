package cn.lunadeer.dominion.utils;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.doos.DominionDOO;
import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import net.minecraft.server.MinecraftServer;





import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Residence plugin migration utility for Fabric.
 * Reads Residence save files and converts them to Dominion format.
 */
public class ResMigration {

    public static class ResidenceNode {
        public UUID owner;
        public String ownerName;
        public String worldName;
        public String name;
        public int x1, y1, z1, x2, y2, z2;
        public int tpX, tpY, tpZ;
        public String joinMessage = "";
        public String leaveMessage = "";
        public List<ResidenceNode> children = new ArrayList<>();
    }

    private static final List<ResidenceNode> extractedData = new ArrayList<>();

    /**
     * Extract Residence data from save files.
     * Looks for Residence/Save/Worlds/*.yml files.
     */
    public static List<ResidenceNode> extractFromResidence(File serverDir) {
        extractedData.clear();
        File resDir = new File(serverDir, "Residence/Save/Worlds");
        if (!resDir.exists()) {
            XLogger.info("Residence save directory not found: {0}", resDir.getAbsolutePath());
            return extractedData;
        }

        File[] files = resDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            XLogger.info("No Residence save files found");
            return extractedData;
        }

        for (File file : files) {
            try {
                List<ResidenceNode> worldData = processWorldFile(file);
                extractedData.addAll(worldData);
                XLogger.info("Processed {0}: {1} residences", file.getName(), worldData.size());
            } catch (Exception e) {
                XLogger.error("Failed to process {0}: {1}", file.getName(), e.getMessage());
            }
        }

        XLogger.info("Total extracted: {0} residences from {1} world files", extractedData.size(), files.length);
        return extractedData;
    }

    /**
     * Migrate all extracted Residence data to Dominion format.
     */
    public static int migrateAll() {
        int migrated = 0;
        for (ResidenceNode node : extractedData) {
            try {
                DominionDTO dominion = migrateNode(node, null);
                if (dominion != null) {
                    migrated++;
                    // Migrate children as sub-dominions
                    for (ResidenceNode child : node.children) {
                        DominionDTO childDom = migrateNode(child, dominion);
                        if (childDom != null) migrated++;
                    }
                }
            } catch (Exception e) {
                XLogger.error("Failed to migrate {0}: {1}", node.name, e.getMessage());
            }
        }
        XLogger.info("Migration complete: {0} dominions created", migrated);
        return migrated;
    }

    /**
     * Migrate a single Residence node to Dominion format.
     */
    private static DominionDTO migrateNode(ResidenceNode node, DominionDTO parent) {
        try {
            UUID worldUid = UUID.nameUUIDFromBytes(node.worldName.getBytes());
            CuboidDTO cuboid = new CuboidDTO(node.x1, node.y1, node.z1, node.x2, node.y2, node.z2);
            Integer parentDomId = parent != null ? parent.getId() : -1;

            DominionDOO dominion = new DominionDOO(node.owner, node.name, worldUid, cuboid, parentDomId);
            dominion.setJoinMessage(node.joinMessage);
            dominion.setLeaveMessage(node.leaveMessage);
            dominion.setTpLocation(node.tpX, node.tpY, node.tpZ);

            DominionDTO result = DominionDOO.insert(dominion);
            if (result != null) {
                XLogger.info("Migrated: {0} (owner: {1})", node.name, node.ownerName);
            }
            return result;
        } catch (Exception e) {
            XLogger.error("Failed to migrate node {0}: {1}", node.name, e.getMessage());
            return null;
        }
    }

    /**
     * Process a single world save file.
     */
    @SuppressWarnings("unchecked")
    private static List<ResidenceNode> processWorldFile(File file) throws Exception {
        List<ResidenceNode> result = new ArrayList<>();
        String worldName = file.getName().replace("res_", "").replace(".yml", "");

        Yaml yaml = new Yaml();
        try (InputStream input = Files.newInputStream(file.toPath())) {
            Map<String, Object> data = yaml.load(input);
            if (data == null) return result;

            // Parse messages
            Map<Integer, String[]> messages = new HashMap<>();
            Map<Integer, Object> messagesRaw = (Map<Integer, Object>) data.get("Messages");
            if (messagesRaw != null) {
                for (Map.Entry<Integer, Object> entry : messagesRaw.entrySet()) {
                    Map<String, String> msg = (Map<String, String>) entry.getValue();
                    String enter = msg.getOrDefault("EnterMessage", "")
                        .replace("%owner", "{OWNER}").replace("%residence", "{DOM}").replace("%player", "{PLAYER}");
                    String leave = msg.getOrDefault("LeaveMessage", "")
                        .replace("%owner", "{OWNER}").replace("%residence", "{DOM}").replace("%player", "{PLAYER}");
                    messages.put(entry.getKey(), new String[]{enter, leave});
                }
            }

            // Parse residences
            Map<String, Object> residences = (Map<String, Object>) data.get("Residences");
            if (residences == null) return result;

            for (Map.Entry<String, Object> entry : residences.entrySet()) {
                try {
                    ResidenceNode node = parseResidence(entry.getKey(), worldName, (Map<String, Object>) entry.getValue(), messages);
                    if (node != null) result.add(node);
                } catch (Exception e) {
                    XLogger.warn("Skipping residence {0}: {1}", entry.getKey(), e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * Parse a single Residence entry.
     */
    @SuppressWarnings("unchecked")
    private static ResidenceNode parseResidence(String name, String worldName, Map<String, Object> data, Map<Integer, String[]> messages) {
        ResidenceNode node = new ResidenceNode();
        node.name = name;
        node.worldName = worldName;

        // Parse permissions
        Map<String, Object> perms = (Map<String, Object>) data.get("Permissions");
        if (perms != null) {
            String ownerUuid = (String) perms.get("OwnerUUID");
            if (ownerUuid == null || ownerUuid.equals("00000000-0000-0000-0000-000000000000")) {
                return null; // Skip server-owned residences
            }
            try {
                node.owner = UUID.fromString(ownerUuid);
            } catch (IllegalArgumentException e) {
                return null;
            }
            node.ownerName = (String) perms.getOrDefault("OwnerLastKnownName", "Unknown");
        }

        // Parse areas (coordinates)
        Map<String, String> areas = (Map<String, String>) data.get("Areas");
        if (areas != null && !areas.isEmpty()) {
            String areaStr = areas.values().iterator().next();
            String[] coords = areaStr.split(":");
            if (coords.length >= 6) {
                node.x1 = Integer.parseInt(coords[0]);
                node.y1 = Integer.parseInt(coords[1]);
                node.z1 = Integer.parseInt(coords[2]);
                node.x2 = Integer.parseInt(coords[3]);
                node.y2 = Integer.parseInt(coords[4]);
                node.z2 = Integer.parseInt(coords[5]);
            }
        }

        // Parse TP location
        String tpLoc = (String) data.get("TPLoc");
        if (tpLoc != null) {
            String[] tpCoords = tpLoc.split(":");
            if (tpCoords.length >= 3) {
                node.tpX = Integer.parseInt(tpCoords[0]);
                node.tpY = Integer.parseInt(tpCoords[1]);
                node.tpZ = Integer.parseInt(tpCoords[2]);
            }
        } else {
            // Default TP to center of residence
            node.tpX = (node.x1 + node.x2) / 2;
            node.tpY = (node.y1 + node.y2) / 2;
            node.tpZ = (node.z1 + node.z2) / 2;
        }

        // Parse messages
        int msgIndex = data.containsKey("Messages") ? (int) data.get("Messages") : 0;
        String[] msg = messages.get(msgIndex);
        if (msg != null) {
            node.joinMessage = msg[0];
            node.leaveMessage = msg[1];
        }

        // Parse subzones
        Map<String, Object> subzones = (Map<String, Object>) data.get("Subzones");
        if (subzones != null) {
            for (Map.Entry<String, Object> entry : subzones.entrySet()) {
                ResidenceNode child = parseResidence(entry.getKey(), worldName, (Map<String, Object>) entry.getValue(), messages);
                if (child != null) node.children.add(child);
            }
        }

        return node;
    }

    public static List<ResidenceNode> getExtractedData() { return extractedData; }
    public static boolean hasData() { return !extractedData.isEmpty(); }
}
