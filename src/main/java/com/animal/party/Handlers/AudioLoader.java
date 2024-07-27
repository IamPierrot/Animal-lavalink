package com.animal.party.Handlers;

import com.animal.party.UserData;
import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.player.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class AudioLoader extends AbstractAudioLoadResultHandler {
    private final MessageReceivedEvent event;
    private final GuildMusicManager guildMusicManager;

    public AudioLoader(MessageReceivedEvent event, GuildMusicManager guildMusicManager) {
        this.event = event;
        this.guildMusicManager = guildMusicManager;
    }

    @Override
    public void ontrackLoaded(@NotNull TrackLoaded result) {
        final Track track = result.getTrack();

        var userData = new UserData(event.getAuthor().getIdLong());

        track.setUserData(userData);

        this.guildMusicManager.scheduler.enqueue(track);

        final var trackTitle = track.getInfo().getTitle();

        event.getGuildChannel().sendMessage("Added to queue: " + trackTitle + "\nRequested by: <@" + userData.requester() + '>').queue();
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        final int trackCount = result.getTracks().size();
        event.getGuildChannel()
                .sendMessage("Added " + trackCount + " tracks to the queue from " + result.getInfo().getName() + "!")
                .queue();

        this.guildMusicManager.scheduler.enqueuePlaylist(result.getTracks());
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult result) {
        final List<Track> tracks = result.getTracks();

        if (tracks.isEmpty()) {
            event.getGuildChannel().sendMessage("No tracks found!").queue();
            return;
        }

        final Track firstTrack = tracks.getFirst();

        event.getGuildChannel().sendMessageEmbeds(trackEmbed(firstTrack)).queue();

        this.guildMusicManager.scheduler.enqueue(firstTrack);
    }

    @Override
    public void noMatches() {
        event.getGuildChannel().sendMessage("No matches found for your input!").queue();
    }

    @Override
    public void loadFailed(@NotNull LoadFailed loadFailed) {
        event.getGuildChannel().sendMessage("Failed to load track! " + loadFailed.getException().getMessage()).queue();
    }

    private MessageEmbed trackEmbed(Track track) {
        var trackInfo = track.getInfo();

        return new EmbedBuilder()
                .setAuthor("THÊM VÀO HÀNG CHỜ", null, trackInfo.getArtworkUrl())
                .setDescription(":notes: **[%s](%s)**\n\nNguồn: **%s**\nThời lượng: `%o`"
                        .formatted(
                        trackInfo.getTitle(),
                        trackInfo.getUri(),
                        trackInfo.getSourceName(),
                        trackInfo.getLength() / 1000 / 60))
                .setFooter("💖 Âm nhạc đi trước tình yêu theo sau", event.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(trackInfo.getArtworkUrl())
                .setColor(Color.pink).build();
    }
}
