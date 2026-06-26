package cn.lunadeer.dominion.utils.stui.inputter;
import net.minecraft.commands.CommandSourceStack;
public abstract class InputterRunner {
    public InputterRunner(CommandSourceStack source, String hint) {}
    public abstract void run(String input);
}
