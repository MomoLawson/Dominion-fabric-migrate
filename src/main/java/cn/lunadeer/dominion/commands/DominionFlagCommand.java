package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.api.providers.DominionProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static cn.lunadeer.dominion.misc.Converts.*;

public class DominionFlagCommand {

    public static class DominionFlagCommandText extends ConfigurationPart {
        public String setEnvDescription = "Set environment flag for a dominion.";
        public String setGuestDescription = "Set guest privilege flag for a dominion.";
    }

    /**
     * Sets an environment flag on a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion to set the flag on
     * @param flagName     the name of the environment flag to set
     * @param valueStr     the value to set the flag to
     */
    public static void setEnv(CommandSourceStack source, String dominionName, String flagName, String valueStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            EnvFlag flag = toEnvFlag(flagName);
            boolean value = toBoolean(valueStr);
            DominionProvider.getInstance().setDominionEnvFlag(source, dominion, flag, value);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    /**
     * Sets a guest privilege flag on a dominion.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion to set the flag on
     * @param flagName     the name of the flag to set
     * @param valueStr     the value to set the flag to
     */
    public static void setGuest(CommandSourceStack source, String dominionName, String flagName, String valueStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            PriFlag flag = toPriFlag(flagName);
            boolean value = toBoolean(valueStr);
            DominionProvider.getInstance().setDominionGuestFlag(source, dominion, flag, value);
        } catch (Exception e) {
            sendError(source, e);
        }
    }

    // --- Helper ---

    private static void sendError(CommandSourceStack source, Throwable e) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, e);
        } catch (Exception ex) {
            Notification.error(source.level().getServer(), e.getMessage());
        }
    }
}
