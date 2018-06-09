package com.andre601.suggesto.utils;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import net.dv8tion.jda.core.entities.Guild;

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
    private static String suggestionTable = "suggestions";

    public static void createDB(Guild g){
        r.table(guildTable).insert(
                r.array(
                        r.hashMap("id", g.getId())
                                .with("ticket_id", 1)
                                .with("suggestion_id", 1)
                                .with("ticket_channel", "none")
                                .with("ticket_category", "none")
                                .with("suggestion_channel", "none")
                                .with("prefix", "t_")
                )
        ).optArg("conflict", "update").run(conn);
    }

    private static Map<String, Object> getGuild(Guild g){
        return r.table(guildTable).get(g.getId()).run(conn);
    }

    public static Integer getTicketID(Guild g){
        Map guild = getGuild(g);
        String id = guild.get("ticket_id").toString();
        updateTicketID(g);
        return Integer.valueOf(id);
    }

    public static Integer getSuggestionID(Guild g){
        Map guild = getGuild(g);
        String id = guild.get("suggestion_id").toString();
        updateSuggestionID(g);
        return Integer.valueOf(id);
    }

    public static void saveTicket(Guild g, String msgID, String author){
        r.table(ticketsTable).insert(
                r.array(
                        r.hashMap("uuid", g.getId() + "-" + getTicketID(g))
                                .with("message_id", msgID)
                                .with("author_id", author)
                )
        ).optArg("conflict", "update").run(conn);
        updateTicketID(g);
    }

    private static void updateTicketID(Guild g){
        int ticketID = getTicketID(g);
        ticketID++;

        r.table(guildTable).update(r.hashMap("ticket_id", String.valueOf(ticketID))).run(conn);
    }

    private static void updateSuggestionID(Guild g){
        int suggestionID = getSuggestionID(g);
        suggestionID++;

        r.table(guildTable).update(r.hashMap("suggestion_id", String.valueOf(suggestionID))).run(conn);
    }

    public static String getPrefix(Guild g){
        Map guild = getGuild(g);
        return guild.get("prefix").toString();
    }

    public static String getSupportChannel(Guild g){
        Map guild = getGuild(g);
        return guild.get("ticket_channel").toString();
    }

    public static String getSuggestionChannel(Guild g){
        Map guild = getGuild(g);
        return guild.get("suggestion_channel").toString();
    }

    public static boolean hasCategory(Guild g){
        Map guild = getGuild(g);
        return !guild.get("ticket_category").equals("none");
    }

    public static String getCategory(Guild g){
        Map guild = getGuild(g);
        return guild.get("ticket_category").toString();
    }
}
