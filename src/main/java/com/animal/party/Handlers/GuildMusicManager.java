package com.animal.party.Handlers;

import com.animal.party.App;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GuildMusicManager {
    public final TrackScheduler scheduler = new TrackScheduler(this);
    private final long guildId;
    private final LavalinkClient lavalink = App.client;
    public final MessageChannelUnion metadata;

    public GuildMusicManager(long guildId, MessageChannelUnion metadata) {
        this.guildId = guildId;
        this.metadata = metadata;
    }
    ////// Function Utils
    public void stop() {
        this.scheduler.queue.clear();

        this.getPlayer().ifPresent(
                (player) -> player.setPaused(false)
                        .setTrack(null)
                        .subscribe()
        );
    }

    public void skip() {
        this.scheduler.skipTrack();
    }

    public Track getCurrentTrack() {
        AtomicReference<Track> result = new AtomicReference<>();
        getPlayer().ifPresent(lavalinkPlayer -> result.set(lavalinkPlayer.getTrack()));
        return result.get();
    }

    public synchronized boolean isPlaying() {
        return getCurrentTrack() != null;
    }
    //////////////////////////////////////////
    public Optional<Link> getLink() {
        return Optional.ofNullable(
                this.lavalink.getLinkIfCached(this.guildId)
        );
    }

    public Optional<LavalinkPlayer> getPlayer() {
        return this.getLink().map(Link::getCachedPlayer);
    }
}