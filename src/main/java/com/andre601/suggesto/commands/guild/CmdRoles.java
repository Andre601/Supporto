package com.andre601.suggesto.commands.guild;

import com.andre601.suggesto.Supporto;
import com.github.rainestormee.jdacommand.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.entities.*;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "Roles",
        description = "Shows all available roles",
        triggers = {"role", "roles", "listroles"},
        attributes = {
                @CommandAttribute(key = "staff+"),
                @CommandAttribute(key = "guild")
        }
)
public class CmdRoles implements Command {

    private Paginator.Builder builder;

    private String getRoleName(Role role){
        return String.format("%-20s %s", role.getName(), role.getId());
    }

    private void addPage(String msg){
        builder.addItems(MessageFormat.format(
                "Use the arrows to navigate through the list (if there are multiple pages :P)\n" +
                "\n" +
                "**Roles**:\n" +
                "```yaml\n" +
                "Name:                ID:\n" +
                "\n" +
                "{0}" +
                "```",
                msg
        ));
    }

    @Override
    public void execute(Message msg, String s) {
        Guild guild = msg.getGuild();
        TextChannel tc = msg.getTextChannel();
        builder = new Paginator.Builder().setEventWaiter(Supporto.waiter).setTimeout(1, TimeUnit.MINUTES);

        StringBuilder sb = new StringBuilder();
        for(Role role : guild.getRoles()){
            if(role.isPublicRole())
                continue;
            if((sb.length() + getRoleName(role).length()) > MessageEmbed.VALUE_MAX_LENGTH){
                addPage(sb.toString());
                sb = new StringBuilder();
            }
            sb.append(getRoleName(role)).append("\n");
        }
        if(sb.length() > 0)
            addPage(sb.toString());

        builder.setText("").setItemsPerPage(1).waitOnSinglePage(true).setFinalAction(message -> {
            if(message != null){
                message.delete().queue();
            }
        });

        builder.build().display(tc);
    }
}
