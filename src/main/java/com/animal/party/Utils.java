package com.animal.party;

import com.animal.party.Handlers.GuildMusicManager;
import com.animal.party.Listener.JDAListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static Map<String, Long> cooldowns = new HashMap<>();

    public static GuildMusicManager getOrCreateMusicManager(long guildId, MessageChannelUnion metadata) {
        synchronized (JDAListener.class) {
            var guildMusicManager = JDAListener.musicManagers.computeIfAbsent(guildId, id -> new GuildMusicManager(id, metadata));

            if (guildMusicManager.metadata.getIdLong() != metadata.getIdLong()) {
                guildMusicManager.metadata = metadata;
            }

            return guildMusicManager;

        }
    }   public static @Nullable GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized (JDAListener.class) {
            return JDAListener.musicManagers.getOrDefault(guildId, null);
        }
    }

    public static void setTimeout(Runnable runnable, long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                getLogger("Error").error(e.getMessage());
            }
        }).start();
    }

    public static void joinHelper(@NotNull MessageReceivedEvent event) {
        final Member member = event.getMember();
        assert member != null;
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(Objects.requireNonNull(memberVoiceState.getChannel()));
        }

        getOrCreateMusicManager(member.getGuild().getIdLong(), event.getChannel());
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static boolean isNotSameVoice(@Nullable GuildVoiceState user_1, @Nullable GuildVoiceState user_2, Message message) {
        if (user_1 == null || user_2 == null) return true;

        if (user_1.getChannel() == null) {
            message.replyEmbeds(new EmbedBuilder().setAuthor("❌ | Bạn cần vào voice để thực hiện lệnh này!").build()).queue();
            return true;
        }
        if (user_2.getChannel() != null && user_1.getChannel().getIdLong() != user_2.getChannel().getIdLong()) {
            message.replyEmbeds(new EmbedBuilder().setAuthor("❌ | Bạn không có ở cùng voice với tui~~").build()).queue();
            return true;
        }
        return false;
    }
}
