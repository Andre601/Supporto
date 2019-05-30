[Wiki]: https://github.com/Andre601/Supporto/wiki
[Supporto-Discord]: https://discord.gg/W79Pbaw

# Supporto
Supporto is a simple Ticket-bot, with a small twist.  
The bot throws the system of writing a command to open a new ticket out of the window and replaces it with a channel-based ticket-creation.  
People just have to type a message in a defined channel to create a ticket.  
The creator, people with the permission `manage server` or a set staff-role (optional) can close the ticket, by simply clicking on the reaction of the first message and confirming the close.

Every participants in the ticket will receive a transcript of the chat in a DM (This can be disabled).

## NOTES
People with `manage server` permission or a registered staff-role assigned **Can NOT** create a ticket like normal.  
This was made to let people have a way, to make a message in their ticket-channel (e.g. for information).  
If you still want to create a ticket, add a `!create` to the message.

## Commands
The default prefix for commands is `t_`.
To know, what prefix is used in a guild, simply mention the bot.

`<arguments>` are required and `[arguments]` are optional.

### Bot
General commands to get info about the bot.

| Command: | Subcommand(s): | Description:                                                             |
| -------- | -------------- | ------------------------------------------------------------------------ |
| `Help`   |                | Shows all commands.                                                      |
|          | `[command]`    | Gives information about a specific command.                              |
| `Info`   |                | Get some info about the bot.                                             |
| `Invite` |                | Sends links to invite the bot, for the Discord or the GitHub-repository. |
| `Stats`  |                | Shows statistics of the bot like the total amount of created tickets.    |

### Guild
This commands are for either getting some guild-info, or for changing stuff.

| Command: | Subcommand(s): | Description:                                                              |
| -------- | -------------- | ------------------------------------------------------------------------- |
| `Guild`  |                | Gives some info about the Guild/Discord.                                  |
| `Roles`  |                | Lists all roles on the Discord. This requires `manage server` permission. |

### Tickets
This commands can only be run inside a ticket.  
Only exception is the `Settings` command.

All commands require you to have `manage server` permission!

| Command:   | Subcommand(s):              | Description:                                                              |
| ---------- | --------------------------- | --------------------------------------------------------- |
| `Add`      | `member <memberID>`         | Adds a member to the ticket.                              |
|            | `role <roleID>`             | Adds a role to the ticket.                                |
| `Remove`   | `member <memberID>`         | Removes a member from the ticket.                         |
|            | `role <roleID>`             | Removes a role from the ticket.                           |
| `Settings` |                             | Shows settings of the bot.                                |
|            | `category set <categoryID>` | Sets a category, where tickets will be created.           |
|            | `category reset`            | Resets the set category.                                  |
|            | `channel set <#channel>`    | Sets a text channel as a ticket channel.                  |
|            | `channel reset`             | Removes the set channel.                                  |
|            | `dm on`                     | Enables sending a transcript to all participants in DM.   |
|            | `dm off`                    | Disables sending of transcripts in DM.                    |
|            | `log set <#channel>`        | Sets a channel, where ticket-actions are logged.          |
|            | `log reset`                 | Resets the set channel.                                   |
|            | `role set <roleID>`         | Sets a role as a Staff-role which allows closing tickets. |
|            | `role reset`                | Resets the set role.                                      |

## For what is the staff-role?
The staff-role allows you to let people be able to close tickets of others without giving them potential harmful permissions.  

## What ticket-actions are logged?
Right now does the bot log the following actions:
- Creating a ticket
- Closing a ticket
- Adding a member to a ticket
- Removing a member from a ticket
- Adding a role to a ticket
- Removing a role from a ticket

Messages from the ticket itself aren't logged and never will be. Only time it is logged is during the ticket-closing when a transcript is send through DM.

## Other questions/Information
For more information and possible questions please visit the [Wiki] or join the [Supporto-Discord]
