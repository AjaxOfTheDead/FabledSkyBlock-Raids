package me.ResurrectAjax.Commands.Managers;

import org.bukkit.entity.Player;

public abstract class CommandInterface {
	public abstract String getName();
	
	public abstract void perform(Player player);
}
