package com.andre601.suggesto.commands.owner;

import com.andre601.suggesto.utils.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

@CommandDescription(
        name = "Shutdown",
        description = "Disables the bot",
        triggers = {"shutdown", "sleep", "disable"},
        attributes = {@CommandAttribute(key = "owner")}
)
public class CmdShutdown implements Command {

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();

        EmbedBuilder invite = EmbedUtil.getEmbed(msg.getAuthor())
                .setAuthor(
                        msg.getJDA().getSelfUser().getName(),
                        null,
                        msg.getJDA().getSelfUser().getEffectiveAvatarUrl()
                )
                .setDescription("disabling bot...");

        tc.sendMessage(invite.build()).queue(message -> System.exit(0));
    }
}
