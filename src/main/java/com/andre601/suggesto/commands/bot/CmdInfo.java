package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.Supporto;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.LinkUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

@CommandDescription(
        name = "Info",
        description = "Gives you basic info about the bot",
        triggers = {"info", "information"},
        attributes = {
                @CommandAttribute(key = "bot")
        }
)
public class CmdInfo implements Command {

    @Override
    public void execute(Message msg, String s) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        EmbedBuilder info = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor(
                        msg.getJDA().getSelfUser().getName(),
                        LinkUtil.INVITE_BOT(msg.getJDA()),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setThumbnail(msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription(String.format(
                        "Hey! I'm %s and I'm a ticket-bot with a small twist!\n" +
                        "Instead of you having to type a command to create a ticket, you can simply type your " +
                        "message in a ticket-channel and I will create a ticket for you!",
                        msg.getJDA().getSelfUser().getName()
                ))
                .addField("For Guild-Owner/Admins:", MessageFormat.format(
                        "To set a channel for making tickets, simply type `{0}settings channel set <#channel>` " +
                        "(Replace `<#channel>` with a channel-mention.) to set it.\n" +
                        "You can optionally set a category, where tickets will be created and a staff-role for users " +
                        "that should be able to close a ticket.",
                        Database.getPrefix(guild)
                ), false);

        tc.sendMessage(info.build()).queue();
    }
}
