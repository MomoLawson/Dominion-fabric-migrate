package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.*;
import cn.lunadeer.dominion.api.dtos.flag.*;
import cn.lunadeer.dominion.api.events.dominion.modify.*;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.configuration.Language;
import cn.lunadeer.dominion.doos.*;
import cn.lunadeer.dominion.handler.DominionProviderHandler;
import cn.lunadeer.dominion.managers.TeleportManager;
import cn.lunadeer.dominion.misc.Converts;
import cn.lunadeer.dominion.misc.DominionException;
import cn.lunadeer.dominion.api.providers.DominionProvider;
import cn.lunadeer.dominion.uis.MainMenu;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.PermissionHelper;
import cn.lunadeer.dominion.utils.XLogger;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.UUID;

public class InitCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var root = Commands.literal("dominion");

            // /dominion menu
            root.then(Commands.literal("menu")
                .requires(src -> src.getPlayer() != null)
                .executes(ctx -> { MainMenu.show(ctx.getSource(), "1"); return Command.SINGLE_SUCCESS; })
            );

            // /dominion info
            root.then(Commands.literal("info")
                .requires(src -> src.getPlayer() != null)
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayer();
                    if (player == null) return 0;
                    DominionDTO d = CacheManager.instance.getDominion(getWorldUid(player),
                        player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                    if (d == null) { Notification.error(ctx.getSource(), "Not in any dominion."); return 0; }
                    PlayerDTO owner = CacheManager.instance.getPlayer(d.getOwner());
                    Notification.info(ctx.getSource(), "§e=== " + d.getName() + " ===");
                    if (owner != null) Notification.info(ctx.getSource(), "Owner: " + owner.getLastKnownName());
                    CuboidDTO c = d.getCuboid();
                    Notification.info(ctx.getSource(), "Size: " + (c.x2()-c.x1()) + "x" + (c.z2()-c.z1()) + " Height: " + c.y1() + "~" + c.y2());
                    return Command.SINGLE_SUCCESS;
                })
            );

            // /dominion create <name>
            root.then(Commands.literal("create")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        String name = StringArgumentType.getString(ctx, "name");
                        int r = cn.lunadeer.dominion.configuration.Configuration.autoCreateRadius;
                        if (r <= 0) r = 10;
                        var pos = player.blockPosition();
                        CuboidDTO cuboid = new CuboidDTO(pos.getX()-r, pos.getY()-r, pos.getZ()-r, pos.getX()+r, pos.getY()+r, pos.getZ()+r);
                        DominionProvider.getInstance().createDominion(ctx.getSource(), name, player.getUUID(), getWorldUid(player), cuboid, null, false);
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );

            // /dominion auto_create <name>
            root.then(Commands.literal("auto_create")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        String name = StringArgumentType.getString(ctx, "name");
                        int r = cn.lunadeer.dominion.configuration.Configuration.autoCreateRadius;
                        if (r <= 0) r = 10;
                        var pos = player.blockPosition();
                        CuboidDTO cuboid = new CuboidDTO(pos.getX()-r, pos.getY()-r, pos.getZ()-r, pos.getX()+r, pos.getY()+r, pos.getZ()+r);
                        DominionProvider.getInstance().createDominion(ctx.getSource(), name, player.getUUID(), getWorldUid(player), cuboid, null, false);
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );

            // /dominion delete <dominion> [force]
            root.then(Commands.literal("delete")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.literal("force")
                        .executes(ctx -> {
                            DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                            if (d != null) DominionProvider.getInstance().deleteDominion(ctx.getSource(), d, false, true);
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .executes(ctx -> {
                        DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                        if (d != null) DominionProvider.getInstance().deleteDominion(ctx.getSource(), d, false, false);
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );

            // /dominion rename <dominion> <newName>
            root.then(Commands.literal("rename")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("newName", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                            if (d != null) DominionProvider.getInstance().renameDominion(ctx.getSource(), d, StringArgumentType.getString(ctx, "newName"));
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            );

            // /dominion set_msg <dominion> <enter|leave> <message>
            root.then(Commands.literal("set_msg")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> { builder.suggest("enter"); builder.suggest("leave"); return builder.buildFuture(); })
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                                String type = StringArgumentType.getString(ctx, "type");
                                String msg = StringArgumentType.getString(ctx, "message");
                                if (d != null) {
                                    DominionSetMessageEvent.TYPE msgType = "enter".equalsIgnoreCase(type) ? DominionSetMessageEvent.TYPE.ENTER : DominionSetMessageEvent.TYPE.LEAVE;
                                    DominionProvider.getInstance().setDominionMessage(ctx.getSource(), d, msgType, msg);
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            );

            // /dominion set_tp <dominion>
            root.then(Commands.literal("set_tp")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.greedyString())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .executes(ctx -> {
                        DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                        if (d != null) DominionProvider.getInstance().setDominionTpLocation(ctx.getSource(), d, ctx.getSource().getPlayer().blockPosition().getX(), ctx.getSource().getPlayer().blockPosition().getY(), ctx.getSource().getPlayer().blockPosition().getZ());
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );

            // /dominion set_map_color <dominion> <color>
            root.then(Commands.literal("set_map_color")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("color", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                            if (d != null) { int[] c = Converts.toColor(StringArgumentType.getString(ctx, "color")); DominionProvider.getInstance().setDominionMapColor(ctx.getSource(), d, c[0], c[1], c[2]); }
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            );

            // /dominion give <dominion> <player> [force]
            root.then(Commands.literal("give")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((ctx, builder) -> { suggestPlayers(ctx, builder); return builder.buildFuture(); })
                        .then(Commands.literal("force")
                            .executes(ctx -> {
                                DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                                PlayerDTO p = Converts.toPlayerDTO(StringArgumentType.getString(ctx, "player"));
                                if (d != null && p != null) DominionProvider.getInstance().transferDominion(ctx.getSource(), d, p, true);
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                        .executes(ctx -> {
                            DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                            PlayerDTO p = Converts.toPlayerDTO(StringArgumentType.getString(ctx, "player"));
                            if (d != null && p != null) DominionProvider.getInstance().transferDominion(ctx.getSource(), d, p, false);
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            );

            // /dominion tp <dominion>
            root.then(Commands.literal("tp")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.greedyString())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                        if (d != null) TeleportManager.teleportToDominion(player, d);
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );

            // /dominion set_env <dominion> <flag> <value>
            root.then(Commands.literal("set_env")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("flag", StringArgumentType.word())
                        .suggests((ctx, builder) -> { Flags.getAllEnvFlagsEnable().stream().map(EnvFlag::getFlagName).forEach(builder::suggest); return builder.buildFuture(); })
                        .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                                EnvFlag f = Converts.toEnvFlag(StringArgumentType.getString(ctx, "flag"));
                                if (d != null && f != null) DominionProvider.getInstance().setDominionEnvFlag(ctx.getSource(), d, f, BoolArgumentType.getBool(ctx, "value"));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            );

            // /dominion set_guest <dominion> <flag> <value>
            root.then(Commands.literal("set_guest")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("flag", StringArgumentType.word())
                        .suggests((ctx, builder) -> { Flags.getAllPriFlagsEnable().stream().filter(f -> !f.equals(Flags.ADMIN)).map(PriFlag::getFlagName).forEach(builder::suggest); return builder.buildFuture(); })
                        .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                                PriFlag f = Converts.toPriFlag(StringArgumentType.getString(ctx, "flag"));
                                if (d != null && f != null) DominionProvider.getInstance().setDominionGuestFlag(ctx.getSource(), d, f, BoolArgumentType.getBool(ctx, "value"));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            );

            // /dominion member_add <dominion> <player>
            root.then(Commands.literal("member_add")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("player", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> { suggestPlayers(ctx, builder); return builder.buildFuture(); })
                        .executes(ctx -> {
                            Notification.info(ctx.getSource(), "Member added (use UI for full management)");
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            );

            // /dominion member_remove <dominion> <player>
            root.then(Commands.literal("member_remove")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("player", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            Notification.info(ctx.getSource(), "Member removed (use UI for full management)");
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            );

            // /dominion group_create <dominion> <name>
            root.then(Commands.literal("group_create")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String domName = StringArgumentType.getString(ctx, "dominion");
                            String name = StringArgumentType.getString(ctx, "name");
                            DominionDTO d = Converts.toDominionDTO(domName);
                            if (d != null) { GroupDOO.create(name, d); Notification.info(ctx.getSource(), "Group " + name + " created"); }
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            );

            // /dominion resize <dominion> <expand|contract> <size> [direction]
            root.then(Commands.literal("resize")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("dominion", StringArgumentType.word())
                    .suggests((ctx, builder) -> { suggestDominions(ctx, builder); return builder.buildFuture(); })
                    .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> { builder.suggest("expand"); builder.suggest("contract"); return builder.buildFuture(); })
                        .then(Commands.argument("size", IntegerArgumentType.integer(1))
                            .executes(ctx -> {
                                DominionDTO d = Converts.toDominionDTO(StringArgumentType.getString(ctx, "dominion"));
                                String type = StringArgumentType.getString(ctx, "type");
                                int size = IntegerArgumentType.getInteger(ctx, "size");
                                if (d != null) {
                                    DominionReSizeEvent.TYPE rt = "expand".equalsIgnoreCase(type) ? DominionReSizeEvent.TYPE.EXPAND : DominionReSizeEvent.TYPE.CONTRACT;
                                    DominionProvider.getInstance().resizeDominion(ctx.getSource(), d, rt, DominionReSizeEvent.DIRECTION.NORTH, size);
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            );

            // /dominion expand <size>
            root.then(Commands.literal("expand")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("size", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        DominionDTO d = CacheManager.instance.getDominion(getWorldUid(player), player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                        if (d != null) DominionProvider.getInstance().resizeDominion(ctx.getSource(), d, DominionReSizeEvent.TYPE.EXPAND, DominionReSizeEvent.DIRECTION.NORTH, IntegerArgumentType.getInteger(ctx, "size"));
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );

            // /dominion contract <size>
            root.then(Commands.literal("contract")
                .requires(src -> src.getPlayer() != null)
                .then(Commands.argument("size", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        DominionDTO d = CacheManager.instance.getDominion(getWorldUid(player), player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
                        if (d != null) DominionProvider.getInstance().resizeDominion(ctx.getSource(), d, DominionReSizeEvent.TYPE.CONTRACT, DominionReSizeEvent.DIRECTION.NORTH, IntegerArgumentType.getInteger(ctx, "size"));
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );

            // /dominion switch_ui
            root.then(Commands.literal("switch_ui")
                .requires(src -> src.getPlayer() != null)
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayer();
                    if (player == null) return 0;
                    PlayerDTO p = CacheManager.instance.getPlayer(player.getUUID());
                    if (p != null) {
                        PlayerDTO.UI_TYPE current = p.getUiPreference();
                        try { p.setUiPreference(current == PlayerDTO.UI_TYPE.TUI ? PlayerDTO.UI_TYPE.CUI : PlayerDTO.UI_TYPE.TUI); } catch (Exception ex) {}
                        Notification.info(ctx.getSource(), "UI switched to " + p.getUiPreference().name());
                    }
                    return Command.SINGLE_SUCCESS;
                })
            );

            // /dominion reload
            root.then(Commands.literal("reload")
                .requires(src -> { try { return src.getPlayer() != null && PermissionHelper.hasPermissionLevel(src.getPlayer(), 4); } catch (Exception e) { return false; } })
                .executes(ctx -> {
                    try { cn.lunadeer.dominion.configuration.Configuration.loadConfigurationAndDatabase(null); Notification.info(ctx.getSource(), "Configuration reloaded."); }
                    catch (Exception e) { Notification.error(ctx.getSource(), "Reload failed: " + e.getMessage()); }
                    return Command.SINGLE_SUCCESS;
                })
            );

            // /dominion title_use
            root.then(Commands.literal("title_use").executes(ctx -> { Notification.info(ctx.getSource(), "Use UI for title management"); return Command.SINGLE_SUCCESS; }));

            // /dominion template_create
            root.then(Commands.literal("template_create")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .executes(ctx -> { Notification.info(ctx.getSource(), "Use UI for template management"); return Command.SINGLE_SUCCESS; })
                )
            );

            // /dominion migrate
            root.then(Commands.literal("migrate").executes(ctx -> { Notification.info(ctx.getSource(), "Migration not available on Fabric"); return Command.SINGLE_SUCCESS; }));
            root.then(Commands.literal("migrate_all").executes(ctx -> { Notification.info(ctx.getSource(), "Migration not available on Fabric"); return Command.SINGLE_SUCCESS; }));

            dispatcher.register(root);
            XLogger.info("All Dominion commands registered.");
        });
    }

    private static boolean hasAdminPermission(CommandSourceStack source) {
        try { ServerPlayer p = source.getPlayer(); return p != null && PermissionHelper.hasPermissionLevel(p, 4); }
        catch (Exception e) { return false; }
    }

    private static void suggestDominions(CommandContext<CommandSourceStack> ctx, com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        try { ServerPlayer p = ctx.getSource().getPlayer(); if (p != null) CacheManager.instance.getPlayerManageDominionNames(p.getUUID()).forEach(builder::suggest); }
        catch (Exception ignored) {}
    }

    private static void suggestPlayers(CommandContext<CommandSourceStack> ctx, com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        try { CacheManager.instance.getPlayerNames().forEach(builder::suggest); }
        catch (Exception ignored) {}
    }

    private static UUID getWorldUid(ServerPlayer player) {
        return UUID.nameUUIDFromBytes(player.level().dimension().identifier().toString().getBytes());
    }
}
