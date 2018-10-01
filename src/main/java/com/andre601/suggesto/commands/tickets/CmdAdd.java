package com.andre601.suggesto.commands.tickets;

import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import com.andre601.suggesto.utils.PermUtil;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.text.MessageFormat;

@CommandDescription(
        name = "Add",
        description =
                "Adds a user or role to a ticket.\n" +
                "Can only be run in a ticket!\n" +
                "\n" +
                "`member <memberID>` Adds a member to the ticket.\n" +
                "`role <roleID>` Adds a role to the ticket",
        triggers = {"add"},
        attributes = {@CommandAttribute(key = "tickets")}
)
public class CmdAdd implements Command {

    private Member getMember(Guild guild, String id){
        Member member;
        try{
            member = guild.getMemberById(id);
        }catch(Exception ex){
            member = null;
        }

        return member;
    }

    private Role getRole(Guild guild, String id){
        Role role;
        try{
            role = guild.getRoleById(id);
        }catch(Exception ex){
            role = null;
        }

        return role;
    }

    @Override
    public void execute(Message msg, String s) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();

        if(!Database.hasTicket(tc.getId())){
            EmbedUtil.sendError(msg, "Please run this command inside a ticket!");
            return;
        }

        String[] args = s.split(" ");
        if(args.length <= 1){
            EmbedUtil.sendError(msg, MessageFormat.format(
                    "You need to provide more arguments!\n" +
                    "Usage: `{0}add <member <ID>|role <ID>>`",
                    Database.getPrefix(guild)
            ));
            return;
        }

        switch(args[0].toLowerCase()){
            case "member":
                Member member = getMember(guild, args[1]);
                if(member == null){
                    EmbedUtil.sendError(msg,
                            "The provided ID was invalid!\n" +
                            "Make sure you have the right ID."
                    );
                    return;
                }
                if(tc.getPermissionOverride(member) != null){
                    EmbedUtil.sendError(msg, MessageFormat.format(
                            "The member {0} is already added!",
                            member.getAsMention()
                    ));
                    return;
                }

                tc.createPermissionOverride(member).setAllow(
                        Permission.VIEW_CHANNEL,
                        Permission.MESSAGE_WRITE,
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_EMBED_LINKS,
                        Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_ADD_REACTION,
                        Permission.MESSAGE_EXT_EMOJI
                ).queue();

                EmbedBuilder memberSuccess = EmbedUtil.getEmbed(msg.getAuthor())
                        .setColor(Color.GREEN)
                        .setDescription(MessageFormat.format(
                                "Successfully added the member {0}!",
                                member.getAsMention()
                        ));
                tc.sendMessage(memberSuccess.build()).queue();
                break;

            case "role":
                Role staff = getRole(guild, Database.getRoleID(guild));
                if(!PermUtil.isAdmin(tc, msg.getMember())){
                    if(staff == null){
                        EmbedUtil.sendError(msg, "You aren't allowed to add a role!");
                        return;
                    }
                    if(!PermUtil.isStaff(msg.getMember(), staff)){
                        EmbedUtil.sendError(msg, "You aren't allowed to add a role!");
                        return;
                    }
                }

                Role role = getRole(guild, args[1]);
                if(role == null){
                    EmbedUtil.sendError(msg, MessageFormat.format(
                            "The provided roleID was invalid!\n" +
                            "Make sure, that the provided id is correct.\n" +
                            "You can use `{0}roles` to get a list of roles and their ids.",
                            Database.getPrefix(guild)
                    ));
                    return;
                }
                if(tc.getPermissionOverride(role) != null){
                    EmbedUtil.sendError(msg, "This role is already added!");
                    return;
                }
                try {
                    if (role.isPublicRole()) {
                        tc.putPermissionOverride(role).setAllow(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_READ,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES,
                                Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EXT_EMOJI
                        ).queue();
                    } else {
                        tc.createPermissionOverride(role).setAllow(
                                Permission.VIEW_CHANNEL,
                                Permission.MESSAGE_WRITE,
                                Permission.MESSAGE_READ,
                                Permission.MESSAGE_HISTORY,
                                Permission.MESSAGE_EMBED_LINKS,
                                Permission.MESSAGE_ATTACH_FILES,
                                Permission.MESSAGE_ADD_REACTION,
                                Permission.MESSAGE_EXT_EMOJI
                        ).queue();
                    }
                }catch(Exception ex){
                    EmbedUtil.sendError(msg, "Couldn't add/override the role! Is it higher than my own?");
                    return;
                }

                EmbedBuilder roleSuccess = EmbedUtil.getEmbed(msg.getAuthor())
                        .setColor(Color.GREEN)
                        .setDescription(MessageFormat.format(
                                "Successfully added the role {0}!",
                                role.getAsMention()
                        ));
                tc.sendMessage(roleSuccess.build()).queue();
                break;

            default:
                EmbedUtil.sendError(msg, MessageFormat.format(
                        "You need to provide valid arguments!\n" +
                                "Usage: `{0}add <member <ID>|role <ID>>`",
                        Database.getPrefix(guild)
                ));
        }
    }
}
