package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class TestCommand extends Command{
	
	public static String COMMAND = "test";
	public static String USAGE = "test";
	public static String DESCRIPTION = "Only for testing weird stuff";
	public static String[] ALIAS = new String[]{};
	
	public TestCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
	}
	
}