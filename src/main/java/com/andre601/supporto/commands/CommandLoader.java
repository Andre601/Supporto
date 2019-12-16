package com.andre601.supporto.commands;

import com.andre601.supporto.Supporto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader{
    private final Set<Command> COMMANDS = new HashSet<>();
    
    public CommandLoader(Supporto bot){
        
    }
    
    private void loadCommands(Command... commands){
        COMMANDS.addAll(Arrays.asList(commands));
    }
    
    public Set<Command> getCommands(){
        return COMMANDS;
    }
}
