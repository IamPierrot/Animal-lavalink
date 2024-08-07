package com.animal.party.Component;

import com.animal.party.Utils;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class ButtonComponent extends Utils {
    protected final static Logger logger = getLogger(ButtonComponent.class);
    public static Map<String, ButtonComponent> buttons = new HashMap<>();
    final String name;
    protected boolean voiceChannel = false;

    protected ButtonComponent(String name) {
        this.name = name;
    }

    protected static void registerComponent(ButtonComponent button) {
        buttons.put(button.name, button);
    }

    public static void loadButtonComponents() {
        Reflections reflections = new Reflections("com.animal.party.Component");
        Set<Class<? extends ButtonComponent>> buttonComponents = reflections.getSubTypesOf(ButtonComponent.class);

        for (Class<? extends ButtonComponent> buttonClass : buttonComponents) {
            try {
                Class.forName(buttonClass.getName());
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


        if (isNotSameVoice(member.getVoiceState(), Objects.requireNonNull(event.getGuild()).getSelfMember().getVoiceState(), event.getMessage()))
            return;

        buttonObject.callback(client, event);
    }

    public abstract void callback(LavalinkClient client, @NotNull ButtonInteractionEvent event);
}
