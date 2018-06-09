package com.andre601.suggesto.commands.commandUtils;

import com.andre601.suggesto.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    public static final CommandParser parser = new CommandParser();
    public static Map<String, Command> commands = new HashMap<>();

    public static void handleCommand(CommandParser.commandContainer cmd){
        if(commands.containsKey(cmd.invoke)){
            boolean safe = commands.get(cmd.invoke).called(cmd.args, cmd.event);

            if(!safe){
                commands.get(cmd.invoke).action(cmd.args, cmd.event);
                commands.get(cmd.invoke).executed(safe, cmd.event);
            }else{
                commands.get(cmd.invoke).executed(safe, cmd.event);
            }
        }
    }
}
