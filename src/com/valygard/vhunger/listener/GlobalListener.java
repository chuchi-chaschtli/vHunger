/**
 * GlobalListener.java is a part of the plugin vHunger.
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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.valygard.vhunger.Hunger;
import com.valygard.vhunger.event.HungerChangeEvent;
import com.valygard.vhunger.event.ScaleHungerEvent;
import com.valygard.vhunger.util.HungerUtils;

/**
 * @author Anand
 *
 */
public class GlobalListener implements Listener {
	private Hunger plugin;
	private HungerUtils utils;
	
	public GlobalListener(Hunger plugin) {
		this.plugin = plugin;
		this.utils	= plugin.getUtils();
	}
	
	/*
	 * Add the potion effect to the player.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		
		// Because e.getEntity() returns HumanEntity, we don't have to perform an instance check.
		Player p = (Player) e.getEntity();
		//FileConfiguration cfg = plugin.getConfig();
		
		HungerChangeEvent event = new HungerChangeEvent(plugin, p);
		plugin.getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled()) {
			e.setCancelled(true);
			return;
		}
	}
	
	/*
	 * Notify players of new updates.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		// Only ops or those with proper permissions get update messages.
		if (!p.hasPermission("hunger.update") && !p.isOp())
			return;
		
		if (!plugin.update)
			return;
		
		plugin.tell(p, "&e" + plugin.name + "&r is available for download at:&e http://dev.bukkit.org/bukkit-plugins/vhunger/");
	}
	
	/*
	 * This event is separate from the last FoodLevelChangeEvent due to readability.
	 * It's goal is to control hunger depletion in a variety of ways.
	 */
	@EventHandler
	public void scaleHunger(FoodLevelChangeEvent e) {
		
		ScaleHungerEvent event = new ScaleHungerEvent(plugin, (Player) e.getEntity());
		plugin.getServer().getPluginManager().callEvent(event);
		
		// Cancel hunger if the custom event is cancelled.
		if (event.isCancelled()) {
			e.setCancelled(true);
			return;
		}
	}
	
	public Hunger getPlugin() {
		return plugin;
	}
	
	public HungerUtils getUtils() {
		return utils;
	}
}
