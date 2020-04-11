package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends ACommand{
	
	public static String COMMAND = "eval";
	public static String USAGE = "eval <code>";
	public static String DESCRIPTION = "Evals some Java Code";
	protected static String[] ALIAS = {};
	private ScriptEngine engine;
	
	public EvalCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
		initEngine();
	}
	
	private void initEngine(){
		engine = new ScriptEngineManager().getEngineByName("nashorn");
		try{
			engine.eval("var imports = new JavaImporter(" +
	            "java.io," +
	            "java.lang," +
	            "java.util," +
	            "Packages.net.dv8tion.jda.api," +
	            "Packages.net.dv8tion.jda.api.entities," +
	            "Packages.net.dv8tion.jda.api.entities.impl," +
	            "Packages.net.dv8tion.jda.api.managers," +
	            "Packages.net.dv8tion.jda.api.managers.impl," +
	            "Packages.net.dv8tion.jda.api.utils);");
		}
		catch (ScriptException e){
			Logger.error(e);
		}
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		
		if(event.getAuthor().getId().equals(main.ADMIN_DISCORD_ID)){
			try{
				engine.put("event", event);
				engine.put("message", event.getMessage());
				engine.put("channel", event.getChannel());
				engine.put("args", args);
				engine.put("api", event.getJDA());
				if (event.getChannel().getType().equals(ChannelType.TEXT))
				{
					engine.put("guild", event.getGuild());
					engine.put("member", event.getMember());
				}
				
				Object out = engine.eval(
			"(function() {" +
					"with (imports) {" +
					event.getMessage().getContentDisplay().substring(args[0].length()) +
					"}" +
					"})();");
				sendAnswer(event.getMessage(), out == null ? "Executed without error." : out.toString());
			}
			catch(Exception e){
				sendError(event, e.getMessage());
			}
		}
		else{
			sendNoPermission(event);
		}
	}
	
}