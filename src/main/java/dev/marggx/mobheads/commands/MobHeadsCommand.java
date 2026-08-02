package dev.marggx.mobheads.commands;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.permissions.provider.HytalePermissionsProvider;
import com.hypixel.hytale.server.core.util.Config;
import dev.marggx.mobheads.MobHeadConfig;
import dev.marggx.mobheads.MobHeadsPlugin;
import dev.marggx.mobheads.ValidHeads;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MobHeadsCommand extends CommandBase {

    public MobHeadsCommand() {
        super("mobheads", "MobHeads plugin commands");
        setPermissionGroups(HytalePermissionsProvider.GROUP_WORLD_EDITOR);
        addSubCommand(new ConfigCommand());
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        context.sendMessage(Message.raw("MobHeads (" + ValidHeads.HEADS.size() + "):"));
        Message message = Message.empty();
        for (String head : ValidHeads.HEADS) {
            message.insert(head + ";");
        }
        context.sendMessage(message);
    }

    public static class ConfigCommand extends CommandBase {

        public ConfigCommand() {
            super("config", "MobHeads configuration commands");
            addSubCommand(new ChanceCommand());
            addSubCommand(new ExcludeCommand());
            addSubCommand(new IncludeCommand());
            addSubCommand(new ReloadCommand());
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            MobHeadConfig config = MobHeadsPlugin.get().getConfig().get();

            context.sendMessage(Message.raw("MobHeads Configuration:"));
            context.sendMessage(Message.raw("  Drop Chance: " + config.getDropChance() + "%"));

            String[] excluded = config.getExcludedHeads();
            if (excluded.length == 0) {
                context.sendMessage(Message.raw("  Excluded Heads: none"));
            } else {
                context.sendMessage(Message.raw("  Excluded Heads: " + Arrays.stream(excluded).collect(Collectors.joining(", "))));
            }
        }

        public static class ChanceCommand extends CommandBase {
            private final DefaultArg<Float> chanceArg = withDefaultArg(
                "chance", "Drop chance percentage (0-100)", ArgTypes.FLOAT,
                2.5f, "Default: 2.5"
            ).addValidator(Validators.insideRange(0f, 100f));

            public ChanceCommand() {
                super("chance", "Set or view the head drop chance");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                MobHeadsPlugin plugin = MobHeadsPlugin.get();
                Config<MobHeadConfig> config = plugin.getConfig();

                float newChance = chanceArg.get(context);
                config.get().setDropChance(newChance);
                config.save();

                context.sendMessage(Message.raw("MobHeads drop chance set to " + newChance + "%"));
            }
        }

        public static class ExcludeCommand extends CommandBase {
            private final RequiredArg<String> headArg = withRequiredArg("head", "Head item ID to exclude (e.g. Head_Spider)", ArgTypes.STRING);

            public ExcludeCommand() {
                super("exclude", "Exclude a mob head from dropping");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                MobHeadsPlugin plugin = MobHeadsPlugin.get();
                Config<MobHeadConfig> config = plugin.getConfig();

                String headId = headArg.get(context);
                String[] excluded = config.get().getExcludedHeads();

                if (Arrays.asList(excluded).contains(headId)) {
                    context.sendMessage(Message.raw(headId + " is already excluded"));
                    return;
                }

                String[] newExcluded = Arrays.copyOf(excluded, excluded.length + 1);
                newExcluded[excluded.length] = headId;
                config.get().setExcludedHeads(newExcluded);
                config.save();

                context.sendMessage(Message.raw("Excluded " + headId + " from dropping"));
            }
        }

        public static class IncludeCommand extends CommandBase {
            private final RequiredArg<String> headArg = withRequiredArg("head", "Head item ID to include (e.g. Head_Spider)", ArgTypes.STRING);

            public IncludeCommand() {
                super("include", "Re-include a previously excluded mob head");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                MobHeadsPlugin plugin = MobHeadsPlugin.get();
                Config<MobHeadConfig> config = plugin.getConfig();

                String headId = headArg.get(context);
                String[] excluded = config.get().getExcludedHeads();

                if (!Arrays.asList(excluded).contains(headId)) {
                    context.sendMessage(Message.raw(headId + " is not excluded"));
                    return;
                }

                String[] newExcluded = Arrays.stream(excluded)
                    .filter(s -> !s.equals(headId))
                    .toArray(String[]::new);
                config.get().setExcludedHeads(newExcluded);
                config.save();

                context.sendMessage(Message.raw("Included " + headId + " back into drops"));
            }
        }

        public static class ReloadCommand extends CommandBase {

            public ReloadCommand() {
                super("reload", "Reload config from disk");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                MobHeadsPlugin plugin = MobHeadsPlugin.get();
                plugin.getConfig().load().join();

                context.sendMessage(Message.raw("MobHeads config reloaded"));
            }
        }
    }
}
