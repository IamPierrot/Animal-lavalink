package com.animal.party.Handlers;

import com.animal.party.Canvas.MusicCard;
import com.animal.party.Utils;
import dev.arbjerg.lavalink.client.event.TrackEndEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TrackScheduler extends Utils {
    public final Queue<Track> queue = new LinkedList<>();
    public final Deque<Track> history = new ArrayDeque<>();
    private final Logger logger = getLogger(TrackScheduler.class);
    private final GuildMusicManager guildMusicManager;
    private LoopMode loopMode = LoopMode.NONE;
    private Track currentTrack;
    private boolean goingBack = false;

    public TrackScheduler(GuildMusicManager guildMusicManager) {
        this.guildMusicManager = guildMusicManager;
    }

    ////////////////// EVENTS

    public void onTrackStart(TrackStartEvent event) {
        var track = event.getTrack();
        currentTrack = track;

        logger.info("Track started: {}", track.getInfo());

        var row = ActionRow.of(
                Button.primary("back", Emoji.fromCustom("firsttrack", 1267689860483907624L, false)),
                Button.primary("loop", Emoji.fromCustom("loop", 1267690139770159197L, false)),
                Button.primary("stop", Emoji.fromCustom("stop", 1267689997050318888L, false)),
                Button.primary("pause", Emoji.fromCustom("pause", 1267689876791230567L, false)),
                Button.primary("skip", Emoji.fromCustom("next", 1267689838480588800L, false))
        ).getComponents();

        Message msg = null;
        try {
            msg = guildMusicManager.metadata.sendFiles(MusicCard.getMusicCard(track.getInfo())).addActionRow(row).complete();
        } catch (TranscoderException | IOException e) {
            logger.error(e.getMessage());
        }
        if (msg != null) {
            Message finalMsg = msg;
            setTimeout(() -> finalMsg.delete().queue(), track.getInfo().getLength());
        }
    }

    public void onTrackEnd(TrackEndEvent event) {
        var endReason = event.getEndReason();
        if (!goingBack && currentTrack != null) {
            history.push(currentTrack);
        } else  {
            history.push(event.getTrack().makeClone());
        }

        if (endReason.getMayStartNext()) {
            if (loopMode == LoopMode.TRACK) {
                startTrack(event.getTrack().makeClone());
            } else {
                nextTrack();
            }
        }
    }
    ////////////////////////////////////////


    ///////////////////////// Queue navigation
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
    //////////////////////////////////////////////


    //////////////////////////////// UTILS
    public synchronized void skipTrack() {
        goingBack = false;
        nextTrack();
    }

    public synchronized void backTrack() {
        if (!history.isEmpty()) {
            goingBack = true;
            Track previousTrack = history.pop();
            queue.offer(currentTrack);
            startTrack(previousTrack);
        } else {
            guildMusicManager.metadata.sendMessageEmbeds(
                    new EmbedBuilder()
                            .setAuthor("Không còn bài hát nào trong lịch sử!")
                            .build()
            ).queue();
        }
    }

    private synchronized void startTrack(Track track) {
        this.guildMusicManager.getLink().ifPresent(
                (link) -> link.createOrUpdatePlayer()
                        .setTrack(track)
                        .setVolume(35)
                        .subscribe()
        );
    }
    ///////////////////////////////////////////


    /////////////////////////// Track handle
    private void nextTrack() {
        final var nextTrack = queue.poll();

        if (nextTrack != null) {
            startTrack(nextTrack);
        } else if (loopMode == LoopMode.TRACK && currentTrack != null) {
            startTrack(currentTrack.makeClone());
        } else if (loopMode == LoopMode.QUEUE && !queue.isEmpty()) {
            queue.addAll(history);
            startTrack(queue.poll());
        } else {
            startTrack(null);
            guildMusicManager.metadata.sendMessageEmbeds(
                    new EmbedBuilder()
                            .setAuthor("Không còn bài hát nào trong danh sách!")
                            .build()
            ).queue();
        }
    }
    ////////////////////////////////////////////

    ////////////////////////////////////// LOOP MODE
    public synchronized int getLoopMode() {
        return loopMode.ordinal();
    }

    public synchronized void setLoopMode(LoopMode loopMode) {
        this.loopMode = loopMode;
    }
    ///////////////////////////////////////


    /////////////////////////////////////// HELPERS
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

    ////////////////////////////////////////////////////

}
