package net.jamcraft.voter;

import java.util.Calendar;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.google.common.collect.Lists;

public class Nominee {
	
	public final VoteType type;
	
	private long startTime;
	public String[] args;
	private int votes;
	private int total;
	private List<EntityPlayer> votedPlayers = Lists.newArrayList();
	private Thread thread;
	
	public Nominee(EntityPlayer player, String...vote){
		this.args = vote;
		this.type = VoteType.parse(vote[1]);
		if(!areArgsValid(type, args))
			throw new IllegalArgumentException("Wrong number of arguments.");
		thread = new Thread(){
			boolean active = true;
			@Override
			public void run(){
				while(active){
					total = MinecraftServer.getServer().getCurrentPlayerCount();
					int secs = (int) (JCVoter.INSTANCE.getCurrentTime() - startTime)/1000;
					if(secs >= Settings.voteTime)
						active = false;
				}
				endVote();
			}
		};
	}
	
	private boolean areArgsValid(VoteType type2, String[] args2) {
		return args2.length == type2.getNumberOfArguments();
	}

	public void startVote(EntityPlayer player){
		JCVoter.INSTANCE.sendChatAllPlayers(type.getChat(player, this));
		this.startTime = Calendar.getInstance().getTimeInMillis();
		addVote(player, true);
		thread.start();
	}
	
	public void addVote(EntityPlayer player, boolean vote){
		if(votedPlayers.contains(player)){
			JCVoter.INSTANCE.sendErrorChat(player, "You have already voted.");
			return;
		}
		votedPlayers.add(player);
		if(vote)
			votes++;
		JCVoter.INSTANCE.sendChat(player, "Your vote has been cast.");
	}
	
	public void endVote(){
		thread.interrupt();
		JCVoter.INSTANCE.resetVote();
		boolean passed = getPercentage() >= Settings.minVote;
		if(passed){
			JCVoter.INSTANCE.sendChatAllPlayers("Vote passed!");
			getAction().action();
		} else {
			JCVoter.INSTANCE.sendChatAllPlayers("Vote failed.");
		}
	}
	
	private VoteAction getAction(){
		switch(type){
		case STOP:
			return new StopAction();
		case KICK:
			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.args[2]);
			return new KickAction(player);
		}
		return null;
	}
	
	public float getPercentage(){
		if(total == 0)
			return 0F;
		return (float)votes / (float)total;
	}
	
	public String getTarget(){
		if(args.length < 3)
			return null;
		return args[2];
	}

}
