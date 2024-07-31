package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.stream.Collectors;

public class History extends PrefixCommand {

    static  {
        registerCommand(new History());
    }

    History() {
        super("history", "xem lại lịch sử đã phát");
    }

    @Override
    protected void initialize() {
        aliases = new String[]{""};
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        var guild = event.getGuild();
        var guildMusicManger = getOrCreateMusicManager(guild.getIdLong(), event.getChannel());

        if (!guildMusicManger.isPlaying()) return;

        var history = guildMusicManger.scheduler.history;
        if (history.isEmpty()) return;

        int songCount = history.size();
        var tracks = formatTracks(history);

        var embed = new EmbedBuilder()
                .setAuthor("LỊCH SỬ PHÁT - %s".formatted(guild.getName()), null, guild.getIconUrl())
                .setDescription(String.join("\n", tracks.subList(0, Math.min(songCount, 10))))
                .build();

        event.getMessage().replyEmbeds(embed).queue();
    }

    private List<String> formatTracks(Deque<Track> history) {
        int[] index = {1};

        return history.stream()
                .map(track -> String.format("**%d** - %s", index[0]++, formatTrack(track)))
                .collect(Collectors.toList());
    }

    private String formatTrack(Track track) {
        return String.format("[%s](%s)", track.getInfo().getTitle(), track.getInfo().getUri());
    }
}
