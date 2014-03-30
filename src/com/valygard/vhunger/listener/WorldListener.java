/**
 * WorldListener.java is a part of the plugin vHunger.
 *
 * Copyright (C) 2013 Anand Kumar <http://dev.bukkit.org/bukkit-plugins/vhunger/>
 *
 * vHunger is a free software: You can redistribute it or modify it
 * under the terms of the GNU General Public License published by the Free
 * Software Foundation, either version 3 of the license of any later version.
 * 
 * vHunger is distributed in the intent of being useful. However, there
 * is NO WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You can view a copy of the GNU General Public License at 
 * <http://www.gnu.org/licenses/> if you have not received a copy.
 */
package com.valygard.vhunger.listener;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.valygard.vhunger.Hunger;
import com.valygard.vhunger.util.HungerUtils;

/**
 * @author Anand
 *
 */
public class WorldListener implements Listener
{
	private Hunger plugin;
	private HungerUtils utils;
	
	public WorldListener(Hunger plugin) {
		this.plugin = plugin;
		this.utils	= plugin.getUtils();
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		World world = p.getWorld();
		if (utils.isValidWorld(p)) {
			ConfigurationSection section = plugin.getConfig().getConfigurationSection(world.getName());
			if (section == null)
				return;
			
			p.setFoodLevel(section.getInt("hunger-on-respawn"));
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		World world = e.getFrom().getWorld();
		if (utils.isValidWorld(p)) {
			ConfigurationSection section = plugin.getConfig().getConfigurationSection(world.getName());
			if (section == null)
				return;
			
			int newHunger = p.getFoodLevel() - section.getInt("teleport-penalty");
			p.setFoodLevel(newHunger < 0 ? 0 : newHunger);
		}
	}
	
	public Hunger getPlugin() {
		return plugin;
	}
	
	public HungerUtils getUtils() {
		return utils;
	}
}
