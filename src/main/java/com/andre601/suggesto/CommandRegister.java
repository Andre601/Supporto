package com.andre601.suggesto;

import com.andre601.suggesto.commands.CmdSettings;
import me.diax.comportment.jdacommand.Command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandRegister {

    private static final Set<Command> COMMANDS = new HashSet<>();

    CommandRegister(){
        register(
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
