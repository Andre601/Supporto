package com.andre601.suggesto.commands.tickets;

import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.Color;
import java.text.MessageFormat;
import java.util.List;

@CommandDescription(
        name = "Settings",
        description =
                "Change settings of the bot.\n" +
                "\n" +
                "`prefix <set prefix|reset>` to set or reset the prefix.\n" +
                "`channel <set #channel|reset>` to set or reset the channel.\n" +
                "`category <set categoryID|reset>` to set or reset the category.\n" +
                "`role <set roleID|reset>` to set or reset the role.\n" +
                "`log <set #channel|reset>` to set or reset the log-channel.\n" +
                "`dm <on/off>` to enable or disable sending transcript in DM.",
        triggers = {"settings", "options"},
        attributes = {
                @CommandAttribute(key = "admin_only"),
                @CommandAttribute(key = "tickets")
        }
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

    private TextChannel getLogChannel(Guild guild){
        TextChannel logChannel;
        try{
            logChannel = guild.getTextChannelById(Database.getLogChannel(guild));
        }catch (Exception ex){
            logChannel = null;
        }

        return logChannel;
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

    private Role getStaffRole(Guild guild, String id){
        Role role;
        try{
            role = guild.getRoleById(id);
        }catch (Exception ex){
            role = null;
        }

        return role;
    }

    private static Role getStaffRole(Guild guild){
        Role role;
        try{
            role = guild.getRoleById(Database.getRoleID(guild));
        }catch (Exception ex){
            role = null;
        }

        return role;
    }

    private void sendUsage(Message msg){
        TextChannel tc = msg.getTextChannel();
        TextChannel support = getSupportChannel(msg.getGuild());
        Category category = getSupportCategory(msg.getGuild());
        Role role = getStaffRole(msg.getGuild());
        String dmSetting = Database.getDMSetting(msg.getGuild());
        TextChannel logChannel = getLogChannel(msg.getGuild());

        EmbedBuilder settings = EmbedUtil.getEmbed(msg.getAuthor())
                .setTitle("Guild-Settings")
                .setDescription(MessageFormat.format(
                    "This are the current settings of the guild.\n" +
                            "\n" +
                            "**Prefix**: `{0}`\n" +
                            "\n" +
                            "**Support-channel**: {1}\n" +
                            "**Support-category**: {2}\n" +
                            "**Staff-role**: {3}\n" +
                            "\n" +
                            "Sending DM-transcripts: `{4}`\n" +
                            "Log-channel: {5}\n" +
                            "\n" +
                            "Type `{0}help settings` to get info on how to change settings.",
                        Database.getPrefix(msg.getGuild()),
                        (support == null ? "`No channel set`" : support.getAsMention()),
                        (category == null ? "`No category set`" : category.getName()),
                        (role == null ? "`No role set`" : role.getAsMention()),
                        dmSetting,
                        (logChannel == null ? "`No channel set`" : logChannel.getAsMention())
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
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <prefix>` or `reset` to change or reset the prefix!"
                    );
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
                        EmbedUtil.sendError(msg, "You need to provide a prefix!");
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setPrefix(guild, "t_");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Prefix reseted to `t_`");
                    tc.sendMessage(success.build()).queue();
                }else{
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <prefix>` or `reset` to change or reset the prefix!"
                    );
                }
                break;

            case "channel":
                if(args.length == 1){
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <#channel>` or `reset` to change or reset the channel!"
                    );
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
                        EmbedUtil.sendError(
                                msg,
                                "You need to mention a channel!"
                        );
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setSupportChannel(guild, "none");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Channel reseted");
                    tc.sendMessage(success.build()).queue();
                }else{
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <#channel>` or `reset` to change or reset the channel!"
                    );
                }
                break;

            case "category":
                if(args.length == 1){
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <categoryID>` or `reset` to change or reset the category!"
                    );
                    return;
                }
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length >= 3){
                        Category category = getSupportCategory(guild, args[2].trim());
                        if(category == null){
                            EmbedUtil.sendError(
                                    msg,
                                    "The provided categoryID was invalid!"
                            );
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
                        EmbedUtil.sendError(
                                msg,
                                "You need to provide a categoryID!"
                        );
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setCategory(guild, "none");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Category reseted");
                    tc.sendMessage(success.build()).queue();
                }else{
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <categoryID>` or `reset` to change or reset the category!"
                    );
                }
                break;

            case "role":
                if(args.length == 1){
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <roleID>` or `reset` to change or reset the staff-role!"
                    );
                    return;
                }
                if(args[1].equalsIgnoreCase("set")){
                    if(args.length >= 3){
                        Role role = getStaffRole(guild, args[2].trim());
                        if(role == null){
                            EmbedUtil.sendError(
                                    msg,
                                    "The provided roleID was invalid!"
                            );
                            return;
                        }
                        Database.setRole(guild, role.getId());
                        EmbedBuilder success = EmbedUtil.getEmbed()
                                .setColor(Color.GREEN)
                                .setDescription(MessageFormat.format(
                                        "Role set to {0}",
                                        role.getAsMention()
                                ));
                        tc.sendMessage(success.build()).queue();
                    }else{
                        EmbedUtil.sendError(
                                msg,
                                "You need to provide a roleID!"
                        );
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setRole(guild, "none");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Role reseted");
                    tc.sendMessage(success.build()).queue();
                }else{
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <roleID>` or `reset` to change or reset the staff-role!"
                    );
                }
                break;

            case "log":
                if(args.length == 1){
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <#channel>` or `reset` to change or reset the log-channel!"
                    );
                    return;
                }
                if(args[1].equalsIgnoreCase("set")){
                    List<TextChannel> channels = msg.getMentionedChannels();
                    if(!channels.isEmpty()){
                        Database.setLogChannel(guild, channels.get(0).getId());
                        EmbedBuilder success = EmbedUtil.getEmbed()
                                .setColor(Color.GREEN)
                                .setDescription(MessageFormat.format(
                                        "Log-channel set to {0}",
                                        channels.get(0).getAsMention()
                                ));
                        tc.sendMessage(success.build()).queue();
                    }else{
                        EmbedUtil.sendError(
                                msg,
                                "You need to mention a channel!"
                        );
                    }
                }else
                if(args[1].equalsIgnoreCase("reset")){
                    Database.setLogChannel(guild, "none");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription("Channel reseted");
                    tc.sendMessage(success.build()).queue();
                }else{
                    EmbedUtil.sendError(
                            msg,
                            "Please use `set <#channel>` or `reset` to change or reset the log-channel!"
                    );
                }
                break;

            case "dm":
                if(args.length == 1){
                    EmbedUtil.sendError(
                            msg,
                            "Please add `on` or `off` to enable/disable sending of transcripts in DM!"
                    );
                    return;
                }
                String dmSetting = Database.getDMSetting(guild);
                if(args[1].equalsIgnoreCase("on")){
                    if(dmSetting.equalsIgnoreCase("on")){
                        EmbedUtil.sendError(
                                msg,
                                "The settings for sending DMs is already enabled!"
                        );
                        return;
                    }

                    Database.setDMSettings(guild, "on");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription(
                                    "Changed DM-Setting to `on`!\n" +
                                    "The bot will now send transcripts in DM."
                            );
                    tc.sendMessage(success.build()).queue();
                }else
                if(args[1].equalsIgnoreCase("off")){
                    if(dmSetting.equalsIgnoreCase("off")){
                        EmbedUtil.sendError(
                                msg,
                                "The settings for sending DMs is already disabled!"
                        );
                        return;
                    }

                    Database.setDMSettings(guild, "off");
                    EmbedBuilder success = EmbedUtil.getEmbed()
                            .setColor(Color.GREEN)
                            .setDescription(
                                    "Changed DM-Setting to `off`!\n" +
                                    "The bot will no longer send transcripts in DMs."
                            );
                    tc.sendMessage(success.build()).queue();
                }else{
                    EmbedUtil.sendError(
                            msg,
                            "Please add `on` or `off` to enable/disable sending of transcripts in DM!"
                    );
                }
                break;

            default:
                sendUsage(msg);
        }
    }
}
