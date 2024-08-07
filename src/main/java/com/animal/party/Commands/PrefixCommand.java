package com.animal.party.Commands;

import com.animal.party.App;
import com.animal.party.Commands.Info.Help;
import com.animal.party.Commands.Info.Ping;
import com.animal.party.Commands.Music.*;
import com.animal.party.Main;
import com.animal.party.Utils;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class PrefixCommand extends Utils {
    public static final Map<String, PrefixCommand> prefixCommandMap = new HashMap<>();
    private static final Logger logger = App.getLogger(PrefixCommand.class);
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

    public static void handlePrefixCommand(LavalinkClient client, @NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String prefix = Main.config.getApp().prefix.toLowerCase();
        final var content = event.getMessage().getContentRaw();

        final var mention = event.getMessage().getMentions().getMentions().getFirst();

        boolean isSelfMention = false;
        if (mention != null && mention.getId().equals(event.getJDA().getSelfUser().getId())) {
            prefix = mention.getAsMention();
            isSelfMention = true;
        }

        if (!content.toLowerCase().startsWith(prefix)) return;
        else if (isSelfMention) {
            event.getMessage()
                    .replyEmbeds(new EmbedBuilder()
                            .setDescription("Ckao`! Đây là bot âm nhạc và prefix của tôi là %s hoặc bạn có thể ping %s để dùng lệnh\n(help để biết thêm thông tin)".formatted(Main.config.getApp().prefix, event.getJDA().getSelfUser().getAsMention()))
                            .setColor(Color.pink)
                            .setFooter("Âm nhạc đi trước tình yêu theo sau 💞", event.getJDA().getSelfUser().getAvatarUrl())
                            .build()
                    ).queue();
            return;
        }

        String[] args = content.substring(prefix.length()).trim().split("\\s+");

        String command = args[0].toLowerCase();
        List<String> commandArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        final var commandObject = getCommand(command);

        if (Objects.isNull(commandObject)) return;

        var member = event.getMember();

        if (commandObject.voiceChannel) {
            if (member == null) return;
            if (isNotSameVoice(member.getVoiceState(), Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState(), event.getMessage()))
                return;
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

    protected abstract void initialize();

    public abstract void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args);

}
