package com.andre601.suggesto.commands.server;

import com.andre601.suggesto.commands.Command;
import com.andre601.suggesto.utils.Database;
import com.andre601.suggesto.utils.EmbedUtil;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.MessageFormat;

public class CmdSettings implements Command {

    private static void showSettings(User user, Guild g){
        EmbedBuilder settings = EmbedUtil.getEmbed(user)
                .setTitle(MessageFormat.format(
                        "Settings: {0}",
                        g.getName()
                ))
                .setDescription(MessageFormat.format(
                        "Ticket-System: `{0}`\n" +
                                "Suggestion-System: `{1}`\n" +
                                "\n" +
                                "**Channels**:\n" +
                                "Tickets: `{2}`\n" +
                                "Suggestions: `{3}`\n" +
                                "\n" +
                                "Ticket-category: `{4}`",
                        ""
                ))
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent e) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent e) {

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent e) {

    }

    @Override
    public String help() {
        return "**Syntax**: `{0}settings [arguments ...]`\n" +
                "\n" +
                "**Arguments**:\n" +
                "```\n" +
                "set <ticket|ticket-cat|suggestion> <channelID>\n" +
                "   Sets the ticket-channel, ticket-category or suggestion-channel\n" +
                "remove [ticket|ticket-cat|suggestion]\n" +
                "   Removes the ticket-channel, ticket-category or suggestion-channel\n" +
                "   No argument: Remove all channels!\n" +
                "show\n" +
                "   Displays the current settings\n" +
                "enable [ticket|suggestion]\n" +
                "   Enables the ticket or suggestion-feature\n" +
                "   No argument: Enable both\n" +
                "disable [ticket|suggestion]\n" +
                "   Disables the ticket or suggestion-feature\n" +
                "   No argument: Disable both\n" +
                "```";
    }
}
