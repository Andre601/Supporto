package com.andre601.suggesto.commands;

import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import me.diax.comportment.jdacommand.Command;
import me.diax.comportment.jdacommand.CommandAttribute;
import me.diax.comportment.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.List;

@CommandDescription(
        name = "Settings",
        description = "Change settings of the bot.",
        triggers = {"settings", "options"},
        attributes = {@CommandAttribute(key = "manageServer")}
)
public class CmdSettings implements Command {

    private TextChannel getSupportChannel(Guild guild){
        TextChannel support;
        try{
            support = guild.getTextChannelById(Database.getSupportChannel(guild));
        }catch (Exception ex){
            support = null;
        }

        return support;
    }

    private Category getSupportCategory(Guild guild){
        Category category;
        try{
            category = guild.getCategoryById(Database.getCategory(guild));
        }catch (Exception ex){
            category = null;
        }

        return category;
    }

    private Category getSupportCategory(Guild guild, String id){
        Category category;
        try{
            category = guild.getCategoryById(id);
        }catch (Exception ex){
            category = null;
        }

        return category;
    }

    private void sendUsage(Message msg){
        TextChannel tc = msg.getTextChannel();
        TextChannel support = getSupportChannel(msg.getGuild());
        Category category = getSupportCategory(msg.getGuild());

        EmbedBuilder settings = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle("Guild-Settings")
                .setDescription(MessageFormat.format(
                    "This are the current settings of the guild.\n" +
                            "\n" +
                            "**Prefix**: `{0}`\n" +
                            "\n" +
                            "**Support-channel**: {1}\n" +
                            "**Support-category**: {2}\n" +
                            "\n" +
                            "Type `{0}help settings` to get info on how to change settings.",
                        Database.getPrefix(msg.getGuild()),
                        (support == null ? "`No channel set`" : support.getAsMention()),
                        (category == null ? "`No category set`" : category.getName())
                ));

        tc.sendMessage(settings.build()).queue();
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        Guild guild = msg.getGuild();
        String[] args = s.trim().split(" ");

        if(args.length == 0){
            sendUsage(msg);
            return;
        }

        switch (args[0].toLowerCase()){
            case "prefix":
                if(args.length == 1){
                    tc.sendMessage(MessageFormat.format(
                            "{0} Please use `set <prefix>` or `reset` to change the prefix!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                    return;
                }
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length >= 3){
                        Database.setPrefix(guild, args[2]);
                        EmbedBuilder success = EmbedUtil.getEmbed()
                                .setColor(Color.GREEN)
                                .setDescription(MessageFormat.format(
                                        "Prefix changed to `{0}`",
                                        args[2]
                                ));
                        tc.sendMessage(success.build()).queue();
                    }else{
                        tc.sendMessage(MessageFormat.format(
                                "{0} You need to provide a prefix!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setPrefix(guild, "t_");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Prefix reseted to `t_`");
                    tc.sendMessage(success.build()).queue();
                }else{
                    tc.sendMessage(MessageFormat.format(
                            "{0} Please use `set <prefix>` or `reset` to change the prefix!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                }
                break;

            case "channel":
                if(args.length == 1){
                    tc.sendMessage(MessageFormat.format(
                            "{0} Please use `set <#channel>` or `reset` to change the channel!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                    return;
                }
                if(args[1].equalsIgnoreCase("set")){
                    List<TextChannel> channels = msg.getMentionedChannels();
                    if(!channels.isEmpty()){
                        Database.setSupportChannel(guild, channels.get(0).getId());
                        EmbedBuilder success = EmbedUtil.getEmbed()
                                .setColor(Color.GREEN)
                                .setDescription(MessageFormat.format(
                                        "Channel set to {0}",
                                        channels.get(0).getAsMention()
                                ));
                        tc.sendMessage(success.build()).queue();
                    }else{
                        tc.sendMessage(MessageFormat.format(
                                "{0} You need to provide a channel!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setSupportChannel(guild, "none");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Channel reseted");
                    tc.sendMessage(success.build()).queue();
                }else{
                    tc.sendMessage(MessageFormat.format(
                            "{0} Please use `set <#channel>` or `reset` to change the channel!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                }
                break;

            case "category":
                if(args.length == 1){
                    tc.sendMessage(MessageFormat.format(
                            "{0} Please use `set <CategoryID>` or `reset` to change the category!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                    return;
                }
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length >= 3){
                        Category category = getSupportCategory(guild, args[2].trim());
                        if(category == null){
                            tc.sendMessage(MessageFormat.format(
                                    "{0} The provided Category-ID was invalid!",
                                    msg.getAuthor().getAsMention()
                            )).queue();
                            return;
                        }
                        Database.setCategory(guild, category.getId());
                        EmbedBuilder success = EmbedUtil.getEmbed()
                                .setColor(Color.GREEN)
                                .setDescription(MessageFormat.format(
                                        "Category set to `{0}` (`{1}`)",
                                        category.getName(),
                                        category.getId()
                                ));
                        tc.sendMessage(success.build()).queue();
                    }else{
                        tc.sendMessage(MessageFormat.format(
                                "{0} You need to provide a category!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setCategory(guild, "none");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Category reseted");
                    tc.sendMessage(success.build()).queue();
                }else{
                    tc.sendMessage(MessageFormat.format(
                            "{0} Please use `set <categoryID>` or `reset` to change the category!",
                            msg.getAuthor().getAsMention()
                    )).queue();
                }
                break;

            default:
                sendUsage(msg);
        }
    }
}
