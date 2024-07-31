package com.animal.party.Component.Button;

import com.animal.party.Component.ButtonComponent;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Skip extends ButtonComponent {

    static  {
        registerComponent(new Skip());
    }

    Skip() {
        super("skip");
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, @NotNull ButtonInteractionEvent event) {
        this.getOrCreateMusicManager(Objects.requireNonNull(event.getGuild()).getIdLong(), event.getChannel()).skip();
        event.getMessage().reply("Bỏ qua bài phát hiện tại!").queue();
        event.getMessage().delete().queue();
    }
}