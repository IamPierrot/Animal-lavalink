package com.animal.party.Commands.Info;

import com.animal.party.Commands.PrefixCommand;
import com.animal.party.Main;
import dev.arbjerg.lavalink.client.LavalinkClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Help extends PrefixCommand {

    static {
        registerCommand(new Help());
    }


    Help() {
        super("help", "xem hướng dẫn sử dụng bot", "Info");
    }

    @Override
    protected void initialize() {
        showHelp = false;
    }

    @Override
    public void callback(LavalinkClient client, MessageReceivedEvent event, List<String> args) {
        if (args.isEmpty()) {
            var helpEmbed = new EmbedBuilder()
                    .setFooter("Prefix của bot là %s".formatted(Main.config.getApp().prefix))
                    .setDescription("Muốn có thêm hướng dẫn chi tiết thì có thể dùng `%s help {tên command}`".formatted(Main.config.getApp().prefix))
                    .setColor(Color.WHITE)
                    .setAuthor("Help Menu - %s".formatted(event.getJDA().getSelfUser().getName()), null, event.getJDA().getSelfUser().getAvatarUrl());

            Map<String, List<String>> descriptionMaps = new HashMap<>();

            prefixCommandMap.forEach((name, command) -> {
                if (!command.showHelp) return;
                descriptionMaps
                        .computeIfAbsent(command.category, k -> new ArrayList<>())
                        .add(command.name);
            });

            // Adding fields to the embed for each category of commands
            // Adding fields to the embed for each category of commands
            descriptionMaps.forEach((category, commands) -> {
                StringBuilder commandsList = new StringBuilder();
                for (int i = 0; i < commands.size(); i++) {
                    commandsList.append("`").append(commands.get(i)).append("` ");
                    if ((i + 1) % 7 == 0) {
                        commandsList.append("\n");
                    }
                }
                helpEmbed.addField(category.toUpperCase(), commandsList.toString(), false);
            });


            event.getMessage().replyEmbeds(helpEmbed.build()).queue();
        } else {
            String commandName = String.join("", args).toLowerCase();
            var commandObject = getCommand(commandName);
            if (Objects.isNull(commandObject)) {
                event.getMessage().reply("Tên command không tồn tại!").queue();
                return;
            }

            var embed = new EmbedBuilder()
                    .setAuthor("Bảng hướng dẫn command", null, event.getJDA().getSelfUser().getAvatarUrl())
                    .setFooter("Chúc bạn nghe nhạc vui vẻ <3", event.getAuthor().getAvatarUrl())
                    .setColor(Color.CYAN)
                    .setTitle(commandObject.name)
                    .setDescription(commandObject.description)
                    .addField("Loại Command", commandObject.category.toUpperCase(), true)
                    .addField("Tên rút gọn", String.join(" " ,commandObject.aliases), true)
                    .addField("Cách dùng", commandObject.usage, false)
                    .addBlankField(false)
                    .addField("Lưu ý", "< > (bắt buộc) : Không cần phải ghi <>\n{ } (Không bắt buộc) : Không cần phải ghi {}", false)
                    .build();

            event.getMessage().replyEmbeds(embed).queue();
        }

    }
}
