package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.listener.ReadyListener;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.LinkUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

@CommandDescription(
        name = "Invite",
        description = "Gives you links.",
        triggers = {"invite", "links", "link"},
        attributes = {
                @CommandAttribute(key = "bot")
        }
)
public class CmdInvite implements Command {

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        EmbedBuilder invite = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor(
                        msg.getJDA().getSelfUser().getName(),
                        null,
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setDescription(MessageFormat.format(
                        "Here are some links, that can be important.\n" +
                        "\n" +
                        "[`Bot-Invite`]({0})\n" +
                        "[`Support-Guild`]({1})\n" +
                        "[`GitHub`]({2})",
                        LinkUtil.INVITE_BOT(ReadyListener.getJda()),
                        LinkUtil.INVITE_GUILD,
                        LinkUtil.GITHUB
                ));

        tc.sendMessage(invite.build()).queue();
    }
}
