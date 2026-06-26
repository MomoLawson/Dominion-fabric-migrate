package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.MemberDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.MemberDOO;
import cn.lunadeer.dominion.doos.TemplateDOO;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static cn.lunadeer.dominion.misc.Asserts.assertDominionAdmin;
import static cn.lunadeer.dominion.misc.Asserts.assertDominionOwner;
import static cn.lunadeer.dominion.misc.Converts.*;

public class TemplateCommand {

    public static class TemplateCommandText extends ConfigurationPart {
        public String nameNotValid = "Template name cannot contain space";
        public String templateNameExist = "Template {0} already exists";
        public String createTemplateFail = "Failed to create template, reason: {0}";
        public String createTemplateSuccess = "Successfully created template {0}";

        public String templateNotExist = "Template {0} does not exist";

        public String deleteTemplateSuccess = "Successfully deleted template {0}";
        public String deleteTemplateFail = "Failed to delete template, reason: {0}";

        public String applyTemplateSuccess = "Successfully applied template {0} to {1}";
        public String applyTemplateFail = "Failed to apply template, reason: {0}";

        public String setFlagSuccess = "Successfully set {0} flag of template {1} to {2}";
        public String setFlagFail = "Failed to set flag, reason: {0}";

        public String renameTemplateSuccess = "Successfully renamed template to {0}";
        public String renameTemplateFail = "Failed to rename template, reason: {0}";

        public String createTemplateDescription = "Create a new privilege template.";
        public String deleteTemplateDescription = "Delete an existing privilege template.";
        public String setTemplateFlagDescription = "Set a privilege flag in a template.";
        public String memberApplyTemplateDescription = "Apply a privilege template to a member.";
        public String renameTemplateDescription = "Rename an existing privilege template.";
    }

    /**
     * Creates a new template.
     *
     * @param source       the command source
     * @param templateName the name of the template to be created
     */
    public static void createTemplate(CommandSourceStack source, String templateName) {
        try {
            ServerPlayer player = source.getPlayer();
            if (templateName.contains(" ")) {
                throw new DominionException(Language.templateCommandText.nameNotValid);
            }
            List<TemplateDOO> templates = TemplateDOO.selectAll(player.getUUID());
            if (templates.stream().anyMatch(t -> t.getName().equals(templateName))) {
                throw new DominionException(Language.templateCommandText.templateNameExist, templateName);
            }
            TemplateDOO.create(player.getUUID(), templateName);
            Notification.info(player, Language.templateCommandText.createTemplateSuccess, templateName);
        } catch (Exception e) {
            sendError(source, Language.templateCommandText.createTemplateFail, e.getMessage());
        }
    }

    /**
     * Deletes an existing template.
     *
     * @param source       the command source
     * @param templateName the name of the template to be deleted
     */
    public static void deleteTemplate(CommandSourceStack source, String templateName) {
        try {
            ServerPlayer player = source.getPlayer();
            TemplateDOO template = TemplateDOO.select(player.getUUID(), templateName);
            if (template == null) {
                throw new DominionException(Language.templateCommandText.templateNotExist, templateName);
            }
            TemplateDOO.delete(player.getUUID(), templateName);
            Notification.info(player, Language.templateCommandText.deleteTemplateSuccess, templateName);
        } catch (Exception e) {
            sendError(source, Language.templateCommandText.deleteTemplateFail, e.getMessage());
        }
    }

    /**
     * Sets a privilege flag in a template.
     *
     * @param source       the command source
     * @param templateName the name of the template
     * @param flagName     the name of the privilege flag
     * @param valueStr     the value to set
     */
    public static void setTemplateFlag(CommandSourceStack source, String templateName, String flagName, String valueStr) {
        try {
            ServerPlayer player = source.getPlayer();
            boolean value = toBoolean(valueStr);
            PriFlag flag = toPriFlag(flagName);
            TemplateDOO template = TemplateDOO.select(player.getUUID(), templateName);
            if (template == null) {
                throw new DominionException(Language.templateCommandText.templateNotExist, templateName);
            }
            template.setFlagValue(flag, value);
            Notification.info(player, Language.templateCommandText.setFlagSuccess, flagName, templateName, valueStr);
        } catch (Exception e) {
            sendError(source, Language.templateCommandText.setFlagFail, e.getMessage());
        }
    }

    /**
     * Applies a privilege template to a member.
     *
     * @param source       the command source
     * @param dominionName the name of the dominion
     * @param playerName   the name of the member
     * @param templateName the name of the template to apply
     */
    public static void memberApplyTemplate(CommandSourceStack source, String dominionName, String playerName, String templateName) {
        try {
            ServerPlayer player = source.getPlayer();
            TemplateDOO template = TemplateDOO.select(player.getUUID(), templateName);
            if (template == null) {
                throw new DominionException(Language.templateCommandText.templateNotExist, templateName);
            }
            DominionDTO dominion = toDominionDTO(dominionName);
            if (template.getFlagValue(Flags.ADMIN)) {
                assertDominionOwner(player, dominion);
            } else {
                assertDominionAdmin(player, dominion);
            }
            MemberDTO member = toMemberDTO(dominion, playerName);
            ((MemberDOO) member).applyTemplate(template);
            Notification.info(player, Language.templateCommandText.applyTemplateSuccess, templateName, playerName);
        } catch (Exception e) {
            sendError(source, Language.templateCommandText.applyTemplateFail, e.getMessage());
        }
    }

    /**
     * Renames an existing template.
     *
     * @param source           the command source
     * @param templateName     the current name of the template
     * @param newTemplateName  the new name for the template
     */
    public static void renameTemplate(CommandSourceStack source, String templateName, String newTemplateName) {
        try {
            ServerPlayer player = source.getPlayer();
            TemplateDOO template = TemplateDOO.select(player.getUUID(), templateName);
            if (template == null) {
                throw new DominionException(Language.templateCommandText.templateNotExist, templateName);
            }
            if (newTemplateName.contains(" ")) {
                throw new DominionException(Language.templateCommandText.nameNotValid);
            }
            template.setName(newTemplateName);
            Notification.info(player, Language.templateCommandText.renameTemplateSuccess, newTemplateName);
        } catch (Exception e) {
            sendError(source, Language.templateCommandText.renameTemplateFail, e.getMessage());
        }
    }

    // --- Helper ---

    private static void sendError(CommandSourceStack source, String msg, Object... args) {
        try {
            ServerPlayer player = source.getPlayer();
            Notification.error(player, msg, args);
        } catch (Exception ex) {
            Notification.error(source.level().getServer(), msg, args);
        }
    }
}
