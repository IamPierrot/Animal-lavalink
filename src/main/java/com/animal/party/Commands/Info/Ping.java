package com.animal.party.Commands.Info;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Date;
import java.util.List;

public class Ping extends PrefixCommand {

    static {
        PrefixCommand.registerCommand(new Ping());
    }

    private Ping() {
        super("ping", "pong!");
    }

    @Override
    protected void initialize() {

    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        final var jda = event.getJDA();
        final var ping = jda.getRestPing();
        var guild = event.getGuild();
        User selfUser = jda.getSelfUser(); // Gets the bot's user
        String botUsername = selfUser.getName();
        String botAvatarUrl = selfUser.getAvatarUrl();
        String guildName = guild.getName();
        String guildIconUrl = guild.getIconUrl();


        // Create the embed
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(Color.MAGENTA) // Replace 'Blurple' with a suitable Color
                .setAuthor(botUsername, null, botAvatarUrl)
                .setDescription(String.format("```elm\nAPI Latency (Websocket) : %dms\nMessage Latency         : %dms```",
                        Math.round(ping.complete()),
                        Math.abs(new Date().getTime() - event.getMessage().getTimeCreated().toInstant().toEpochMilli())))
                .setFooter(guildName, guildIconUrl);
        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
    }
}
