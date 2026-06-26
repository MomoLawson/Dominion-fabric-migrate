package cn.lunadeer.dominion.utils.holograme;

public class HoloElement {
    private String text;
    private double offsetX, offsetY, offsetZ;

    public HoloElement(String text) { this.text = text; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public void setOffset(double x, double y, double z) { this.offsetX = x; this.offsetY = y; this.offsetZ = z; }
}
