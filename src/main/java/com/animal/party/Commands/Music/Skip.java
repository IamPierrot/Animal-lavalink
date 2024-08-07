package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class Skip extends PrefixCommand  {

    static {
        PrefixCommand.registerCommand(new Skip());
    }

    private Skip() {
        super("skip", "bỏ qua hàng phát hiện tại", "Music");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        try {
            var musicManager = getOrCreateMusicManager(event.getGuild().getIdLong());
            event.getMessage().reply("Bỏ qua bài phát hiện tại!").queue();
            Objects.requireNonNull(musicManager).skip();
        } catch (NullPointerException e) {
            event.getMessage().reply("❌ | Có lỗi khi bỏ qua bài hát").queue();
        }
    }
}
