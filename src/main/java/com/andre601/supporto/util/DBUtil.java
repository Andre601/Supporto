package com.andre601.supporto.util;

import com.andre601.supporto.Supporto;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.util.Map;

public class DBUtil {

    private Supporto bot;

    private final RethinkDB r;
    private Connection connection;

    private String guildTable;
    private String ticketTable;

    public DBUtil(Supporto bot){
        r = RethinkDB.r;
        connection = r.connection()
                .hostname(bot.getgFile().getString("config", "db-ip"))
                .port(28015)
                .db(bot.getgFile().getString("config", "db-name"))
                .connect();

        guildTable  = bot.getgFile().getString("config", "db-guildTable");
        ticketTable = bot.getgFile().getString("config", "db-memberTable");
        this.bot = bot;
    }
    
    /*
     * Guild specific actions.
     */
    
    public void addGuild(String id){
        r.table(guildTable).insert(
                r.array(
                        r.hashMap("id", id)
                                .with("dm", "on")
                                .with("log_channel", "none")
                                .with("prefix", "t_")
                                .with("staff_role", "none")
                                .with("ticket_id", 1)
                                .with("ticket_channel", "none")
                                .with("ticket_category", "none")
                )
        ).optArg("conflict", "update").run(connection);
    }
    
    private void checkGuild(String id){
        Map<String, Object> guild = r.table(guildTable).get(id).run(connection);
        
        if(guild == null)
            addGuild(id);
    }
    
    private Map<String, Object> getGuild(String id){
        checkGuild(id);
        
        return r.table(guildTable).get(id).run(connection);
    }
    
    public boolean dmEnabled(String id){
        Map<String, Object> guild = getGuild(id);
        
        return guild.get("dm").toString().equals("on");
    }
    
    public String getLogChannel(String id){
        Map<String, Object> guild = getGuild(id);
        
        return guild.get("log_channel").toString();
    }
    
    public String getPrefix(String id){
        Map<String, Object> guild = getGuild(id);
        
        return guild.get("prefix").toString();
    }
    
    public String getStaffRole(String id){
        Map<String, Object> guild = getGuild(id);
        
        return guild.get("staff_role").toString();
    }
    
    public Long getTicketId(String id){
        Map<String, Object> guild = getGuild(id);
        
        return Long.valueOf(guild.get("ticket_id").toString());
    }
    
    public String getTicketChannel(String id){
        Map<String, Object> guild = getGuild(id);
        
        return guild.get("ticket_channel").toString();
    }
    
    public String getTicketCategory(String id){
        Map<String, Object> guild = getGuild(id);
        
        return guild.get("ticket_category").toString();
    }
    
    public void setDMEnabled(String id, boolean enabled){
        r.table(guildTable).get(id).update(r.hashMap("dm", enabled ? "on" : "off")).run(connection);
    }
    
    public void setLogChannel(String id, String channelId){
        r.table(guildTable).get(id).update(r.hashMap("log_channel", channelId)).run(connection);
    }
    
    public void setPrefix(String id, String prefix){
        r.table(guildTable).get(id).update(r.hashMap("prefix", prefix)).run(connection);
    }
    
    public void setStaffRole(String id, String roleId){
        r.table(guildTable).get(id).update(r.hashMap("staff_role", roleId)).run(connection);
    }
    
    public void setTicketCategory(String id, String categoryId){
        r.table(guildTable).get(id).update(r.hashMap("ticket_category", categoryId)).run(connection);
    }
    
    public void setTicketChannel(String id, String channelId){
        r.table(guildTable).get(id).update(r.hashMap("ticket_channel", channelId)).run(connection);
    }
    
    public void updateTicketId(String id){
        long ticketId = getTicketId(id);
        
        r.table(guildTable).get(id).update(r.hashMap("ticket_id", ticketId+1)).run(connection);
    }
    
    /*
     * Ticket-specific actions.
     */
    
    public Map<String, Object> getTicket(String id){
        return r.table(ticketTable).get(id).run(connection);
    }
    
    public String getAuthor(String id){
        Map<String, Object> ticket = getTicket(id);
        
        return ticket.get("author").toString();
    }
}
