package com.animal.party;

import com.animal.party.Handlers.GuildMusicManager;
import com.animal.party.Listener.JDAListener;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Utils {
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static GuildMusicManager getOrCreateMusicManager(long guildId, MessageChannelUnion metadata) {
        synchronized (JDAListener.class) {
            var guildMusicManager = JDAListener.musicManagers.get(guildId);

            if (Objects.isNull(guildMusicManager)) {
                guildMusicManager = new GuildMusicManager(guildId, metadata);
                JDAListener.musicManagers.put(guildId, guildMusicManager);
            }

            if (guildMusicManager.metadata.getIdLong() != metadata.getIdLong()) guildMusicManager.metadata = metadata;

            return guildMusicManager;
        }
    }
}
