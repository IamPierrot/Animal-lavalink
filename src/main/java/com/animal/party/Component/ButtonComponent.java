package com.animal.party.Component;

import com.animal.party.Component.Button.Back;
import com.animal.party.Component.Button.Loop;
import com.animal.party.Component.Button.Pause;
import com.animal.party.Component.Button.Skip;
import com.animal.party.Utils;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ButtonComponent extends Utils {
    final String name;
    protected final static Logger logger = getLogger(ButtonComponent.class);
    protected boolean voiceChannel = false;
    public static Map<String, ButtonComponent> buttons = new HashMap<>();

    protected ButtonComponent(String name) {
        this.name = name;
    }

    protected static void registerComponent(ButtonComponent button) {
        buttons.put(button.name, button);
    }

    public abstract void callback(LavalinkClient client, @NotNull ButtonInteractionEvent event);

    public static void loadButtonComponents() {
        Class<?>[] buttonComponents = {
                Loop.class,
                Skip.class,
                Pause.class,
                Back.class
                // Add other command classes here, e.g., Pause.class, Stop.class, etc.
        };
        for (Class<?> command : buttonComponents) {
            try {
                Class.forName(command.getName());
            } catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public static void handleButtonComponent(LavalinkClient client, @NotNull ButtonInteractionEvent event) {
        var customId = event.getComponentId();
        var buttonObject = buttons.get(customId);
        if (buttonObject == null) return;

        var member = event.getMember();
        if (member == null) return;


        if (isNotSameVoice(member.getVoiceState(), Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState(), event.getMessage())) return;

        buttonObject.callback(client, event);
    }
}
