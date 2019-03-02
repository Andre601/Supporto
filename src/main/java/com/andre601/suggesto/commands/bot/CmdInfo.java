package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.constants.Links;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

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
                        Links.INVITE_BOT(msg.getJDA()),
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setThumbnail(msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setDescription(String.format(
                        "I'm %s and I'm a ticket-bot with a small change.\n" +
                        "Instead of writing a command, to create a ticket, members just need to type in a channel " +
                        "that the guild-owner has defined as a ticket-channel.",
                        msg.getJDA().getSelfUser().getName()
                ))
                .addField("Setup:", String.format(
                        "As a guild-owner, just run `%ssettings channel set <#channel>` to set a text channel as a " +
                        "ticket-channel. People can then type their message there, to create a ticket.",
                        Database.getPrefix(guild)
                ), false)
                .addField("Other Settings:", String.format(
                        "You can change other things of the bot like...\n" +
                        "Setting a category, where tickets are created.\n" +
                        "Setting a log-channel to log creation and deletion of tickets, as-well as other ticket " +
                        "actions\n" +
                        "Enabling/Disabling sending of a transcript in DMs, when a ticket is closed (Default is on)\n" +
                        "\n" +
                        "Just run `%shelp settings` for more information",
                        Database.getPrefix(guild)
                ),false);

        tc.sendMessage(info.build()).queue();
    }
}
