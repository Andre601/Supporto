package com.andre601.suggesto;

import com.andre601.suggesto.commands.bot.CmdHelp;
import com.andre601.suggesto.commands.bot.CmdInvite;
import com.andre601.suggesto.commands.bot.CmdStats;
import com.andre601.suggesto.commands.guild.CmdGuild;
import com.andre601.suggesto.commands.guild.CmdSettings;
import com.andre601.suggesto.commands.owner.CmdShutdown;
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
                new CmdInvite(),
                new CmdStats(),
                // Guild
                new CmdGuild(),
                new CmdSettings(),
                // Owner
                new CmdShutdown()
        );
    }

    private void register(Command... cmds) {
        COMMANDS.addAll(Arrays.asList(cmds));
    }

    Set<Command> getCommands(){
        return COMMANDS;
    }
}
