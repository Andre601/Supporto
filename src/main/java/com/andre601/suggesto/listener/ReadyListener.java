package com.andre601.suggesto.listener;

import com.andre601.suggesto.Supporto;
import com.andre601.suggesto.utils.Database;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.text.MessageFormat;

public class ReadyListener extends ListenerAdapter {

    private int shardCount = 0;
    private static JDA jda;
    private static ShardManager shardManager;
    private static boolean ready = Boolean.FALSE;

    public static boolean getReady(){
        return ready;
    }

    private static void setReady(Boolean ready){
        ReadyListener.ready = ready;
    }

    public static JDA getJda(){
        return jda;
    }

    public static void setJda(JDA jda){
        ReadyListener.jda = jda;
    }

    public static ShardManager getShards(){
        return shardManager;
    }

    public static void setShards(ShardManager shardManager){
        ReadyListener.shardManager = shardManager;
    }

    public void onReady(ReadyEvent event){
        shardCount += 1;
        JDA jda = event.getJDA();

        setShards(jda.asBot().getShardManager());
        setJda(jda);

        for(Guild guild : jda.getGuilds()){
            if(!Database.hasGuild(guild)){
                Database.createDB(guild);
            }
        }

        if(shardCount == jda.getShardInfo().getShardTotal()){
            setReady(Boolean.TRUE);
            jda.asBot().getShardManager().setStatus(OnlineStatus.ONLINE);
            jda.asBot().getShardManager().setGame(Game.watching(MessageFormat.format(
                    "{0} created tickets | {1} guilds",
                    Database.getTotalTickets(),
                    jda.asBot().getShardManager().getGuildCache().size()
            )));
            Supporto.getLogger().info(MessageFormat.format(
                    "Enabled Bot {0} ({1}) on {2} guild(s) with {3} shard(s)!",
                    jda.getSelfUser().getName(),
                    jda.getSelfUser().getId(),
                    jda.asBot().getShardManager().getGuildCache().size(),
                    jda.asBot().getShardManager().getShardCache().size()
            ));
        }
    }
}
