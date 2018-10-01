package com.andre601.suggesto.commands.owner;

import com.andre601.suggesto.listener.ReadyListener;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.text.MessageFormat;

@CommandDescription(
        name = "Blacklist",
        description = "Adds/Removes a guild to/from the blacklist",
        triggers = {"blacklist"},
        attributes = {@CommandAttribute(key = "owner")}
)
public class CmdBlacklist implements Command {

    private Guild getGuild(String guildID, ShardManager shardManager){
        Guild guild;
        try {
            guild = shardManager.getGuildById(guildID);
        }catch(Exception ex){
            guild = null;
        }

        return guild;
    }

    @Override
    public void execute(Message msg, String s) {
        TextChannel tc = msg.getTextChannel();
        String[] args = s.split(" ");

        if(args.length <= 1){
            EmbedUtil.sendError(msg, MessageFormat.format(
                    "You need to provide arguments!\n" +
                    "Usage: `{0}blacklist <add/remove> <guildID> [-reason <reason>]`",
                    Database.getPrefix(msg.getGuild())
            ));
            return;
        }

        Guild guild = getGuild(args[1], ReadyListener.getShards());
        String reason = s.split("-reason")[1];
        switch(args[0]){
            case "add":
                if(guild == null){
                    EmbedUtil.sendError(msg, "The provided GuildID was invalid!");
                    return;
                }
                if(Database.isBlacklisted(args[1])){
                    EmbedUtil.sendError(msg, "This guild is already blacklisted!");
                    return;
                }

                Database.addBlacklistedGuild(args[1], (reason == null || reason.equals("") ? null : reason));
                EmbedBuilder blacklist = EmbedUtil.getEmbed(msg.getAuthor())
                        .setColor(Color.GREEN)
                        .setDescription(MessageFormat.format(
                                "Added Guild `{0}` (`{1}`) to blacklist with reason `{2}`!",
                                guild.getName(),
                                guild.getId(),
                                (reason == null || reason.equals("") ? "No reason given!" : reason)
                        ));

                tc.sendMessage(blacklist.build()).queue();
                guild.leave().queue();
                break;

            case "remove":
                if(!Database.isBlacklisted(args[1])){
                    EmbedUtil.sendError(msg, "This guild is not blacklisted!");
                    return;
                }

                Database.removeBlacklistedGuild(args[1]);
                EmbedBuilder removeBlacklist = EmbedUtil.getEmbed(msg.getAuthor())
                        .setColor(Color.GREEN)
                        .setDescription("Removed guild from blacklist!");

                tc.sendMessage(removeBlacklist.build()).queue();
                break;

            default:
                EmbedUtil.sendError(msg, MessageFormat.format(
                        "You need to provide arguments!\n" +
                        "Usage: `{0}blacklist <add/remove> <guildID> [-reason <reason>]`",
                        Database.getPrefix(msg.getGuild())
                ));
        }
    }
}
