package cn.lunadeer.dominion.utils.holograme;

public class HoloItem {
    private final String itemId;
    private final int count;

    public HoloItem(String itemId, int count) { this.itemId = itemId; this.count = count; }
    public String getItemId() { return itemId; }
    public int getCount() { return count; }
}
