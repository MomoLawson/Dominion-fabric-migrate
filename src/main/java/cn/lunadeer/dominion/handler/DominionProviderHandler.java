package cn.lunadeer.dominion.handler;

import cn.lunadeer.dominion.api.dtos.CuboidDTO;
import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.doos.DominionDOO;
import cn.lunadeer.dominion.api.events.dominion.modify.*;
import cn.lunadeer.dominion.misc.Asserts;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.api.providers.DominionProvider;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Fabric implementation of DominionProvider.
 * Handles all dominion CRUD operations.
 */
public class DominionProviderHandler extends DominionProvider {

    public static class DominionProviderHandlerText extends ConfigurationPart {
        public String createSuccess = "Create dominion {0} success.";
        public String createFailed = "Create dominion {0} failed, reason: {1}";
        public String expandSuccess = "Expand dominion {0} success.";
        public String expandFailed = "Expand dominion {0} failed, reason: {1}";
        public String contractSuccess = "Contract dominion {0} success.";
        public String contractFailed = "Contract dominion {0} failed, reason: {1}";
        public String deleteSuccess = "Delete dominion {0} success.";
        public String deleteFailed = "Delete dominion {0} failed, reason: {1}";
        public String deleteConfirm = "Use command '/dominion delete {0} force' to confirm.";
        public String renameSuccess = "Rename dominion {0} to {1} success.";
        public String renameFailed = "Rename dominion {0} failed, reason: {1}";
        public String sameName = "The new name is the same as the old name.";
        public String giveSuccess = "Give dominion {0} to {1} success.";
        public String giveFailed = "Give dominion {0} failed, reason: {1}";
        public String tpLocationSetSuccess = "Set teleport location for {0} success.";
        public String setEnterMessageSuccess = "Set enter message for {0} success.";
        public String setLeaveMessageSuccess = "Set leave message for {0} success.";
        public String SetMapColorSuccess = "Set map color for {0} success.";
        public String setEnvFlagSuccess = "Set env flag {0} to {1} success.";
        public String setGuestFlagSuccess = "Set guest flag {0} to {1} success.";
        public String setOwnerGlowFlagSuccess = "Set owner glow to {0} success.";
    }

    public DominionProviderHandler() {
        instance = this;
        XLogger.info("DominionProviderHandler initialized");
    }

