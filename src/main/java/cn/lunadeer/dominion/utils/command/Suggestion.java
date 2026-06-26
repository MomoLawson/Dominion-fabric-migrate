package cn.lunadeer.dominion.utils.command;

import net.minecraft.commands.CommandSourceStack;
import java.util.List;

/**
 * Fabric port: functional interface for tab-completion suggestions.
 */
public interface Suggestion {
    List<String> get(CommandSourceStack source, String... preArguments);
}
