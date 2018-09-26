package com.andre601.suggesto.commands.guild;

import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.MessageUtil;
import me.diax.comportment.jdacommand.Command;
import me.diax.comportment.jdacommand.CommandAttribute;
import me.diax.comportment.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@CommandDescription(
        name = "Guild",
        description = "Gives information about the guild.",
        triggers = {"guild", "server"},
        attributes = {@CommandAttribute(key = "guild")}
)
public class CmdGuild implements Command {

    @Override
    public void execute(Message msg, String s) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        EmbedBuilder guildInfo = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .addField("Users", MessageFormat.format(
                        "**Total**: {0}\n" +
                        "\n" +
                        "**Humans**: {1}\n" +
                        "**Bots**: {2}",
                        guild.getMembers().size(),
                        guild.getMembers().stream().filter(user -> !user.getUser().isBot()).count(),
                        guild.getMembers().stream().filter(user -> user.getUser().isBot()).count()
                ), true)
                .addField("Region", guild.getRegion().getEmoji(), true)
                .addField("Level", guild.getVerificationLevel().name().toLowerCase(), true)
                .addField("Owner", guild.getOwner().getAsMention(), true)
                .addField("Created", MessageUtil.formatTime(LocalDateTime.from(guild.getCreationTime())), true);

        tc.sendMessage(guildInfo.build()).queue();
    }
}
