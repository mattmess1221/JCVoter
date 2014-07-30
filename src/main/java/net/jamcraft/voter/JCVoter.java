package net.jamcraft.voter;

import java.util.Calendar;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.ServerChatEvent;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(version = "1.0", modid = "jcvoter", name = "JamCraft Voting Mod")
public class JCVoter {
	
	private Nominee currentVote;
	private Map<EntityPlayer, Long> playerVoteTimes = Maps.newHashMap();
	@Instance
	public static JCVoter INSTANCE;
	
	@EventHandler
	public void pre(FMLPreInitializationEvent event){
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		Settings.readSettings(config);
	}
	
	@EventHandler
	public void start(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void voteStart(ServerChatEvent event){
		String message = event.message;
		EntityPlayer player = event.player;
		if(!message.startsWith("!"))
			return;
		if(message.length() < 5 || !message.substring(1,5).equalsIgnoreCase("vote"))
			return;
		event.setCanceled(true);
		String[] vote = message.split(" ");
		if(vote.length <= 1)
			throw new IllegalArgumentException("Enter something to vote for.  Add yes to vote yes, and no to vote no.");
		if(currentVote != null){
			String value = vote[1];
			try{
				currentVote.addVote(player, parseVote(value));
			} catch(IllegalArgumentException e){
				this.sendErrorChat(player, "There is already a vote in progress. Vote yes or no.");
			}
			return;
		}
		if(playerVoteTimes.containsKey(player) && (getCurrentTime() - playerVoteTimes.get(player))/1000 < Settings.waitTime){
			int sec = (int)(getCurrentTime() - playerVoteTimes.get(player))/1000;
			this.sendErrorChat(player, "You recently started a vote. Please wait " + sec + " more second" + (sec == 0 ? "" : "s") + ".");
			return;
		}
		try {
			currentVote = new Nominee(player, message.split(" "));
			currentVote.startVote(player);
			playerVoteTimes.put(player, Calendar.getInstance().getTimeInMillis());
		} catch(IllegalArgumentException e){
			this.sendErrorChat(player, e.getMessage());
		}
	}
	
	public void sendChat(EntityPlayer player, String message){
		IChatComponent chat = new ChatComponentText(message);
		player.addChatMessage(chat);
	}
	
	public void sendErrorChat(EntityPlayer player, String message) {
		IChatComponent chat = new ChatComponentText(message);
		chat.getChatStyle().setColor(EnumChatFormatting.RED);
		player.addChatMessage(chat);
	}
	
	public void sendChatAllPlayers(String message){
		IChatComponent chat = new ChatComponentText(message);
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(chat);
	}
	
	private boolean parseVote(String vote){
		if(vote.equalsIgnoreCase("yes"))
			return true;
		else if(vote.equalsIgnoreCase("no"))
			return false;
		else
			throw new IllegalArgumentException("Unknown value: " + vote);
	}
	
	public long getCurrentTime(){
		return Calendar.getInstance().getTimeInMillis();
	}

	public void resetVote(){
		this.currentVote = null;
	}
}
