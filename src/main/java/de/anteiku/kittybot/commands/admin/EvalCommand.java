package de.anteiku.kittybot.commands.admin;

import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.objects.Config;
import net.dv8tion.jda.api.entities.ChannelType;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends ACommand{

	public static final String COMMAND = "eval";
	public static final String USAGE = "eval <code>";
	public static final String DESCRIPTION = "Evals some Java Code";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.ADMIN;
	private ScriptEngine engine;

	public EvalCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
		initEngine();
	}

	private void initEngine(){
		engine = new ScriptEngineManager().getEngineByName("nashorn");
		try{
			engine.eval("var imports = new JavaImporter(" + "java.io," + "java.lang," + "java.util," + "Packages.net.dv8tion.jda.api," + "Packages.net.dv8tion.jda.api.entities," + "Packages.net.dv8tion.jda.api.entities.impl," + "Packages.net.dv8tion.jda.api.managers," + "Packages.net.dv8tion.jda.api.managers.impl," + "Packages.net.dv8tion.jda.api.utils);");
		}
		catch(ScriptException e){
			LOG.error("Error while initializing script engine", e);
		}
	}

	@Override
	public void run(CommandContext ctx){
		if(Config.ADMIN_IDS.contains(ctx.getUser().getId())){
			try{
				engine.put("ctx", ctx);
				engine.put("message", ctx.getMessage());
				engine.put("channel", ctx.getChannel());
				engine.put("ctx.getArgs()", ctx.getArgs());
				engine.put("api", ctx.getJDA());
				if(ctx.getChannel().getType().equals(ChannelType.TEXT)){
					engine.put("guild", ctx.getGuild());
					engine.put("member", ctx.getMember());
				}

				Object out = engine.eval("(function() {" + "with (imports) {" + ctx.getMessage().getContentDisplay().substring(command.length() + 1) + "}" + "})();");
				sendAnswer(ctx, out == null ? "Executed without error." : out.toString());
			}
			catch(Exception e){
				sendError(ctx, e.getMessage());
			}
		}
		else{
			sendNoPermission(ctx);
		}
	}

}
