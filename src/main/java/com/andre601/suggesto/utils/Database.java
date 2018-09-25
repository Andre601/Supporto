package com.andre601.suggesto.utils;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

import java.util.Map;

public class Database {

    private static final RethinkDB r = RethinkDB.r;

    private static Connection conn = r.connection()
            .hostname("localhost")
            .port(28015)
            .db("suggesto")
            .connect();

    private static String guildTable = "guilds";
    private static String ticketsTable = "tickets";
    private static String statsTable = "stats";

    public static void createDB(Guild g){
        r.table(guildTable).insert(
                r.array(
                        r.hashMap("id", g.getId())
                                .with("ticket_id", 1)
                                .with("ticket_channel", "none")
                                .with("ticket_category", "none")
                                .with("ticket_enabled", "yes")
                                .with("prefix", "t_")
                )
        ).optArg("conflict", "update").run(conn);
    }

    private static Map<String, Object> getGuild(Guild g){
        return r.table(guildTable).get(g.getId()).run(conn);
    }

    public static Long getTicketID(Guild guild){
        Map g = getGuild(guild);
        long id = Long.valueOf(g.get("ticket_id").toString());
        updateTicketID(guild, id);
        return id;
    }

    public static void saveTicket(Guild guild, String msgID, String ChannelID, String author){
        long id = getTicketID(guild);
        r.table(ticketsTable).insert(
                r.array(
                        r.hashMap("uuid", ChannelID)
                                .with("message_id", msgID)
                                .with("author_id", author)
                                .with("ticket_id", id)
                )
        ).optArg("conflict", "update").run(conn);
    }

    private static void updateTicketID(Guild guild, Long id){
        long ticketID = id;
        ticketID++;

        r.table(guildTable).get(guild).update(r.hashMap("ticket_id", ticketID)).run(conn);
    }

    public static String getPrefix(Guild g){
        Map guild = getGuild(g);
        return guild.get("prefix").toString();
    }

    public static void setPrefix(Guild guild, String prefix){
        r.table(guildTable).get(guild.getId()).update(r.hashMap("prefix", prefix)).run(conn);
    }

    public static boolean hasPrefix(Message msg, Guild guild){
        if(msg.getContentRaw().startsWith(getPrefix(guild)))
            return true;

        return msg.getContentRaw().startsWith(guild.getSelfMember().getAsMention());
    }

    public static String getSupportChannel(Guild g){
        Map guild = getGuild(g);
        return guild.get("ticket_channel").toString();
    }

    public static void setSupportChannel(Guild guild, String id){
        r.table(guildTable).get(guild.getId()).update(r.hashMap("ticket_channel", id)).run(conn);
    }

    public static String getCategory(Guild g){
        Map guild = getGuild(g);
        return guild.get("ticket_category").toString();
    }

    public static void setCategory(Guild guild, String id){
        r.table(guildTable).get(guild.getId()).update(r.hashMap("ticket_category", id)).run(conn);
    }

    public static boolean hasCategory(Guild g){
        Map guild = getGuild(g);
        return !guild.get("ticket_category").equals("none");
    }

    public static boolean ticketEbabled(Guild g){
        Map guild = getGuild(g);
        return guild.get("ticket_enabled").equals("yes");
    }

    public static boolean suggestionEbabled(Guild g){
        Map guild = getGuild(g);
        return guild.get("suggestion_enabled").equals("yes");
    }

    public static long getTotalTickets(){
        return r.table(statsTable).get("tickets").run(conn);
    }

    public static void updateTotalTickets(){
        long tickets = r.table(statsTable).get("tickets").run(conn);
        tickets++;

        r.table(statsTable).update(r.hashMap("tickets", tickets)).run(conn);
    }
}
