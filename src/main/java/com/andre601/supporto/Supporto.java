package com.andre601.supporto;

import ch.qos.logback.classic.Logger;
import com.andre601.supporto.commands.CommandLoader;
import com.andre601.supporto.tickets.TicketManager;
import com.andre601.supporto.util.DBUtil;
import com.andre601.supporto.util.EmbedUtil;
import com.andre601.supporto.util.LogUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class Supporto {

    private Logger logger = (Logger)LoggerFactory.getLogger(Supporto.class);

    private boolean beta;
    private ShardManager shardManager;

    private GFile gFile;
    private DBUtil dbUtil;
    private EmbedUtil embedUtil;
    private LogUtil logUtil;
    private TicketManager ticketManager;
    
    Cache<String, String> prefix = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    
    Cache<String, String> category = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    Cache<String, String> ticketChannel = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    Cache<String, String> staffRole = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    Cache<String, Boolean> dmEnabled = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    
    Cache<String, String> ticket = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    
    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();

    public static void main(String... args){
        try{
            new Supporto().startBot();
        }catch(LoginException ex){
            new Supporto().logger.warn("Couldn't login to Discord!", ex);
        }
    }

    private void startBot() throws LoginException{
        gFile = new GFile();
        dbUtil = new DBUtil(this);
        embedUtil = new EmbedUtil();
        logUtil = new LogUtil(this);
        ticketManager = new TicketManager(this);

        getgFile().createOrLoad("config", "/config.json", "./config.json");

        beta = getgFile().getString("config", "beta").equalsIgnoreCase("true");

        CMD_HANDLER.registerCommands(new HashSet<>(new CommandLoader(this).getCommands()));
        
        shardManager = new DefaultShardManagerBuilder()
                .setToken(getgFile().getString("config", "bot-token"))
                .addEventListeners(

                )
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Starting bot..."))
                .setShardsTotal(-1)
                .build();
    }

    public boolean isBeta(){
        return beta;
    }

    public GFile getgFile(){
        return gFile;
    }
    public DBUtil getDbUtil(){
        return dbUtil;
    }
    public EmbedUtil getEmbedUtil(){
        return embedUtil;
    }
    public LogUtil getLogUtil(){
        return logUtil;
    }
    public TicketManager getTicketManager(){
        return ticketManager;
    }
    
    public ShardManager getShardManager(){
        return shardManager;
    }
    
    public String getPrefix(String id){
        return prefix.get(id, k -> getDbUtil().getPrefix(id));
    }
    
    public String getCategory(String id){
        return category.get(id, k -> getDbUtil().getTicketCategory(id));
    }
    public String getTicketChannel(String id){
        return ticketChannel.get(id, k -> getDbUtil().getTicketChannel(id));
    }
    public String getStaffRole(String id){
        return staffRole.get(id, k -> getDbUtil().getStaffRole(id));
    }
    public Boolean isDmEnabled(String id){
        return dmEnabled.get(id, k -> getDbUtil().dmEnabled(id));
    }
    
    public String getAuthor(String id){
        return ticket.get(id, k -> getDbUtil().getAuthor(id));
    }
    public String getTicket(String id){
        return ticket.getIfPresent(id);
    }
    
    public CommandHandler<Message> getCommandHandler(){
        return CMD_HANDLER;
    }
}
