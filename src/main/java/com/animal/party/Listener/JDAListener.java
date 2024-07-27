package com.animal.party.Listener;

import com.animal.party.Commands.PrefixCommand;
import com.animal.party.Handlers.GuildMusicManager;

import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

public class JDAListener extends ListenerAdapter {
    private static final Logger LOG =  LoggerFactory.getLogger(JDAListener.class);

    public static final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();
    private final LavalinkClient client;

    public JDAListener(LavalinkClient client) {
        this.client = client;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        PrefixCommand.loadCommands();
        LOG.info("{} is ready!", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        PrefixCommand.handlePrefixCommand(client, event);
    }
}