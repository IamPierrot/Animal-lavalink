package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Pause extends PrefixCommand {

    static {
        PrefixCommand.registerCommand(new Pause());
    }

    private Pause() {
        super("pause", "tạm dừng hoặc tiếp tục hàng phát");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        var guild = event.getGuild();
        client.getOrCreateLink(guild.getIdLong())
                .getPlayer()
                .flatMap((player) -> player.setPaused(!player.getPaused()))
                .subscribe((player) -> {
                    event.getMessage().reply("Player has been " + (player.getPaused() ? "paused" : "resumed") + "!").queue();
                });
    }
}
