package com.animal.party.Commands;

import com.animal.party.App;
import com.animal.party.Commands.Info.Ping;
import com.animal.party.Commands.Music.*;

import com.animal.party.Handlers.GuildMusicManager;
import com.animal.party.Listener.JDAListener;
import com.animal.party.Main;
import com.animal.party.Utils;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.*;

public abstract class PrefixCommand extends Utils {
    private static final Logger logger = App.getLogger(PrefixCommand.class);
    public static Map<String, PrefixCommand> prefixCommandMap = new HashMap<>();

    public final String name;
    public final String description;
    protected String[] aliases = {};
    protected boolean voiceChannel = false;

    protected PrefixCommand(@NotNull String name, @NotNull String description) {
        this.name = name.toLowerCase();
        this.description = description.toLowerCase();
    }

    protected static void registerCommand(PrefixCommand command) {
        prefixCommandMap.put(command.name, command);
        logger.info("Loaded {}!", command.name);
        command.initialize();
    }

    public static void loadCommands() {
        // Reference all command classes to ensure their static blocks are executed
        Class<?>[] commands = {
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

    protected void joinHelper(@NotNull MessageReceivedEvent event) {
        final Member member = event.getMember();
        assert member != null;
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(Objects.requireNonNull(memberVoiceState.getChannel()));
        }

        this.getOrCreateMusicManager(member.getGuild().getIdLong(), event.getChannel());
    }


    public static void handlePrefixCommand(LavalinkClient client, @NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        final String PREFIX = Main.config.getApp().prefix;
        if (!event.getMessage().getContentRaw().startsWith(PREFIX)) return;

        var args = event.getMessage().getContentRaw().substring(PREFIX.length()).trim().split(" ");

        String command = args[0].toLowerCase();
        List<String> commandArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        var commandObject = prefixCommandMap.get(command);
        if (Objects.isNull(commandObject)) {
            for (var value : prefixCommandMap.values()) {
                if (Arrays.stream(value.aliases).toList().isEmpty()) break;
                else if (Arrays.stream(value.aliases).toList().contains(command)) {
                    commandObject = value;
                    break;
                }
            }
            if (Objects.isNull(commandObject)) return;
        }

        var member = event.getMember();

        if (commandObject.voiceChannel) {
            if (member == null) return;

            var memberVoiceState = member.getVoiceState();
            var selfVoiceState = event.getGuild().getSelfMember().getVoiceState();

            assert memberVoiceState != null;
            if (memberVoiceState.getChannel() == null) return;
            assert selfVoiceState != null;
            if (selfVoiceState.getChannel() != null && memberVoiceState.getChannel().getIdLong() != selfVoiceState.getChannel().getIdLong()) return;
        }

        commandObject.callback(client, event, commandArgs);
    }

}
