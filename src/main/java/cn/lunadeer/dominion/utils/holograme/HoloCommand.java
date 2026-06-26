package cn.lunadeer.dominion.utils.holograme;

import cn.lunadeer.dominion.misc.CommandArguments;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class HoloCommand {
    public static int execute(CommandContext<CommandSourceStack> context) {
        // Debug hologram command
        return Command.SINGLE_SUCCESS;
    }
}
