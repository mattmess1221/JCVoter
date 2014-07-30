package net.jamcraft.voter;

import net.minecraft.server.MinecraftServer;

public abstract class VoteAction {
	
	protected MinecraftServer server = MinecraftServer.getServer();

	abstract void action();
}
