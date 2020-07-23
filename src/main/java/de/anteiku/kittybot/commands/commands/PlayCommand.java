package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.commands.MusicPlayer;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class PlayCommand extends ACommand{

	private static final int VOLUME_STEP = 10;
	public static String COMMAND = "play";
	public static String USAGE = "play <playlist/song/video>";
	public static String DESCRIPTION = "Plays what you want him to play";
	protected static String[] ALIAS = {"p", "spiele"};

	public PlayCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendError(ctx, "Please provide a link or search term");
			return;
		}
		GuildVoiceState voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && voiceState.inVoiceChannel()){
			JdaLink link = KittyBot.lavalink.getLink(ctx.getGuild());
			link.connect(voiceState.getChannel());

			LavalinkPlayer player = link.getPlayer();
			MusicPlayer musicPlayer = new MusicPlayer(player);
			player.addListener(musicPlayer);
			Cache.addMusicPlayer(ctx.getGuild(), musicPlayer);
			musicPlayer.loadItem(this, ctx, ctx.getArgs());
		}
		else{
			sendError(ctx, "Please connect to a voice channel to play some stuff");
		}
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().isEmoji()){
			var musicPlayer = Cache.getMusicPlayer(event.getGuild());
			if(musicPlayer == null){
				return;
			}
			if(!musicPlayer.getRequesterId().equals(event.getUserId())){
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			}
			String emoji = event.getReactionEmote().getEmoji();
			if(emoji.equals(Emotes.FORWARD.get())){
				musicPlayer.nextTrack();
			}
			else if(emoji.equals(Emotes.BACK.get())){
				musicPlayer.lastTrack();
			}
			else if(emoji.equals(Emotes.SHUFFLE.get())){
				musicPlayer.shuffle();
			}
			else if(emoji.equals(Emotes.PLAY_PAUSE.get())){
				musicPlayer.pause();
			}
			else if(emoji.equals(Emotes.VOLUME_DOWN.get())){
				musicPlayer.changeVolume(-VOLUME_STEP);
				//ctx.getChannel().editMessageById(ctx.getMessageId(), PlayCommand.buildMusicControlMessage(musicPlayer).build()).queue();
			}
			else if(emoji.equals(Emotes.VOLUME_UP.get())){
				musicPlayer.changeVolume(VOLUME_STEP);
				//ctx.getChannel().editMessageById(ctx.getMessageId(), PlayCommand.buildMusicControlMessage(musicPlayer).build()).queue();
			}
			else if(emoji.equals(Emotes.X.get())){
				event.getChannel().deleteMessageById(event.getMessageId()).queue();// TODO deleting the message is bad :)
				Cache.destroyMusicPlayer(event.getGuild(), event.getMessageId());
			}
			musicPlayer.updateMusicControlMessage(event.getChannel());
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

}
