package com.animal.party.Listener;

import com.animal.party.Utils;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.*;
import org.slf4j.Logger;

import java.util.Optional;

public class LavaLinkListener extends Utils {
    private static final Logger LOG = getLogger("LavaLink");

    public static void lavaLinkRegisterEvents(LavalinkClient client) {
        registerLavalinkNodes(client);
        registerLavalinkListeners(client);
    }

    private static void registerLavalinkNodes(LavalinkClient client) {
        client.addNode(
                new NodeOptions.Builder()
                        .setName("localhost")
                        .setServerUri("http://localhost:2333")
                        .setPassword("youshallnotpass")
                        .build()
        );
    }

    private static void registerLavalinkListeners(LavalinkClient client) {
        client.on(ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            LOG.info(
                    "Node '{}' is ready, session id is '{}'!",
                    node.getName(),
                    event.getSessionId()
            );
        });

        client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            LOG.info(
                    "Node '{}' has stats, current players: {}/{} (link count {})",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers(),
                    client.getLinks().size()
            );
        });

        client.on(TrackStartEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            LOG.info(
                    "{}: track started: {}",
                    node.getName(),
                    event.getTrack().getInfo()
            );
            Optional.ofNullable(JDAListener.musicManagers.get(event.getGuildId())).ifPresent(
                    (guildMusicManager) -> guildMusicManager.scheduler.onTrackStart(event)
            );
        });

        client.on(TrackEndEvent.class).subscribe((event) -> {
            Optional.ofNullable(JDAListener.musicManagers.get(event.getGuildId())).ifPresent(
                    (guildMusicManager) -> guildMusicManager.scheduler.onTrackEnd(event)
            );
        });

        client.on(EmittedEvent.class).subscribe((event) -> {
            final var node = event.getNode();

            LOG.info(
                    "Node '{}' emitted event: {}",
                    node.getName(),
                    event
            );
        });
    }
}
