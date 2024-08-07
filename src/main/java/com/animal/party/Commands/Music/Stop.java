package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class Stop extends PrefixCommand {

    static {
        PrefixCommand.registerCommand(new Stop());
    }

    private Stop() {
        super("stop", "dừng player và ngắt kết nối", "music");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
        aliases = new String[]{"dung", "yamate", "cut", "cook", "thuongem"};
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        try {
            var musicManager = getOrCreateMusicManager(event.getGuild().getIdLong());
            event.getJDA().getDirectAudioController().disconnect(event.getGuild());
            Objects.requireNonNull(musicManager).stop();
            event.getMessage().reply("Đã dọn sách hàng chờ và xin chào tạm biệt <3").queue();
        } catch (NullPointerException e) {
            event.getMessage().reply("❌ | Có lỗi khi dọn hàng chờ").queue();
        }
    }
}
