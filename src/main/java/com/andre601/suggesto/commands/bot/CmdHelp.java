package com.andre601.suggesto.commands.bot;

import com.andre601.suggesto.Supporto;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.PermUtil;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Help",
        description =
                "Shows you this help-list\n" +
                "Add a command at the end, to get more info.",
        triggers = {"help", "commands", "command"},
        attributes = {
                @CommandAttribute(key = "bot")
        }
)
public class CmdHelp implements Command {

    private Paginator.Builder builder;

    private HashMap<String, String> categories = new LinkedHashMap<String, String>(){
        {
            put("bot", "⚙");
            put("guild", "\uD83C\uDFAE");
            put("tickets", "\uD83D\uDCC4");
            put("owner", "⛔");
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

    private String firstUppercase(String word){
        return Character.toString(word.charAt(0)).toUpperCase() + word.substring(1).toLowerCase();
    }

    private static boolean isCommand(Command command){
        return command.getDescription() != null || command.hasAttribute("description");
    }

    @Override
    public void execute(Message msg, String s) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        builder = new Paginator.Builder().setEventWaiter(Supporto.waiter).setTimeout(1, TimeUnit.MINUTES);

        String prefix = Database.getPrefix(guild);

        HashMap<String, StringBuilder> builders = new LinkedHashMap<>();

        for(Map.Entry<String, String> category : categories.entrySet()){
            builders.put(category.getKey(), new StringBuilder());
        }

        for(Command command : Supporto.COMMAND_HANDLER.getCommands()){
            String category;

            if(!command.hasAttribute("owner")){
                category = builders.keySet().stream().filter(command::hasAttribute).findFirst().get();
            }else{
                category = "owner";
            }

            builders.get(category).append(String.format(
                    "[`%s%s`](https://github.com/Andre601/Supporto '%s')\n",
                    prefix,
                    command.getDescription().name(),
                    command.getDescription().description()
                )
            );
        }

        for(Map.Entry<String, StringBuilder> builderMap : builders.entrySet()){
            if(builderMap.getKey().equals("owner") && !PermUtil.isOwner(msg)){
                continue;
            }

            builder.addItems(String.format(
                    "User the reactions, to navigate through the pages.\n" +
                    "Type `%shelp [command]` or hover over a command, to get more information about it.\n" +
                    "\n" +
                    "%s **%s**\n" +
                    "%s",
                    prefix,
                    categories.get(builderMap.getKey()),
                    firstUppercase(builderMap.getKey()),
                    builderMap.getValue()
            ));
        }

        if(s.length() != 0){
            Command command = Supporto.COMMAND_HANDLER.findCommand(s.split(" ")[0]);

            if(command == null || !isCommand(command)){
                EmbedUtil.sendError(msg, "This command does not exist!");
                return;
            }
            tc.sendMessage(commandHelp(msg, command, prefix)).queue();
        }else{
            builder.setText("")
                    .setItemsPerPage(1)
                    .waitOnSinglePage(false)
                    .setFinalAction(message -> {
                        if(message != null)
                            message.delete().queue();
                    })
                    .build()
                    .display(tc);
        }
    }
}
