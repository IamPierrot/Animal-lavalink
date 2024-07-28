package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Skip extends PrefixCommand  {

    static {
        PrefixCommand.registerCommand(new Skip());
    }

    private Skip() {
        super("skip", "bỏ qua hàng phát hiện tại");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        this.getOrCreateMusicManager(event.getGuild().getIdLong(), event.getChannel()).skip();
        event.getMessage().reply("Bỏ qua bài phát hiện tại!").queue();
    }
}
