package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class KissCommand extends Command{

	public static String COMMAND = "kiss";
	public static String USAGE = "kiss <@user>";
	public static String DESCRIPTION = "Sends a kiss to a user";
	public static String[] ALIAS = {"küss"};

	public KissCommand(KittyBot main){
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
					String url = API.getNeko("kiss");
					EmbedBuilder eb = new EmbedBuilder();
					eb.setColor(Color.pink);
					if(user.getId().equals(event.getAuthor().getId())){
						eb.setDescription(user.getAsMention() + " kissed **himself**!");
					}
					else{
						eb.setDescription(user.getAsMention() + " received a kiss from **" + API.getNameByUser(event.getMember()) + "**!");
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
