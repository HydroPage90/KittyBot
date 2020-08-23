package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Config;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OnReadyEvent extends ListenerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(OnReadyEvent.class);

	@Override
	public final void onReady(@NotNull ReadyEvent event){
		if(Config.DISCORD_BOTS_TOKEN == null){
			var shardInfo = event.getJDA().getShardInfo();
			Request request = new Request.Builder().url("https://discord.bots.gg/api/v1/bots/" + Config.BOT_ID + "/stats").header("authorization", Config.DISCORD_BOTS_TOKEN).post(
					RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
							DataObject.empty().put("shardCount", shardInfo.getShardTotal()).put("shardId", shardInfo.getShardId()).put("guildCount",
									event.getGuildTotalCount()
							).toString()
					)).build();
			KittyBot.getHttpClient().newCall(request).enqueue(new Callback(){
				@Override
				public void onFailure(@NotNull Call call, @NotNull IOException e){
					LOG.error("Error while publishing bot stats to https://discord.bots.gg/", e);
				}

				@Override
				public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
					if(response.code() == 200){
						LOG.info("Published serverCount to https://discord.bots.gg/");
						return;
					}
					try(var body = response.body()){
						LOG.error("Error while publishing bot stats to https://discord.bots.gg/ code: '" + response.code() + "'" + (body == null ? "" : "body: '" + body.string() + "'"));
					}
				}
			});
		}
		if(Config.DISCORD_BOT_LIST_TOKEN == null){
			var shardInfo = event.getJDA().getShardInfo();
			KittyBot.getDiscordBotListAPI().setStats(shardInfo.getShardId(), shardInfo.getShardTotal(), event.getGuildTotalCount());

		}
	}

}
