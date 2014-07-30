package net.jamcraft.voter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class KickAction extends VoteAction {

	private EntityPlayerMP playerToBeKicked;
	
	public KickAction(EntityPlayerMP player){
		this.playerToBeKicked = player;
	}
	
	@Override
	public void action() {
		playerToBeKicked.playerNetServerHandler.kickPlayerFromServer("You were voted to be kicked.");
	}

}
