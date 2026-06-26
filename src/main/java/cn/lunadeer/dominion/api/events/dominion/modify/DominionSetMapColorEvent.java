package cn.lunadeer.dominion.api.events.dominion.modify;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event data object for setting the map color of a Dominion.
 * <p>
 * Color is stored as RGB integers instead of Bukkit Color.
 */
public class DominionSetMapColorEvent extends DominionModifyEvent {

    private final int oldColorR;
    private final int oldColorG;
    private final int oldColorB;
    private int newColorR;
    private int newColorG;
    private int newColorB;

    public DominionSetMapColorEvent(@NotNull CommandSourceStack operator,
                                    @NotNull DominionDTO dominion,
                                    int r, int g, int b) {
        super(operator, dominion);
        this.oldColorR = dominion.getColorR();
        this.oldColorG = dominion.getColorG();
        this.oldColorB = dominion.getColorB();
        this.newColorR = r;
        this.newColorG = g;
        this.newColorB = b;
    }

    public int getOldColorR() { return oldColorR; }
    public int getOldColorG() { return oldColorG; }
    public int getOldColorB() { return oldColorB; }

    public int getNewColorR() { return newColorR; }
    public int getNewColorG() { return newColorG; }
    public int getNewColorB() { return newColorB; }

    public void setNewColor(int r, int g, int b) {
        this.newColorR = r;
        this.newColorG = g;
        this.newColorB = b;
    }
}
