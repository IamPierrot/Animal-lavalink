package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import com.animal.party.Main;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class DeleteTrack extends PrefixCommand {

    static {
        registerCommand(new DeleteTrack());
    }

    DeleteTrack() {
        super("deletetrack", "Xoá 1 track tại đâu đó", "Music");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
        aliases = new String[]{"delete", "del", "huy"};
        usage = "%s %s <Vị trí của track> (có thể dùng %s queue để xem)".formatted(Main.config.getApp().prefix, name, Main.config.getApp().prefix);
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        try {
            int index = Integer.parseInt(args.getFirst());
            var guildMusicManger = getOrCreateMusicManager(event.getGuild().getIdLong());
            Objects.requireNonNull(guildMusicManger).scheduler.queue.remove(index);
        } catch (NullPointerException | NumberFormatException exception) {
            if (exception instanceof NumberFormatException) {
                event.getMessage().reply("❌ | Nhập sai cú pháp!").queue();
                return;
            }
            event.getMessage().reply("❌ | Có lỗi khi xoá track trong queue").queue();
        }

    }
}
