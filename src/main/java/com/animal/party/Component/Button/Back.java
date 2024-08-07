package com.animal.party.Component.Button;

import com.animal.party.Component.ButtonComponent;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Back extends ButtonComponent {

    static {
        registerComponent(new Back());
    }

    Back() {
        super("back");
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, @NotNull ButtonInteractionEvent event) {
        try {
            Objects.requireNonNull(getOrCreateMusicManager(Objects.requireNonNull(event.getGuild()).getIdLong())).back();
            event.getInteraction().reply("Trở về track phía trước!").queue();
        } catch (NullPointerException e) {
            event.getInteraction().reply("❌ | có lỗi khi trở về quá khứ!").queue();
        }
    }
}
