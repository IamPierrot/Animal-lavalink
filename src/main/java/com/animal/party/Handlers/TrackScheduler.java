package com.animal.party.Handlers;

import com.animal.party.App;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler {
    public final Queue<Track> queue = new LinkedList<>();
    private final Logger logger = App.getLogger(TrackScheduler.class);
    private final GuildMusicManager guildMusicManager;
    private LoopMode loopMode = LoopMode.NONE;
    private Track lastTrack;

    public TrackScheduler(GuildMusicManager guildMusicManager) {
        this.guildMusicManager = guildMusicManager;
    }

    public void enqueue(Track track) {
        this.guildMusicManager.getPlayer().ifPresentOrElse(
                (player) -> {
                    if (player.getTrack() == null) {
                        this.startTrack(track);
                    } else {
                        this.queue.offer(track);
                    }
                },
                () -> {
                    this.startTrack(track);
                }
        );
    }

    public void enqueuePlaylist(List<Track> tracks) {
        this.queue.addAll(tracks);

        this.guildMusicManager.getPlayer().ifPresentOrElse(
                (player) -> {
                    if (player.getTrack() == null) {
                        this.startTrack(this.queue.poll());
                    }
                },
                () -> {
                    this.startTrack(this.queue.poll());
                }
        );
    }

    public void skipTrack() {
        nextTrack();
    }

    ////////////////// EVENTS

    public void onTrackStart(TrackStartEvent event) {
        var track = event.getTrack();
        lastTrack = track;
        logger.info("Track started: {}", track.getInfo());
        guildMusicManager.metadata.sendMessageEmbeds(trackEmbed(track)).queue();
    }

    public void onTrackEnd(TrackEndEvent event) {
        var endReason = event.getEndReason();

        lastTrack = event.getTrack();
        if (endReason.getMayStartNext()) {
            if (loopMode == LoopMode.TRACK) {
                startTrack(event.getTrack().makeClone());
            } else {
                nextTrack();
            }
        }
    }
    ////////////////////////////////////////

    public synchronized int getLoopMode() {
        return loopMode.ordinal();
    }

    public synchronized void setLoopMode(LoopMode loopMode) {
        this.loopMode = loopMode;
    }

    private void startTrack(Track track) {
        this.guildMusicManager.getLink().ifPresent(
                (link) -> link.createOrUpdatePlayer()
                        .setTrack(track)
                        .setVolume(35)
                        .subscribe()
        );
    }

    private void nextTrack() {
        final var nextTrack = queue.poll();

        if (nextTrack != null) {
            startTrack(nextTrack);
        } else if (loopMode == LoopMode.TRACK && lastTrack != null) {
            startTrack(lastTrack.makeClone());
        } else if (loopMode == LoopMode.QUEUE && !queue.isEmpty()) {
            var firstTrack = queue.poll();
            queue.offer(firstTrack);
            startTrack(firstTrack.makeClone());
        } else {
            startTrack(null);
            guildMusicManager.metadata.sendMessageEmbeds(
                    new EmbedBuilder()
                            .setAuthor("Không còn bài hát nào trong danh sách!")
                            .build()
            ).queue();
        }
    }

    private MessageEmbed trackEmbed(Track track) {
        var trackInfo = track.getInfo();
        long lengthInMillis = trackInfo.getLength();
        long minutes = (lengthInMillis / 1000) / 60;
        long seconds = (lengthInMillis / 1000) % 60;

        return new EmbedBuilder()
                .setAuthor("MENU ĐIỀU KHIỂN", null, trackInfo.getArtworkUrl())
                .setDescription("""
                        :notes: **[%s](%s)**
                                               \s
                        :musical_keyboard: **Tác giả :** `%s`
                        :hourglass: **Thời lượng :** `%d:%02d`"""
                        .formatted(
                                trackInfo.getTitle(),
                                trackInfo.getUri(),
                                trackInfo.getAuthor(),
                                minutes,
                                seconds))
                .setFooter("💖 Âm nhạc đi trước tình yêu theo sau", guildMusicManager.metadata.getJDA().getSelfUser().getAvatarUrl())
                .setThumbnail(trackInfo.getArtworkUrl())
                .setColor(Color.pink).build();
    }

}