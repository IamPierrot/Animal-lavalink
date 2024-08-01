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
        super("stop", "dừng máy phát nhạc và rời khỏi voice", "Music");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
        aliases = new String[]{"dung", "yamate", "cut", "cook", "thuongem"};
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        getOrCreateMusicManager(event.getGuild().getIdLong(), event.getChannel()).stop();
        event.getJDA().getDirectAudioController().disconnect(event.getGuild());
        event.getMessage().reply("Đã dọn sách hàng chờ và xin chào tạm biệt <3").queue();
    }
}
