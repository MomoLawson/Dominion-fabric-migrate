package cn.lunadeer.dominion.doos;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import java.net.URL;
import java.util.*;
public class PlayerDOO implements PlayerDTO {
    private Integer id; private UUID uuid; private String lastKnownName;
    public Integer getId() { return id; }
    public UUID getUuid() { return uuid; }
    public String getLastKnownName() { return lastKnownName; }
    public PlayerDTO.UI_TYPE getUiPreference() { return PlayerDTO.UI_TYPE.TUI; }
    public void setUiPreference(PlayerDTO.UI_TYPE type) {}
    public Integer getUsingGroupTitleID() { return 0; }
    public void setUsingGroupTitleID(Integer id) {}
    public URL getSkinUrl() { try { return new URL("https://"); } catch (Exception e) { return null; } }
    public void setSkinUrl(URL url) {}
    public PlayerDTO updateLastKnownName(String name, URL skinUrl) { this.lastKnownName = name; return this; }
    public static List<PlayerDOO> selectAll() { return new ArrayList<>(); }
}
