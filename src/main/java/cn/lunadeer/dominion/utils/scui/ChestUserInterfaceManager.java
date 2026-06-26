package cn.lunadeer.dominion.utils.scui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChestUserInterfaceManager {
    private static ChestUserInterfaceManager instance;
    private final Map<UUID, Boolean> openScreens = new ConcurrentHashMap<>();

    public ChestUserInterfaceManager() { instance = this; }
    public static ChestUserInterfaceManager getInstance() { return instance; }
    public boolean isCuiScreen(UUID uuid) { return openScreens.containsKey(uuid); }
    public void openScreen(UUID uuid) { openScreens.put(uuid, true); }
    public void closeScreen(UUID uuid) { openScreens.remove(uuid); }
}
