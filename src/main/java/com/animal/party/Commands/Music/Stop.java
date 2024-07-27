package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Stop extends PrefixCommand {

    static {
        PrefixCommand.registerCommand(new Stop());
    }

    private Stop() {
        super("stop", "dừng máy phát nhạc và rời khỏi voice");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        event.getMessage().reply("Stopped the current track and clearing the queue").queue();
        this.getOrCreateMusicManager(event.getGuild().getIdLong()).stop();
    }
}
