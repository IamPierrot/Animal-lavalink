package com.animal.party.Component.Button;

import com.animal.party.Component.ButtonComponent;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Stop extends ButtonComponent {

    static {
        registerComponent(new Stop());
    }

    Stop() {
        super("stop");
    }

    @Override
    public void callback(LavalinkClient client, @NotNull ButtonInteractionEvent event) {
        try {
            Objects.requireNonNull(getOrCreateMusicManager(Objects.requireNonNull(event.getGuild()).getIdLong())).stop();
            event.getJDA().getDirectAudioController().disconnect(event.getGuild());
            event.getInteraction().reply("Đã dọn sách hàng chờ và xin chào tạm biệt <3").queue();

        } catch (Exception e) {
            event.getInteraction().reply("❌ | Có lỗi khi stop").queue();
        }
    }
}
