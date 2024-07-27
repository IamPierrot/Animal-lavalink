package com.animal.party;

import com.animal.party.Listener.JDAListener;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.event.*;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.animal.party.Listener.LavaLinkListener.*;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final int SESSION_INVALID = 4006;
    public static LavalinkClient client;
//    private static JDAListener listener;

    public static void main(String[] args) throws InterruptedException {
        final var token = "MTI2Mjc0MTk4OTY1NzQxNTgwMA.GY2Klv.DwXLj8xb9g992l3gzJa4IjxsMA8St_3eEzXcuU";
        client = new LavalinkClient(Helpers.getUserIdFromToken(token));

        client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        lavaLinkRegister(client);

        final var jda = JDABuilder.createDefault(token)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(new JDAListener(client))
                .build()
                .awaitReady();

        client.on(WebSocketClosedEvent.class).subscribe((event) -> {
            if (event.getCode() == SESSION_INVALID) {
                final var guildId = event.getGuildId();
                final var guild = jda.getGuildById(guildId);

                if (guild == null) {
                    return;
                }

                final var connectedChannel = Objects.requireNonNull(guild.getSelfMember().getVoiceState()).getChannel();

                // somehow
                if (connectedChannel == null) {
                    return;
                }

                jda.getDirectAudioController().reconnect(connectedChannel);
            }
        });
    }
}