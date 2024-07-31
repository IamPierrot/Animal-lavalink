package com.animal.party.Component.Button;

import com.animal.party.Component.ButtonComponent;
import com.animal.party.Handlers.LoopMode;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Loop extends ButtonComponent {

    static  {
        registerComponent(new Loop());
    }

    Loop() {
        super("loop");
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, @NotNull ButtonInteractionEvent event) {
        var guildMusicManger = getOrCreateMusicManager(Objects.requireNonNull(event.getGuild()).getIdLong(), event.getChannel());
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
