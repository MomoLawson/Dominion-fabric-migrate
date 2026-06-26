package cn.lunadeer.dominion.utils.scui;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import java.util.List;
public class ChestButton {
    private ItemStack item;
    private Runnable action;
    public ChestButton(ItemStack item) { this.item = item; }
    public ChestButton setAction(Runnable action) { this.action = action; return this; }
    public ItemStack getItem() { return item; }
    public void click() { if (action != null) action.run(); }
}
