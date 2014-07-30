package net.jamcraft.voter;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class StopAction extends VoteAction {

	@Override
	public void action() {
		MinecraftServer.getServer().stopServer();
	}

}
