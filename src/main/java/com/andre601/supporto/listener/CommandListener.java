package com.andre601.supporto.listener;

import ch.qos.logback.classic.Logger;
import com.andre601.supporto.Supporto;
import com.andre601.supporto.commands.Command;
import com.andre601.supporto.util.constants.Links;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandListener extends ListenerAdapter{
    
    private Logger logger = (Logger) LoggerFactory.getLogger(CommandListener.class);
    
    private final ThreadGroup CMD_THREAD = new ThreadGroup("CommandThread");
    private final Executor CMD_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(CMD_THREAD, "CommandThread-Pool")
    );
    
    private Supporto bot;
    private final CommandHandler<Message> CMD_HANDLER;
    
    public CommandListener(Supporto bot, CommandHandler<Message> commandHandler){
        this.bot = bot;
        this.CMD_HANDLER = commandHandler;
    }
    
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event){
        CMD_EXECUTOR.execute(
                () -> {
                    Message msg = event.getMessage();
                    Guild guild = event.getGuild();
                    Member member = event.getMember();
                    TextChannel tc = event.getChannel();
                    
                    if(member == null)
                        return;
                    
                    if(member.getUser().isBot())
                        return;
                    
                    String prefix = bot.getPrefix(guild.getId());
                    String raw = msg.getContentRaw();
                    
                    if(!raw.toLowerCase().startsWith(prefix) && raw.equals(guild.getSelfMember().getAsMention()))
                        return;
                    
                    if(!guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_WRITE))
                        return;
                    
                    if(!guild.getSelfMember().hasPermission(tc, Permission.MESSAGE_EMBED_LINKS)){
                        tc.sendMessage(
                                "I cannot perform any command, due to a lack of permissions!\n" +
                                "\n" +
                                "**Required permission**: `Embed Links`\n" +
                                "\n" +
                                "Please make sure I have the required permission in this channel!"
                        ).queue(del -> del.delete().queueAfter(10, TimeUnit.SECONDS));
                        return;
                    }
                    
                    if(tc.getId().equals(bot.getTicketChannel(guild.getId()))){
                        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                                .setColor(0xFF0000)
                                .setDescription(
                                        "You can't execute commands in a Ticket-Channel!"
                                )
                                .build();
    
                        tc.sendMessage(embed).queue(del -> del.delete().queueAfter(5, TimeUnit.SECONDS));
                        return;
                    }
                    
                    if(raw.equals(guild.getSelfMember().getAsMention())){
                        MessageEmbed embed = bot.getEmbedUtil().getEmbed()
                                .setDescription(String.format(
                                        "My prefix on this Discord is `%s`\n" +
                                        "Use `%shelp` for a list of commands.",
                                        prefix,
                                        prefix
                                ))
                                .build();
                        
                        tc.sendMessage(embed).queue();
                        return;
                    }
                    
                    String[] args = split(raw, prefix.length());
                    String cmd = args[0];
                    
                    if(cmd == null)
                        return;
                    
                    Command command = (Command)CMD_HANDLER.findCommand(cmd.toLowerCase());
                    if(command == null)
                        return;
                    
                    try{
                        CMD_HANDLER.execute(command, msg, args[1] == null ? "" : args[1]);
                    }catch(Exception ex){
                        MessageEmbed error = bot.getEmbedUtil().getEmbed()
                                .setColor(0xFF0000)
                                .setDescription(
                                        "There was an error while performing a command!\n" +
                                        "Please report this issue either in our Discord, or on GitHub (Links below).\n" +
                                        "\n" +
                                        "Make sure to provide information like the time of the error and error message."
                                )
                                .addField("Links", String.format(
                                        "[`Discord`](%s)\n" + 
                                        "[`GitHub`](%s)",
                                        Links.DISCORD_INVITE,
                                        Links.GITHUB
                                ), false)
                                .addField("Error Message", String.format(
                                        "`%s`",
                                        ex.getMessage() == null ? "No error message" : ex.getMessage()
                                ), false)
                                .build();
                        
                        tc.sendMessage(error).queue();
                    }
                }
        );
    }
    
    private String[] split(String text, int length){
        return Arrays.copyOf(text.substring(length).trim().split("\\s+", 2), 2);
    }
}
