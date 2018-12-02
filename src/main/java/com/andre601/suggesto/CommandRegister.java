package com.andre601.suggesto;

import com.andre601.suggesto.commands.bot.CmdHelp;
import com.andre601.suggesto.commands.bot.CmdInfo;
import com.andre601.suggesto.commands.bot.CmdInvite;
import com.andre601.suggesto.commands.bot.CmdStats;
import com.andre601.suggesto.commands.guild.CmdGuild;
import com.andre601.suggesto.commands.guild.CmdRoles;
import com.andre601.suggesto.commands.tickets.CmdSettings;
import com.andre601.suggesto.commands.owner.CmdLeave;
import com.andre601.suggesto.commands.owner.CmdShutdown;
import com.andre601.suggesto.commands.tickets.CmdAdd;
import com.andre601.suggesto.commands.tickets.CmdRemove;
import com.github.rainestormee.jdacommand.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandRegister {

    private static final Set<Command> COMMANDS = new HashSet<>();

    CommandRegister(){
        register(
                // Bot
                new CmdHelp(),
                new CmdInfo(),
                new CmdInvite(),
                new CmdStats(),
                // Guild
                new CmdGuild(),
                new CmdRoles(),
                // Owner
                new CmdLeave(),
                new CmdShutdown(),
                // Tickets
                new CmdAdd(),
                new CmdRemove(),
                new CmdSettings()
        );
    }

    private void register(Command... cmds) {
        COMMANDS.addAll(Arrays.asList(cmds));
    }

    Set<Command> getCommands(){
        return COMMANDS;
    }
}
