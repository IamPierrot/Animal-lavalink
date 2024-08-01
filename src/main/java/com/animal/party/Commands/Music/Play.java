package com.animal.party.Commands.Music;

import com.animal.party.Commands.PrefixCommand;
import com.animal.party.Handlers.AudioLoader;
import com.animal.party.Main;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;

public class Play extends PrefixCommand {

    static {
        registerCommand(new Play());
    }

    private Play() {
        super("play", "chơi 1 bài nhạc", "Music");
    }

    @Override
    protected void initialize() {
        voiceChannel = true;
        aliases = new String[]{"p", "choi"};
        usage = "%s %s <tên bài hát | link youtube/spotify>".formatted(Main.config.getApp().prefix, name);
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        var guild = event.getGuild();

        if (!Objects.requireNonNull(guild.getSelfMember().getVoiceState()).inAudioChannel()) {
            joinHelper(event);
        }

        if (args.isEmpty()) {
            event.getMessage().reply("Sai cú pháp, cú pháp: %s".formatted(usage)).queue();
            return;
        }

        final String identifier = String.join(" ", args);

        String query = identifier.startsWith("https") ? identifier : "ytsearch:" + identifier;

        final long guildId = guild.getIdLong();
        final Link link = client.getOrCreateLink(guildId);
        final var guildMusicManager = getOrCreateMusicManager(guildId, event.getChannel());

        link.loadItem(query).subscribe(new AudioLoader(event, guildMusicManager));
    }
}