    @Override
    public CompletableFuture<DominionDTO> createDominion(@NotNull CommandSourceStack operator,
                                                         @NotNull String name, @NotNull UUID owner,
                                                         @NotNull UUID worldUid, @NotNull CuboidDTO cuboid,
                                                         @Nullable DominionDTO parent, boolean skipEconomy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Asserts.assertDominionNameNotExists(name, owner);
                DominionDOO toBeCreated = new DominionDOO();
                DominionDTO inserted = DominionDOO.insert(toBeCreated);
                if (inserted != null) {
                    Notification.info(operator, Language.dominionProviderHandlerText.createSuccess, name);
                }
                return inserted;
            } catch (Exception e) {
                Notification.error(operator, Language.dominionProviderHandlerText.createFailed, name, e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> resizeDominion(@NotNull CommandSourceStack operator,
                                                         @NotNull DominionDTO dominion,
                                                         @NotNull DominionReSizeEvent.TYPE type,
                                                         @NotNull DominionReSizeEvent.DIRECTION direction,
                                                         int size) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Notification.info(operator, "Dominion " + type.name().toLowerCase() + " by " + size + " blocks");
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> deleteDominion(@NotNull CommandSourceStack operator,
                                                         @NotNull DominionDTO dominion,
                                                         boolean skipEconomy,
                                                         boolean force) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (operator.getPlayer() != null) Asserts.assertDominionOwner(operator.getPlayer(), dominion);
                DominionDOO.deleteById(dominion.getId());
                Notification.info(operator, Language.dominionProviderHandlerText.deleteSuccess, dominion.getName());
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, Language.dominionProviderHandlerText.deleteFailed, dominion.getName(), e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> renameDominion(@NotNull CommandSourceStack operator,
                                                         @NotNull DominionDTO dominion,
                                                         @NotNull String newName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (operator.getPlayer() != null) Asserts.assertDominionOwner(operator.getPlayer(), dominion);
                dominion.setName(newName);
                Notification.info(operator, Language.dominionProviderHandlerText.renameSuccess, dominion.getName(), newName);
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, Language.dominionProviderHandlerText.renameFailed, dominion.getName(), e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> transferDominion(@NotNull CommandSourceStack operator,
                                                           @NotNull DominionDTO dominion,
                                                           @NotNull PlayerDTO newOwner,
                                                           boolean force) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (operator.getPlayer() != null) Asserts.assertDominionOwner(operator.getPlayer(), dominion);
                dominion.setOwner(newOwner.getUuid());
                Notification.info(operator, Language.dominionProviderHandlerText.giveSuccess, dominion.getName(), newOwner.getLastKnownName());
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, Language.dominionProviderHandlerText.giveFailed, dominion.getName(), e.getMessage());
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> setDominionTpLocation(@NotNull CommandSourceStack operator,
                                                                @NotNull DominionDTO dominion,
                                                                @NotNull int x, int y, int z) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (operator.getPlayer() != null) Asserts.assertDominionOwner(operator.getPlayer(), dominion);
                dominion.setTpLocation(x, y, z);
                Notification.info(operator, Language.dominionProviderHandlerText.tpLocationSetSuccess, dominion.getName());
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> setDominionMessage(@NotNull CommandSourceStack operator,
                                                             @NotNull DominionDTO dominion,
                                                             @NotNull DominionSetMessageEvent.TYPE type,
                                                             @NotNull String newMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (operator.getPlayer() != null) Asserts.assertDominionOwner(operator.getPlayer(), dominion);
                if (type == DominionSetMessageEvent.TYPE.ENTER) {
                    dominion.setJoinMessage(newMessage);
                    Notification.info(operator, Language.dominionProviderHandlerText.setEnterMessageSuccess, dominion.getName());
                } else {
                    dominion.setLeaveMessage(newMessage);
                    Notification.info(operator, Language.dominionProviderHandlerText.setLeaveMessageSuccess, dominion.getName());
                }
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> setDominionMapColor(@NotNull CommandSourceStack operator,
                                                              @NotNull DominionDTO dominion,
                                                              int r, int g, int b) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (operator.getPlayer() != null) Asserts.assertDominionOwner(operator.getPlayer(), dominion);
                dominion.setColor(r, g, b);
                Notification.info(operator, Language.dominionProviderHandlerText.SetMapColorSuccess, dominion.getName());
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> setDominionEnvFlag(@NotNull CommandSourceStack operator,
                                                             @NotNull DominionDTO dominion,
                                                             @NotNull EnvFlag flag,
                                                             boolean newValue) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                dominion.setEnvFlag(flag.getFlagName(), newValue);
                Notification.info(operator, Language.dominionProviderHandlerText.setEnvFlagSuccess, flag.getDisplayName(), newValue);
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> setDominionGuestFlag(@NotNull CommandSourceStack operator,
                                                               @NotNull DominionDTO dominion,
                                                               @NotNull PriFlag flag,
                                                               boolean newValue) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                dominion.setGuestFlag(flag.getFlagName(), newValue);
                Notification.info(operator, Language.dominionProviderHandlerText.setGuestFlagSuccess, flag.getDisplayName(), newValue);
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, e);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<DominionDTO> setDominionOwnerGlow(@NotNull CommandSourceStack operator,
                                                               @NotNull DominionDTO dominion,
                                                               boolean ownerGlow) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                dominion.setOwnerGlow(ownerGlow);
                Notification.info(operator, Language.dominionProviderHandlerText.setOwnerGlowFlagSuccess, ownerGlow);
                return dominion;
            } catch (Exception e) {
                Notification.error(operator, e);
                return null;
            }
        });
    }
}
