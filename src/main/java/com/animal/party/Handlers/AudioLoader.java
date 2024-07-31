package com.animal.party.Handlers;

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

        event.getMessage().replyEmbeds(trackEmbed(track)).queue();

        this.guildMusicManager.scheduler.enqueue(track);
    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded result) {
        this.guildMusicManager.scheduler.enqueuePlaylist(result.getTracks());

        event.getMessage().replyEmbeds(playlistEmbed(result.getTracks())).queue();
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult result) {
        final List<Track> tracks = result.getTracks();

        if (tracks.isEmpty()) {
            event.getGuildChannel().sendMessage("No tracks found!").queue();
            return;
        }

        final Track firstTrack = tracks.getFirst();

        event.getMessage().replyEmbeds(trackEmbed(firstTrack)).queue();

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
                .setDescription("Đã thêm [%s](%s) vào hàng chờ!"
                        .formatted(
                        trackInfo.getTitle(),
                        trackInfo.getUri()))
                .setFooter("💖 Âm nhạc đi trước tình yêu theo sau", event.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(trackInfo.getArtworkUrl())
                .setColor(Color.pink).build();
    }

    private MessageEmbed playlistEmbed(List<Track> playlist) {
        var trackInfo = playlist.getFirst().getInfo();
        return new EmbedBuilder()
                .setAuthor("THÊM PLAYLIST", null, trackInfo.getArtworkUrl())
                .setDescription("Đã thêm **%d** bài hát vào hàng chờ!"
                        .formatted(playlist.size()))
                .setFooter("💖 Âm nhạc đi trước tình yêu theo sau", event.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(trackInfo.getArtworkUrl())
                .setColor(Color.pink).build();
    }
}
