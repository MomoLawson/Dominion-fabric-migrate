package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.utils.XLogger;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.Command;
import static net.minecraft.commands.Commands.literal;

public class InitCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("dominion")
                .then(literal("menu")
                    .executes(ctx -> {
                        cn.lunadeer.dominion.uis.MainMenu.show(ctx.getSource(), "1");
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(literal("info")
                    .executes(ctx -> {
                        cn.lunadeer.dominion.uis.dominion.manage.Info.show(ctx.getSource(), "0");
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
            XLogger.info("Dominion commands registered.");
        });
    }
}
