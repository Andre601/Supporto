package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static com.andre601.suggesto.listener.ReadyListener.getShards;

@CommandDescription(
        name = "Stats",
        description = "Gives some stats.",
        triggers = {"stats", "stat", "statistics"},
        attributes = {@CommandAttribute(key = "bot")}
)
public class CmdStats implements Command {

    private String getUptime(){
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        long d = TimeUnit.MILLISECONDS.toDays(uptime);
        long h = TimeUnit.MILLISECONDS.toHours(uptime) - d * 24;
        long m = TimeUnit.MILLISECONDS.toMinutes(uptime) - h * 60 - d * 1440;
        long s = TimeUnit.MILLISECONDS.toSeconds(uptime) - m * 60 - h * 3600 - d * 86400;

        String days    = d + (d == 1 ? " day" : " days");
        String hours   = h + (h == 1 ? " hour" : " hours");
        String minutes = m + (m == 1 ? " minute" : " minutes");
        String seconds = s + (s == 1 ? " second" : " seconds");

        return MessageFormat.format(
                "{0} {1} {2} {3}",
                days,
                hours,
                minutes,
                seconds
        );
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        Guild guild = msg.getGuild();

        EmbedBuilder stats = EmbedUtil.getEmbed(msg.getAuthor())
                .addField("Uptime:", getUptime(), false)
                .addField("Guilds", String.valueOf(getShards().getGuildCache().size()), true)
                .addField("Users", MessageFormat.format(
                        "**Total**: {0}\n" +
                        "\n" +
                        "**User**: {1}\n" +
                        "**Bots**: {2}",
                        String.valueOf(getShards().getUserCache().size()),
                        String.valueOf(getShards().getUserCache().stream().filter(user -> !user.isBot()).count()),
                        String.valueOf(getShards().getUserCache().stream().filter(user -> user.isBot()).count())
                ), true)
                .addField("Shards", MessageFormat.format(
                        "**Current**: `{0}`\n" +
                        "**Total**: `{1}`",
                        msg.getJDA().getShardInfo().getShardId(),
                        getShards().getShardCache().size()
                ), true)
                .addField("Created tickets", MessageFormat.format(
                        "**Total**: `{0}`\n" +
                        "**This guild**: `{1}`",
                        Database.getTotalTickets(),
                        Database.getCurrTicketID(guild)
                ), true);

        tc.sendMessage(stats.build()).queue();
    }
}
