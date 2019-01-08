package com.andre601.suggesto.listener;

import com.andre601.suggesto.Supporto;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.LinkUtil;
import com.andre601.suggesto.utils.PermUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
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

    private Role getStaffRole(Guild guild){
        Role role;
        try{
            role = guild.getRoleById(Database.getRoleID(guild));
        }catch (Exception ex){
            role = null;
        }

        return role;
    }

    private TextChannel getSupportChannel(Guild guild){
        TextChannel channel;
        try{
            channel = guild.getTextChannelById(Database.getSupportChannel(guild));
        }catch (Exception ex){
            channel = null;
        }

        return channel;
    }

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
                    TextChannel support = getSupportChannel(guild);
                    if(support != null)
                        if(tc == support) return;
                    if(!Database.hasPrefix(msg, guild)) return;

                    String prefix = Database.getPrefix(guild);
                    String raw = msg.getContentRaw();

                    if(!PermUtil.canSeeChannel(tc)) return;
                    if(!PermUtil.canSeeHistory(tc)) return;
                    if(!PermUtil.canSendMsg(tc)) return;

                    if(raw.startsWith(event.getJDA().getSelfUser().getAsMention()) &&
                            raw.length() == event.getJDA().getSelfUser().getAsMention().length()){
                        tc.sendMessage(String.format(
                                "%s My prefix on this guild is `%s`\n" +
                                "You can run `%shelp` for a list of my commands.",
                                msg.getAuthor().getAsMention(),
                                prefix,
                                prefix
                        )).queue();
                        return;
                    }

                    if(raw.startsWith(guild.getSelfMember().getAsMention()) &&
                            raw.length() == guild.getSelfMember().getAsMention().length()){
                        tc.sendMessage(String.format(
                                "%s My prefix on this guild is `%s`\n" +
                                "You can run `%shelp` for a list of my commands.",
                                msg.getAuthor().getAsMention(),
                                prefix,
                                prefix
                        )).queue();
                        return;
                    }


                    String[] split = raw.split("\\s+", 2);
                    String commandString;

                    Role role = getStaffRole(guild);

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
                    if(command.hasAttribute("owner") && !PermUtil.isOwner(msg)) return;
                    if(!PermUtil.canEmbedLinks(tc)) {
                        tc.sendMessage(String.format(
                                "%s I need permission to embed links in this channel!",
                                msg.getAuthor().getAsMention()
                        )).queue();
                        return;
                    }
                    if(!PermUtil.canAddReaction(tc)){
                        EmbedUtil.sendError(msg, "I need permissions, to add reactions!");
                        return;
                    }
                    if(command.hasAttribute("admin_only")){
                        if(!PermUtil.isAdmin(msg)) {
                            EmbedUtil.sendError(
                                    msg,
                                    "You need the permission `manage server` for this command!"
                            );
                            return;
                        }
                    }
                    if(command.hasAttribute("staff+")){
                        if(role != null) {
                            if (!PermUtil.isStaff(msg.getMember(), role)) {
                                if(!PermUtil.isAdmin(tc, msg.getMember())){
                                    EmbedUtil.sendError(
                                            msg,
                                            "You need to have Staff-role or `manage server` permission"
                                    );
                                    return;
                                }
                            }
                        }else
                        if(!PermUtil.isAdmin(tc, msg.getMember())){
                            EmbedUtil.sendError(
                                    msg,
                                    "You need to have Staff-role or `manage server` permission"
                            );
                            return;
                        }
                    }

                    try {
                        HANDLER.execute(command, msg, split.length > 1 ? split[1] : "");
                        if(PermUtil.canManageMsg(tc)) msg.delete().queue();
                    }catch (Exception ex){
                        EmbedUtil.sendError(
                                msg,
                                String.format(
                                        "There was an issue performing this command!\n" +
                                        "Please join the [supporto-guild](%s) and let the owner of the bot know " +
                                        "about this issue!\n" +
                                        "\n" +
                                        "Cause of error:\n" +
                                        "`%s`",
                                        LinkUtil.INVITE_GUILD,
                                        ex.getMessage()

                                )
                        );
                        Supporto.getLogger().error("Error in command", ex);
                    }
                }
        );
    }
}
