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
package com.valygard.vhunger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;

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
		
		// Don't cancel the potion effect, just cancel the change in hunger.
		if (p.hasPermission("hunger.nohunger") || !plugin.getConfig().getBoolean("global-settings.hunger")) 
			e.setCancelled(true);

		// Iterate through all valid potion effects and add any that are applicable.
		for (String effect : plugin.getConfig().getConfigurationSection("effects").getKeys(false)) {
			utils.addEffect(p, PotionEffectType.getByName(effect.toUpperCase()));
		}
	}
	
	/*
	 * Notify players of new updates.
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		// Only ops or those with proper permissions get update messages.
		if (!p.hasPermission("hunger.admin") && !p.hasPermission("hunger.update") && !p.isOp())
			return;
		
		if (!plugin.update)
			return;
		
		if (!plugin.getConfig().getBoolean("global-settings.check-for-updates"))
			return;
		
		// If auto-update is disabled, tell the player that vHunger is available for download.
		if (!plugin.getConfig().getBoolean("global-settings.auto-update"))
			plugin.tell(p, "&e" + plugin.name + " &r is available for download at:&e http://dev.bukkit.org/bukkit-plugins/vhunger/");
	}
	
	/*
	 * This event is separate from the last FoodLevelChangeEvent due to readability.
	 * It's goal is to control hunger depletion in a variety of ways.
	 */
	@EventHandler
	public void scaleHunger(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		FileConfiguration c = plugin.getConfig();
		
		// Sanity-Checks
		if (!utils.isValidWorld(p) || !utils.isEnabled()) {
			e.setCancelled(false);
			return;
		}
		
		if (utils.isExempt(p)) {
			e.setCancelled(false);
			return;
		}
		
		if (!c.getBoolean("global-settings.hunger")) {
			e.setCancelled(true);
			return;
		}
		
		if(p.isSprinting()) {
			p.setFoodLevel((int) (p.getFoodLevel() - c.getInt("depletion-rate.sprinting")));
		} else if(p.isSneaking()) {
			p.setFoodLevel((int) (p.getFoodLevel() - c.getInt("depletion-rate.sneaking")));
		} else if(p.isSleeping()) {
			p.setFoodLevel((int) (p.getFoodLevel() - c.getInt("depletion-rate.sleeping")));
		} else if(p.isFlying()) {
			p.setFoodLevel((int) (p.getFoodLevel() - c.getInt("depletion-rate.flying")));
		} else if(p.getLocation().getBlock().getType().equals(Material.WATER)) {
			p.setFoodLevel((int) (p.getFoodLevel() - c.getInt("depletion-rate.swimming")));
		}
		
		
	}
	
	public Hunger getPlugin() {
		return plugin;
	}
	
	public HungerUtils getUtils() {
		return utils;
	}
}
