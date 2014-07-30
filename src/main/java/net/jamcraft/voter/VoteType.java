package net.jamcraft.voter;

import net.minecraft.entity.player.EntityPlayer;

public enum VoteType {
	STOP("stop the server.", 0, Settings.canStop), KICK("kick %s.", 1, Settings.canKick);
	
	private String description;
	private int arguments;
	private boolean enabled;
	
	private VoteType(String s, int i, boolean b){
		description = s;
		arguments = i;
		enabled = b;
	}
	
	public static VoteType parse(String s){
		for(VoteType vote : values()){
			if(vote.enabled && vote.toString().equalsIgnoreCase(s))
				return vote;
		}
		throw new IllegalArgumentException("Unknown type '" + s + "'");
	}
	
	public int getNumberOfArguments(){
		return arguments + 2;
	}

	public String getChat(EntityPlayer starter, Nominee nominee) {
		// TODO Auto-generated method stub
		return starter.getDisplayName() + " has started a vote to " + String.format(description, nominee.getTarget());
	}
}
