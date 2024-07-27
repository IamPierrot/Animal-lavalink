package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MusicQueue extends PrefixCommand {

    static  {
        PrefixCommand.registerCommand(new MusicQueue());
    }

    private MusicQueue() {
        super("queue", "xem hàng chờ");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        var guild = event.getGuild();
        var guildMusicManger = getOrCreateMusicManager(guild.getIdLong());

        var queue = guildMusicManger.scheduler.queue;

//        final String[] methods = {"", "🔁", "🔂"};

        final int songCount = queue.size();
        String nextSongs = songCount > 5 ? "Và **%d** bài khác nữa...".formatted(songCount - 5) : "Đang trong hàng chờ được phát là **%d** bài hát...".formatted(songCount);

        var tracks = formatTracks(queue);
        AtomicReference<Track> currentTrack = new AtomicReference<>();

        guildMusicManger.getPlayer().ifPresentOrElse(
                (player) -> {
                    if (player.getTrack() != null) {
                        currentTrack.set(player.getTrack());
                    }
                },
                () -> {}
        );
        var embed = new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setAuthor("Danh sách hàng chờ - %s".formatted(guild.getName()), null, event.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(guild.getIconUrl())
                .setDescription("Đang phát **[%s](%s)**\n\n%s\n\n%s".formatted(
                        currentTrack.get().getInfo().getTitle(),
                        currentTrack.get().getInfo().getUri(),
                        String.join("\n", tracks.subList(0, songCount)),
                        nextSongs
                ))
                .setFooter("💖 Âm nhạc đi trước tình yêu theo sau", event.getJDA().getSelfUser().getAvatarUrl());

        event.getMessage().replyEmbeds(embed.build()).queue();
    }

    private List<String> formatTracks(Queue<Track> queue) {
        int[] index = {1}; // to keep track of the index
        return queue.stream()
                .map(track -> String.format("**%d** - %s", index[0]++, formatTrack(track)))
                .collect(Collectors.toList());
    }

    private String formatTrack(Track track) {
        return String.format("`%s | %s`", track.getInfo().getTitle(), track.getInfo().getAuthor());
    }

}
