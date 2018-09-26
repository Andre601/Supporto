package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.SuggestoBot;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.PermUtil;
import me.diax.comportment.jdacommand.Command;
import me.diax.comportment.jdacommand.CommandAttribute;
import me.diax.comportment.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@CommandDescription(
        name = "Help",
        description =
                "Shows you this help-list\n" +
                "Add a command at the end, to get more info.",
        triggers = {"help", "commands", "command"},
        attributes = {@CommandAttribute(key = "bot")}
)
public class CmdHelp implements Command {

    private HashMap<String, String> categories = new HashMap<String, String>(){
        {
            put("bot", "**Bot**");
            put("guild", "**Guild**");
        }
    };

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
        EmbedBuilder embedBuilder = EmbedUtil.getEmbed(msg.getAuthor());

        String prefix = Database.getPrefix(guild);
        String gp = "\\" + prefix;

        HashMap<String, StringBuilder> builders = new HashMap<String, StringBuilder>(){
            {

            }
        };

        for(Map.Entry<String, String> category : categories.entrySet()){
            builders.put(category.getKey(), new StringBuilder());
        }

        for(Command cmd : SuggestoBot.COMMAND_HANDLER.getCommands()){
            String category;

            if(!cmd.hasAttribute("owner")){
                category = builders.keySet().stream().filter(cmd::hasAttribute).findFirst().get();
            }else{
                category = "owner";
            }

            builders.get(category).append(String.format(
                    "%s%s\n%s",
                    gp,
                    cmd.getDescription().name(),
                    cmd.getDescription().description()
            ));
        }

        for(Map.Entry<String, StringBuilder> builder : builders.entrySet()){
            if(builder.getKey().equals("owner") && !PermUtil.isOwner(msg)){
                continue;
            }

            embedBuilder.addField(categories.get(builder.getKey()),
                    builder.getValue().toString(),
                    false
            );
        }

        MessageEmbed embed = embedBuilder.build();

        if(s.length() != 0){
            Command command = SuggestoBot.COMMAND_HANDLER.findCommand(s.split(" ")[0]);

            if(command == null || !isCommand(command)){
                EmbedUtil.sendError(msg, "This command does not exist!");
                return;
            }
            tc.sendMessage(commandHelp(msg, command, gp)).queue();
        }else{
            tc.sendMessage(embed).queue();
        }
    }
}
