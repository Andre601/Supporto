package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.SuggestoBot;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;

@CommandDescription(
        name = "Help",
        description =
                "Shows you this help-list\n" +
                "Add a command at the end, to get more info.",
        triggers = {"help", "commands", "command"},
        attributes = {@CommandAttribute(key = "bot")}
)
public class CmdHelp implements Command {

    private static MessageEmbed commandHelp(Message msg, Command cmd, String prefix){
        CommandDescription description = cmd.getDescription();
        EmbedBuilder command = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle(MessageFormat.format(
                        "Command: {0}",
                        description.name()
                ))
                .setDescription(description.description())
                .addField("Usage:", MessageFormat.format(
                        "`{0}{1}`",
                        prefix,
                        description.name()
                ), true)
                .addField("Aliases:", MessageFormat.format(
                        "`{0}`",
                        String.join(", ", description.triggers())
                ), true);

        return command.build();
    }

    private static boolean isCommand(Command command){
        return command.getDescription() != null || command.hasAttribute("description");
    }

    @Override
    public void execute(Message msg, String s) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        String prefix = Database.getPrefix(guild);
        EmbedBuilder embedBuilder = EmbedUtil.getEmbed(msg.getAuthor())
                .setDescription(MessageFormat.format(
                        "Type `{0}Help [command]` for more info about a command.",
                        prefix
                ))
                .addField("Bot:", MessageFormat.format(
                        "`{0}Help`\n" +
                        "`{0}Invite`\n" +
                        "`{0}Stats`",
                        prefix
                        ), false)
                .addField("Guild:", MessageFormat.format(
                        "`{0}Guild`\n" +
                        "`{0}Settings`",
                        prefix
                ), false);

        if(s.length() != 0){
            Command command = SuggestoBot.COMMAND_HANDLER.findCommand(s.split(" ")[0]);

            if(command == null || !isCommand(command)){
                EmbedUtil.sendError(msg, "This command does not exist!");
                return;
            }
            tc.sendMessage(commandHelp(msg, command, prefix)).queue();
        }else{
            tc.sendMessage(embedBuilder.build()).queue();
        }
    }
}
