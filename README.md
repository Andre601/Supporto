# Supporto
Supporto is a simple Ticket-bot, with a small twist.  
Instead of you having to type something like `/new <topic>` to create a ticket, Supporto simply lets you set a channel as a ticket-channel, where people can just type a message and the bot will make a new ticket.  
The creator, people with the permission `manage server` or a set staff-role can then close the ticket, by simply clicking on the reaction of the first message and confirming the close.

Every participient in the ticket will receive a transcript of the chat in a DM.

## NOTES
People with `manage server` permission or a registered staff-role assigned **Can NOT** create a ticket like normal.  
This was made to let people have a way, to make a message in their ticket-channel (e.g. for information).  
If you still want to create a ticket, add a `-test` to the message.

## Commands
The default prefix for commands is `t_`.
To know, what prefix is used in a guild, simply mention the bot.

### Bot
General commands to get info about the bot.

#### Help
**Aliases**:
- Commands
- Command

Shows you all available commands.  
You can provide a command after `Help` (f.e. `t_help settings`) to get info about the command.

#### Info
**Alias**:
- Information

Gives basic info for the bot.

#### Invite
**Aliases**:
- Link
- Links

Gives you the links, to invite the bot, join the guild, or to [the GitHub-repository](https://github.com/Andre601/Supporto).

#### Stats
**Aliases**:
- Stat
- Statistics

Gives you some statistics about the bot.

### Guild
This commands are for either getting some guild-info, or for changing stuff.

#### Guild
**Alias**:
- Server

Shows general info about the guild.

#### Roles
**Alias**:
- Role
- Listroles

Shows you a list of roles and their IDs in the guild.
Requires you to have `manage server` permission.

#### Settings
**Alias**:
- Options

Lets you change different settings of the bot.  
This command requires you to have the `manage server` permission!

**Subcommands**:
- `prefix <set <prefix>|reset>` Set or reset the prefix.
- `channel <set <#channel>|reset>` Set or reset a channel as ticket-channel.
- `category <set <categoryID>|reset>` Set or reset a category for creating tickets in it.
- `role <set <roleID>|reset>` Set or reset a role, that should be able to close tickets.

### Tickets
This commands can only be run inside a ticket.

#### Add
Lets you add a member or role to the ticket.

**Subcommands**:
- `member <memberID>` adds a member, if the ID is valid.
- `role <roleID>` adds a role, if the ID is valid.

#### Remove
Lets you remove a member or role from the ticket.

**Subcommands**:
- `member <memberID>` removes a member, if the ID is valid.
- `role <roleID>` removes a role, if the ID is valid.