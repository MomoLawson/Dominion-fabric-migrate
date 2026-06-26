package cn.lunadeer.dominion.inputters;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import net.minecraft.commands.CommandSourceStack;
public class CreateDominionInputter {
    public static class CreateDominionInputterText extends ConfigurationPart { public String button = "CREATE"; public String hint = "Enter name:"; }
    public static void createOn(CommandSourceStack source) {}
    public static FunctionalButton createTuiButtonOn(CommandSourceStack source) { return new FunctionalButton() { public void function() {} }; }
}
