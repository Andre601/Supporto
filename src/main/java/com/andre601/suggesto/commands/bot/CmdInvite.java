package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.listener.ReadyListener;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.LinkUtil;
import me.diax.comportment.jdacommand.Command;
import me.diax.comportment.jdacommand.CommandAttribute;
import me.diax.comportment.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

@CommandDescription(
        name = "Invite",
        description = "Gives you links.",
        triggers = {"invite", "links", "link"},
        attributes = {@CommandAttribute(key = "bot")}
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
                        "[`Support-Guild`]({1})",
                        LinkUtil.getInviteURL(ReadyListener.getJda()),
                        LinkUtil.guild_invite
                ));

        tc.sendMessage(invite.build()).queue();
    }
}
