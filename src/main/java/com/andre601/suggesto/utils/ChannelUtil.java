package com.andre601.suggesto.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelUtil {

    public static void createTicket(Guild g, User user, Message msg){
        TextChannel tc = (TextChannel)g.getController().createTextChannel("ticket-" + Database.getTicketID(g))
                .complete();
        if(Database.hasCategory(g)){
            Category category = g.getCategoryById(Database.getCategory(g));
            tc.getManager().setParent(category).queue();
        }

        g.getTextChannelById(tc.getId()).createPermissionOverride(g.getMember(user)).setAllow(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ATTACH_FILES,
                Permission.MESSAGE_EMBED_LINKS
        ).queue();

        Database.saveTicket(g, msg.getId(), user.getId());
    }

    public static void createSuggestion(Guild g, User author, Message msg){
        if(CheckUtil.checkMsg(msg)){
            String raw = msg.getContentRaw();
            String title = "";
            String description = "";
            String link = "";

            Pattern p = Pattern.compile("([\\[][\\w]+[]])(.+)");
            Matcher m = p.matcher(raw);

            while (m.find()){
                String bracket = m.group(1);
                String user = m.group(2);

                switch (bracket.toLowerCase()){
                    case "[title]":
                        title = user.trim();
                        break;

                    case "[description]":
                        description = user.trim();
                        break;

                    case "[link]":
                        link = user.trim();
                        break;
                }
            }

            if(!link.equalsIgnoreCase("none") || !link.startsWith("http://") || !link.startsWith("https://")){
                return;
            }

            MessageEmbed.Field linkField = new MessageEmbed.Field("Link:",
                    (link.equalsIgnoreCase("none") ?
                            "`none`" :
                            "[`" + title + "`](" + link + ")"
                    ), true);

            EmbedBuilder suggestion = EmbedUtil.getEmbed(author)
                    .setTitle(title + Database.getSuggestionID(g))
                    .addField("Description:", description, false)
                    .addField(linkField)
                    .addField("Likes/Dislikes:", "``", true);

        }
    }
}
