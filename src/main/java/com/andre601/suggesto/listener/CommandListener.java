package com.andre601.suggesto.listener;

import com.andre601.suggesto.SuggestoBot;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.PermUtil;
import me.diax.comportment.jdacommand.Command;
import me.diax.comportment.jdacommand.CommandHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommandListener extends ListenerAdapter {

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("CommandExecutor");
    private static final Executor COMMAND_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(THREAD_GROUP, r, "CommandPool")
    );

    private final CommandHandler HANDLER;

    public CommandListener(CommandHandler handler){
        this.HANDLER = handler;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMessageReceived(MessageReceivedEvent event){
        if(!ReadyListener.getReady()) return;

        COMMAND_EXECUTOR.execute(
                () -> {
                    Message msg = event.getMessage();
                    Guild guild = event.getGuild();

                    if(PermUtil.isBot(msg)) return;
                    if(PermUtil.isSelf(msg)) return;
                    if(PermUtil.isDM(msg)) return;

                    TextChannel tc = event.getTextChannel();
                    if(tc.getId().equals(Database.getSupportChannel(guild))) return;

                    String prefix = Database.getPrefix(guild);
                    String raw = msg.getContentRaw();
                    if(raw.equalsIgnoreCase(guild.getSelfMember().getAsMention())){
                        tc.sendMessage(MessageFormat.format(
                                "{0} My prefix in this guild is `{1}`!",
                                msg.getAuthor().getAsMention(),
                                prefix
                        )).queue();
                        return;
                    }

                    if(!Database.hasPrefix(msg, guild)) return;

                    String[] split = raw.split("\\s+", 2);
                    String commandString;

                    try{
                        if(raw.startsWith(prefix))
                            commandString = split[0].substring(prefix.length());
                        else
                            commandString = split[0].substring(guild.getSelfMember().getAsMention().length());
                    }catch (Exception ex){
                        return;
                    }
                    Command command = HANDLER.findCommand(commandString.toLowerCase());
                    if(command == null) return;
                    if(command.hasAttribute("Owner") && !PermUtil.isOwner(msg)) return;
                    if(!PermUtil.canSeeChannel(tc)) return;
                    if(!PermUtil.canSeeHistory(tc)) return;
                    if(!PermUtil.canSendMsg(tc)) return;
                    if(command.hasAttribute("manageServer") && !PermUtil.isAdmin(msg)){
                        tc.sendMessage(MessageFormat.format(
                                "{0} You need the permission `manage server` for this command!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                        return;
                    }
                    if(!PermUtil.canManageMsg(tc)){
                        tc.sendMessage("I need permission to manage Messages!").queue();
                        return;
                    }
                    if(!PermUtil.canEmbedLinks(tc)) {
                        tc.sendMessage(MessageFormat.format(
                                "{0} I need permission to embed links in this channel!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                        return;
                    }

                    try {
                        HANDLER.execute(command, msg, split.length > 1 ? split[1] : "");
                    }catch (Exception ex){
                        SuggestoBot.getLogger().error("Error in command", ex);
                    }
                }
        );
    }
}
