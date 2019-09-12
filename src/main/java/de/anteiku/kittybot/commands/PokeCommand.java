package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class PokeCommand extends Command{
	
	public static String COMMAND = "poke";
	public static String USAGE = "poke <@user>";
	public static String DESCRIPTION = "Pokes a user";
	public static String[] ALIAS = {"stups"};
	
	public PokeCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length != 1){
			sendUsage(event.getChannel());
			return;
		}
		String id = API.getIdByMention(args[0]);
		if(id != null){
			User user = event.getJDA().getUserById(id);
			if(user != null){
				try{
					String url = API.getNeko("poke");
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.pink);
					if(user.getId().equals(event.getAuthor().getId())){
						eb.setDescription(user.getAsMention() + " poked **himself**!");
					}
					else{
						eb.setDescription(API.getNameByUser(event.getMember()) + " poked **" + user.getAsMention() + "**!");
					}
					eb.setImage(url);
					event.getChannel().sendMessage(eb.build()).queue();
				}
				catch(Exception e){
					sendError(event.getChannel(), "That's a weird error, oops!");
				}
			}
			else{
				sendError(event.getChannel(), "User not found!");
			}
		}
		else{
			sendError(event.getChannel(), "You need to mention a User!");
		}
	}
	
}