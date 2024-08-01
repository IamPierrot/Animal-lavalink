package com.animal.party.Commands;

import com.animal.party.App;
import com.animal.party.Commands.Info.Help;
import com.animal.party.Commands.Info.Ping;
import com.animal.party.Commands.Music.*;

import com.animal.party.Main;
import com.animal.party.Utils;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public abstract class PrefixCommand extends Utils {
    private static final Logger logger = App.getLogger(PrefixCommand.class);
    public static final Map<String, PrefixCommand> prefixCommandMap = new HashMap<>();

    public final String name;
    public final String description;
    public final String category;

    public boolean showHelp = true;
    public String[] aliases = {};
    public boolean voiceChannel = false;
    public String usage = "";

    protected PrefixCommand(@NotNull String name, @NotNull String description, @NotNull String category) {
        this.name = name.toLowerCase();
        this.description = description.toLowerCase();
        this.category = capitalizeFirstLetter(category);
    }

    protected static void registerCommand(PrefixCommand command) {
        prefixCommandMap.put(command.name, command);
        logger.info("Loaded {}!", command.name);
        command.initialize();
    }

    public static void loadCommands() {
        // Reference all command classes to ensure their static blocks are executed
        Class<?>[] commands = {
                Help.class,
                Play.class,
                Skip.class,
                Stop.class,
                Pause.class,
                MusicQueue.class,
                Loop.class,
                History.class,
                Ping.class
                // Add other command classes here, e.g., Pause.class, Stop.class, etc.
        };
        for (Class<?> command : commands) {
            try {
                Class.forName(command.getName());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
            }
        }
    }

    protected abstract void initialize();

    public abstract void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args);

    public static void handlePrefixCommand(LavalinkClient client, @NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        final String PREFIX = Main.config.getApp().prefix.toLowerCase();
        if (!event.getMessage().getContentRaw().toLowerCase().startsWith(PREFIX)) return;

        var args = event.getMessage().getContentRaw().substring(PREFIX.length()).trim().split(" ");

        String command = args[0].toLowerCase();
        List<String> commandArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        final var commandObject = getCommand(command);

        if (Objects.isNull(commandObject)) return;

        var member = event.getMember();

        if (commandObject.voiceChannel) {
            if (member == null) return;
            if (isNotSameVoice(member.getVoiceState(), event.getMember().getVoiceState(), event.getMessage())) return;
        }

        commandObject.callback(client, event, commandArgs);
    }


    protected static @Nullable PrefixCommand getCommand(String commandName) {
        return Optional.ofNullable(prefixCommandMap.get(commandName))
                .orElseGet(() -> prefixCommandMap.values().stream()
                        .filter(value -> Arrays.asList(value.aliases).contains(commandName))
                        .findFirst()
                        .orElse(null));
    }

}
