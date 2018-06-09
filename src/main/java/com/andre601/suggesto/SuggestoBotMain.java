package com.andre601.suggesto;

import com.andre601.suggesto.listener.CommandListener;
import com.andre601.suggesto.utils.ConfigUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;
import java.io.File;

public class SuggestoBotMain {

    private static JDABuilder builder = new JDABuilder(AccountType.BOT);
    private static JDA jda;

    public static void main(String[] args){
        File c = new File("config.cfg");
        ConfigUtil config = new ConfigUtil();
        if(!c.exists()){
            config.create();
        }
        builder.setToken(config.getProperty("token"))
                .setAutoReconnect(true)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setGame(Game.playing("Starting up..."));

        addListeners();

        try{
            jda = builder.buildBlocking();
        }catch (LoginException|InterruptedException ex){
            System.out.println("[WARN] Issue while starting up bot:");
            ex.printStackTrace();
        }
    }

    private static void addListeners(){
        builder.addEventListener(new CommandListener());
    }
}
