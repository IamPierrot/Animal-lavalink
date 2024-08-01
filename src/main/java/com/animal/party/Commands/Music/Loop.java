package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import com.animal.party.Handlers.LoopMode;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Loop extends PrefixCommand {

    static  {
        PrefixCommand.registerCommand(new Loop());
    }

    private Loop() {
        super("loop", "thiết lặp chế độ lập", "Music");
    }

    @Override
    protected void initialize() {

    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        var guildMusicManger = getOrCreateMusicManager(event.getGuild().getIdLong(), event.getChannel());
        String[] methods = {"Lặp bài hát", "Lặp cả hàng chờ", "tắt vòng lặp"};
        var loopMode = guildMusicManger.scheduler.getLoopMode();

        if (guildMusicManger.getPlayer().isEmpty()) {
            event.getMessage().replyEmbeds(new EmbedBuilder()
                    .setAuthor("Không có gì đang phát ấy ? thử lại ikkk.... ❌")
                    .build()).queue();
        } else {
            switch (loopMode) {
                case 0: {
                    guildMusicManger.scheduler.setLoopMode(LoopMode.TRACK);
                    break;
                }
                case 1: {
                    guildMusicManger.scheduler.setLoopMode(LoopMode.QUEUE);
                    break;
                }
                case 2: {
                    guildMusicManger.scheduler.setLoopMode(LoopMode.NONE);
                    break;
                }
                default: {
                    break;
                }
            }
            var loopEmbed = new EmbedBuilder()
                    .setDescription("Thiết lập chế độ : **%s** ✅".formatted(methods[loopMode]));

            event.getMessage().replyEmbeds(loopEmbed.build()).queue();
        }

    }
}
