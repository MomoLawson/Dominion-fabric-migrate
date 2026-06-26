package cn.lunadeer.dominion.utils.stui.components.buttons;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
public class UrlButton extends PermissionButton {
    private String url;
    public UrlButton(String text, String url) { this.text = text; this.url = url; }
    public MutableComponent build() { return buildGeneric(new ClickEvent.OpenUrl(java.net.URI.create(url))); }
}
