package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.utils.PermissionHelper;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionReSizeEvent;
import cn.lunadeer.dominion.api.events.dominion.modify.DominionSetMessageEvent;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.utils.XLogger;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static cn.lunadeer.dominion.Dominion.adminPermission;
import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Registers all Dominion commands with Brigadier.
 * All commands are registered under the /dominion root command.
 */
public class InitCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // Build the root command node
            var root = literal("dominion");

            // === Administrator Commands (require admin permission) ===

            // /dominion reload [config|cache|all]
            root.then(literal("reload")
                    .requires(src -> hasAdminPermission(src))
                    .then(argument("type", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                builder.suggest("config");
                                builder.suggest("cache");
                                builder.suggest("all");
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                String type = StringArgumentType.getString(ctx, "type");
                                AdministratorCommand.handleReload(ctx.getSource(), type);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .executes(ctx -> {
                        AdministratorCommand.handleReload(ctx.getSource(), "all");
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // /dominion export [mca|db]
            root.then(literal("export")
                    .requires(src -> hasAdminPermission(src))
                    .then(argument("type", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                builder.suggest("mca");
                                builder.suggest("db");
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                String type = StringArgumentType.getString(ctx, "type");
                                AdministratorCommand.handleExport(ctx.getSource(), type);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .executes(ctx -> {
                        AdministratorCommand.handleExport(ctx.getSource(), "db");
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // /dominion import [confirm]
            root.then(literal("import")
                    .requires(src -> hasAdminPermission(src))
                    .then(argument("confirm", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                builder.suggest("confirm");
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                String confirm = StringArgumentType.getString(ctx, "confirm");
                                AdministratorCommand.handleImport(ctx.getSource(), "confirm".equals(confirm));
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .executes(ctx -> {
                        AdministratorCommand.handleImport(ctx.getSource(), false);
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // /dominion update_language [confirm]
            root.then(literal("update_language")
                    .requires(src -> hasAdminPermission(src))
                    .then(argument("confirm", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                builder.suggest("confirm");
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                String confirm = StringArgumentType.getString(ctx, "confirm");
                                AdministratorCommand.handleUpdateLanguage(ctx.getSource(), "confirm".equals(confirm));
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .executes(ctx -> {
                        AdministratorCommand.handleUpdateLanguage(ctx.getSource(), false);
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // === Dominion Create Commands ===

            // /dominion create <name>
            root.then(literal("create")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "name");
                                DominionCreateCommand.create(ctx.getSource(), name);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion create_sub <name> <parent>
            root.then(literal("create_sub")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.word())
                            .then(argument("parent", StringArgumentType.greedyString())
                                    .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                                    .executes(ctx -> {
                                        String name = StringArgumentType.getString(ctx, "name");
                                        String parent = StringArgumentType.getString(ctx, "parent");
                                        DominionCreateCommand.createSub(ctx.getSource(), name, parent);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion auto_create <name>
            root.then(literal("auto_create")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "name");
                                DominionCreateCommand.autoCreate(ctx.getSource(), name);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion auto_create_sub <name> <parent>
            root.then(literal("auto_create_sub")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.word())
                            .then(argument("parent", StringArgumentType.greedyString())
                                    .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                                    .executes(ctx -> {
                                        String name = StringArgumentType.getString(ctx, "name");
                                        String parent = StringArgumentType.getString(ctx, "parent");
                                        DominionCreateCommand.autoCreateSub(ctx.getSource(), name, parent);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // === Dominion Operate Commands ===

            // /dominion resize <dominion> <type> <size> [direction]
            root.then(literal("resize")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("type", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        Arrays.stream(DominionReSizeEvent.TYPE.values())
                                                .map(Enum::name).map(String::toLowerCase)
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .then(argument("size", IntegerArgumentType.integer())
                                            .then(argument("direction", StringArgumentType.word())
                                                    .suggests((ctx, builder) -> {
                                                        Arrays.stream(DominionReSizeEvent.DIRECTION.values())
                                                                .map(Enum::name).map(String::toLowerCase)
                                                                .forEach(builder::suggest);
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(ctx -> {
                                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                                        String type = StringArgumentType.getString(ctx, "type");
                                                        String size = String.valueOf(IntegerArgumentType.getInteger(ctx, "size"));
                                                        String dir = StringArgumentType.getString(ctx, "direction");
                                                        DominionOperateCommand.resize(ctx.getSource(), dominion, type, size, dir);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String type = StringArgumentType.getString(ctx, "type");
                                                String size = String.valueOf(IntegerArgumentType.getInteger(ctx, "size"));
                                                DominionOperateCommand.resize(ctx.getSource(), dominion, type, size, "");
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // /dominion expand <size> [direction]
            root.then(literal("expand")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("size", IntegerArgumentType.integer())
                            .then(argument("direction", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        Arrays.stream(DominionReSizeEvent.DIRECTION.values())
                                                .map(Enum::name).map(String::toLowerCase)
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String size = String.valueOf(IntegerArgumentType.getInteger(ctx, "size"));
                                        String dir = StringArgumentType.getString(ctx, "direction");
                                        DominionOperateCommand.easyExpand(ctx.getSource(), size, dir);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .executes(ctx -> {
                                String size = String.valueOf(IntegerArgumentType.getInteger(ctx, "size"));
                                DominionOperateCommand.easyExpand(ctx.getSource(), size, "");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion contract <size> [direction]
            root.then(literal("contract")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("size", IntegerArgumentType.integer())
                            .then(argument("direction", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        Arrays.stream(DominionReSizeEvent.DIRECTION.values())
                                                .map(Enum::name).map(String::toLowerCase)
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String size = String.valueOf(IntegerArgumentType.getInteger(ctx, "size"));
                                        String dir = StringArgumentType.getString(ctx, "direction");
                                        DominionOperateCommand.easyContract(ctx.getSource(), size, dir);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .executes(ctx -> {
                                String size = String.valueOf(IntegerArgumentType.getInteger(ctx, "size"));
                                DominionOperateCommand.easyContract(ctx.getSource(), size, "");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion delete <dominion> [force]
            root.then(literal("delete")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("force", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        builder.suggest("force");
                                        return builder.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String force = StringArgumentType.getString(ctx, "force");
                                        DominionOperateCommand.delete(ctx.getSource(), dominion, force);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .executes(ctx -> {
                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                DominionOperateCommand.delete(ctx.getSource(), dominion, "");
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion set_msg <dominion> <type> <message>
            root.then(literal("set_msg")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("type", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        Arrays.stream(DominionSetMessageEvent.TYPE.values())
                                                .map(Enum::name).map(String::toLowerCase)
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .then(argument("message", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String type = StringArgumentType.getString(ctx, "type");
                                                String message = StringArgumentType.getString(ctx, "message");
                                                DominionOperateCommand.setMessage(ctx.getSource(), dominion, type, message);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // /dominion set_tp <dominion>
            root.then(literal("set_tp")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.greedyString())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .executes(ctx -> {
                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                DominionOperateCommand.setTp(ctx.getSource(), dominion);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion rename <dominion> <newName>
            root.then(literal("rename")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("newName", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String newName = StringArgumentType.getString(ctx, "newName");
                                        DominionOperateCommand.rename(ctx.getSource(), dominion, newName);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion set_map_color <dominion> <color>
            root.then(literal("set_map_color")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("color", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String color = StringArgumentType.getString(ctx, "color");
                                        DominionOperateCommand.setMapColor(ctx.getSource(), dominion, color);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion give <dominion> <player> [force]
            root.then(literal("give")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("player", StringArgumentType.word())
                                    .suggests((ctx, builder) -> suggestPlayers(ctx, builder))
                                    .then(argument("force", StringArgumentType.word())
                                            .suggests((ctx, builder) -> {
                                                builder.suggest("force");
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String player = StringArgumentType.getString(ctx, "player");
                                                String force = StringArgumentType.getString(ctx, "force");
                                                DominionOperateCommand.give(ctx.getSource(), dominion, player, force);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String player = StringArgumentType.getString(ctx, "player");
                                        DominionOperateCommand.give(ctx.getSource(), dominion, player, "");
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion tp <dominion>
            root.then(literal("tp")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.greedyString())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .executes(ctx -> {
                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                DominionOperateCommand.tp(ctx.getSource(), dominion);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion switch_ui [TUI|CUI]
            root.then(literal("switch_ui")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("type", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                builder.suggest("TUI");
                                builder.suggest("CUI");
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                String type = StringArgumentType.getString(ctx, "type");
                                DominionOperateCommand.switchUi(ctx.getSource(), type);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .executes(ctx -> {
                        DominionOperateCommand.switchUi(ctx.getSource(), "");
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // /dominion info
            root.then(literal("info")
                    .requires(src -> hasDefaultPermission(src))
                    .executes(ctx -> {
                        DominionOperateCommand.easyInfo(ctx.getSource());
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // === Dominion Flag Commands ===

            // /dominion set_env <dominion> <flag> <value>
            root.then(literal("set_env")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("flag", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        Flags.getAllEnvFlagsEnable().stream()
                                                .map(f -> f.getFlagName())
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .then(argument("value", StringArgumentType.word())
                                            .suggests((ctx, builder) -> {
                                                builder.suggest("true");
                                                builder.suggest("false");
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String flag = StringArgumentType.getString(ctx, "flag");
                                                String value = StringArgumentType.getString(ctx, "value");
                                                DominionFlagCommand.setEnv(ctx.getSource(), dominion, flag, value);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // /dominion set_guest <dominion> <flag> <value>
            root.then(literal("set_guest")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("flag", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        Flags.getAllPriFlagsEnable().stream()
                                                .filter(f -> !f.equals(Flags.ADMIN))
                                                .map(f -> f.getFlagName())
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .then(argument("value", StringArgumentType.word())
                                            .suggests((ctx, builder) -> {
                                                builder.suggest("true");
                                                builder.suggest("false");
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String flag = StringArgumentType.getString(ctx, "flag");
                                                String value = StringArgumentType.getString(ctx, "value");
                                                DominionFlagCommand.setGuest(ctx.getSource(), dominion, flag, value);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // === Member Commands ===

            // /dominion member_add <dominion> <player>
            root.then(literal("member_add")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("player", StringArgumentType.greedyString())
                                    .suggests((ctx, builder) -> suggestPlayers(ctx, builder))
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String player = StringArgumentType.getString(ctx, "player");
                                        MemberCommand.addMember(ctx.getSource(), dominion, player);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion member_set_pri <dominion> <member> <flag> <value>
            root.then(literal("member_set_pri")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("member", StringArgumentType.word())
                                    .then(argument("flag", StringArgumentType.word())
                                            .suggests((ctx, builder) -> {
                                                Flags.getAllPriFlagsEnable().stream()
                                                        .map(f -> f.getFlagName())
                                                        .forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .then(argument("value", StringArgumentType.word())
                                                    .suggests((ctx, builder) -> {
                                                        builder.suggest("true");
                                                        builder.suggest("false");
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(ctx -> {
                                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                                        String member = StringArgumentType.getString(ctx, "member");
                                                        String flag = StringArgumentType.getString(ctx, "flag");
                                                        String value = StringArgumentType.getString(ctx, "value");
                                                        MemberCommand.setMemberPrivilege(ctx.getSource(), dominion, member, flag, value);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                            )
                    )
            );

            // /dominion member_remove <dominion> <member>
            root.then(literal("member_remove")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("member", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String member = StringArgumentType.getString(ctx, "member");
                                        MemberCommand.removeMember(ctx.getSource(), dominion, member);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // === Group Commands ===

            // /dominion group_create <dominion> <name>
            root.then(literal("group_create")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("name", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String name = StringArgumentType.getString(ctx, "name");
                                        GroupCommand.createGroup(ctx.getSource(), dominion, name);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion group_delete <dominion> <group>
            root.then(literal("group_delete")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("group", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                        String group = StringArgumentType.getString(ctx, "group");
                                        GroupCommand.deleteGroup(ctx.getSource(), dominion, group);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion group_rename <dominion> <old_name> <new_name>
            root.then(literal("group_rename")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("old_name", StringArgumentType.word())
                                    .then(argument("new_name", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String oldName = StringArgumentType.getString(ctx, "old_name");
                                                String newName = StringArgumentType.getString(ctx, "new_name");
                                                GroupCommand.renameGroup(ctx.getSource(), dominion, oldName, newName);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // /dominion group_set_flag <dominion> <group> <flag> <value>
            root.then(literal("group_set_flag")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("group", StringArgumentType.word())
                                    .then(argument("flag", StringArgumentType.word())
                                            .suggests((ctx, builder) -> {
                                                Flags.getAllPriFlagsEnable().stream()
                                                        .map(f -> f.getFlagName())
                                                        .forEach(builder::suggest);
                                                return builder.buildFuture();
                                            })
                                            .then(argument("value", StringArgumentType.word())
                                                    .suggests((ctx, builder) -> {
                                                        builder.suggest("true");
                                                        builder.suggest("false");
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(ctx -> {
                                                        String dominion = StringArgumentType.getString(ctx, "dominion");
                                                        String group = StringArgumentType.getString(ctx, "group");
                                                        String flag = StringArgumentType.getString(ctx, "flag");
                                                        String value = StringArgumentType.getString(ctx, "value");
                                                        GroupCommand.setGroupFlag(ctx.getSource(), dominion, group, flag, value);
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                            )
                    )
            );

            // /dominion group_add_member <dominion> <group> <member>
            root.then(literal("group_add_member")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("group", StringArgumentType.word())
                                    .then(argument("member", StringArgumentType.greedyString())
                                            .suggests((ctx, builder) -> suggestPlayers(ctx, builder))
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String group = StringArgumentType.getString(ctx, "group");
                                                String member = StringArgumentType.getString(ctx, "member");
                                                GroupCommand.addMember(ctx.getSource(), dominion, group, member);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // /dominion group_remove_member <dominion> <group> <member>
            root.then(literal("group_remove_member")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("group", StringArgumentType.word())
                                    .then(argument("member", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String group = StringArgumentType.getString(ctx, "group");
                                                String member = StringArgumentType.getString(ctx, "member");
                                                GroupCommand.removeMember(ctx.getSource(), dominion, group, member);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // === Group Title Commands ===

            // /dominion title_use [id]
            root.then(literal("title_use")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("id", IntegerArgumentType.integer())
                            .executes(ctx -> {
                                int id = IntegerArgumentType.getInteger(ctx, "id");
                                GroupTitleCommand.useTitle(ctx.getSource(), String.valueOf(id));
                                return Command.SINGLE_SUCCESS;
                            })
                    )
                    .executes(ctx -> {
                        GroupTitleCommand.useTitle(ctx.getSource(), "-1");
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // === Template Commands ===

            // /dominion template_create <name>
            root.then(literal("template_create")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "name");
                                TemplateCommand.createTemplate(ctx.getSource(), name);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion template_delete <name>
            root.then(literal("template_delete")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.greedyString())
                            .suggests((ctx, builder) -> suggestTemplates(ctx, builder))
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "name");
                                TemplateCommand.deleteTemplate(ctx.getSource(), name);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion template_set_flag <name> <flag> <value>
            root.then(literal("template_set_flag")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestTemplates(ctx, builder))
                            .then(argument("flag", StringArgumentType.word())
                                    .suggests((ctx, builder) -> {
                                        Flags.getAllPriFlagsEnable().stream()
                                                .map(f -> f.getFlagName())
                                                .forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .then(argument("value", StringArgumentType.word())
                                            .suggests((ctx, builder) -> {
                                                builder.suggest("true");
                                                builder.suggest("false");
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> {
                                                String name = StringArgumentType.getString(ctx, "name");
                                                String flag = StringArgumentType.getString(ctx, "flag");
                                                String value = StringArgumentType.getString(ctx, "value");
                                                TemplateCommand.setTemplateFlag(ctx.getSource(), name, flag, value);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // /dominion member_apply_template <dominion> <member> <template>
            root.then(literal("member_apply_template")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("dominion", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("member", StringArgumentType.word())
                                    .then(argument("template", StringArgumentType.greedyString())
                                            .suggests((ctx, builder) -> suggestTemplates(ctx, builder))
                                            .executes(ctx -> {
                                                String dominion = StringArgumentType.getString(ctx, "dominion");
                                                String member = StringArgumentType.getString(ctx, "member");
                                                String template = StringArgumentType.getString(ctx, "template");
                                                TemplateCommand.memberApplyTemplate(ctx.getSource(), dominion, member, template);
                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
            );

            // /dominion template_rename <name> <new_name>
            root.then(literal("template_rename")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("name", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestTemplates(ctx, builder))
                            .then(argument("new_name", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String name = StringArgumentType.getString(ctx, "name");
                                        String newName = StringArgumentType.getString(ctx, "new_name");
                                        TemplateCommand.renameTemplate(ctx.getSource(), name, newName);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // === Copy Commands ===

            // /dominion copy_env <from> <to>
            root.then(literal("copy_env")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("from", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("to", StringArgumentType.greedyString())
                                    .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                                    .executes(ctx -> {
                                        String from = StringArgumentType.getString(ctx, "from");
                                        String to = StringArgumentType.getString(ctx, "to");
                                        CopyCommand.copyEnvironment(ctx.getSource(), from, to);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion copy_guest <from> <to>
            root.then(literal("copy_guest")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("from", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("to", StringArgumentType.greedyString())
                                    .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                                    .executes(ctx -> {
                                        String from = StringArgumentType.getString(ctx, "from");
                                        String to = StringArgumentType.getString(ctx, "to");
                                        CopyCommand.copyGuest(ctx.getSource(), from, to);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion copy_member <from> <to>
            root.then(literal("copy_member")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("from", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("to", StringArgumentType.greedyString())
                                    .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                                    .executes(ctx -> {
                                        String from = StringArgumentType.getString(ctx, "from");
                                        String to = StringArgumentType.getString(ctx, "to");
                                        CopyCommand.copyMember(ctx.getSource(), from, to);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // /dominion copy_group <from> <to>
            root.then(literal("copy_group")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("from", StringArgumentType.word())
                            .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                            .then(argument("to", StringArgumentType.greedyString())
                                    .suggests((ctx, builder) -> suggestDominions(ctx, builder))
                                    .executes(ctx -> {
                                        String from = StringArgumentType.getString(ctx, "from");
                                        String to = StringArgumentType.getString(ctx, "to");
                                        CopyCommand.copyGroup(ctx.getSource(), from, to);
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            );

            // === Migration Commands ===

            // /dominion migrate <residence_name>
            root.then(literal("migrate")
                    .requires(src -> hasDefaultPermission(src))
                    .then(argument("residence_name", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                String name = StringArgumentType.getString(ctx, "residence_name");
                                MigrationCommand.migrate(ctx.getSource(), name);
                                return Command.SINGLE_SUCCESS;
                            })
                    )
            );

            // /dominion migrate_all
            root.then(literal("migrate_all")
                    .requires(src -> hasAdminPermission(src))
                    .executes(ctx -> {
                        MigrationCommand.migrateAll(ctx.getSource());
                        return Command.SINGLE_SUCCESS;
                    })
            );

            // Register the root command
            dispatcher.register(root);
            XLogger.info("Dominion commands registered.");
        });
    }

    // --- Permission helpers ---

    private static boolean hasAdminPermission(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayer();
            return player != null && (PermissionHelper.hasPermissionLevel(player, 4) ||
                    (adminPermission != null && PermissionHelper.hasPermissionLevel(player, 4)));
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasDefaultPermission(CommandSourceStack source) {
        try {
            ServerPlayer player = source.getPlayer();
            return player != null && (PermissionHelper.hasPermissionLevel(player, 4) ||
                    (defaultPermission != null && PermissionHelper.hasPermissionLevel(player, 0)));
        } catch (Exception e) {
            return false;
        }
    }

    // --- Suggestion helpers ---

    private static CompletableFuture<Suggestions> suggestDominions(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        try {
            ServerPlayer player = ctx.getSource().getPlayer();
            if (player != null) {
                CacheManager.instance.getPlayerManageDominionNames(player.getUUID())
                        .forEach(builder::suggest);
            } else {
                CacheManager.instance.getAllDominionNames().forEach(builder::suggest);
            }
        } catch (Exception ignored) {
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestPlayers(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        try {
            CacheManager.instance.getPlayerNames().forEach(builder::suggest);
        } catch (Exception ignored) {
        }
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestTemplates(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        try {
            ServerPlayer player = ctx.getSource().getPlayer();
            if (player != null) {
                cn.lunadeer.dominion.doos.TemplateDOO.selectAll(player.getUUID())
                        .stream().map(cn.lunadeer.dominion.doos.TemplateDOO::getName)
                        .forEach(builder::suggest);
            }
        } catch (Exception ignored) {
        }
        return builder.buildFuture();
    }
}
