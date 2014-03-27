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
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffectType;

import com.valygard.vhunger.util.HungerUtils;

/**
 * @author Anand
 *
 */
public class GlobalListener implements Listener {
	private Hunger plugin;
	
	public GlobalListener(Hunger plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		
		// Because e.getEntity() returns HumanEntity, we don't have to perform an instance check.
		Player p = (Player) e.getEntity();
		
		// Don't cancel the potion effect, just cancel the food level change.
		if (p.hasPermission("hunger.nohunger") || !plugin.getConfig().getBoolean("global-settings.hunger")) 
			e.setCancelled(true);
		
		// Iterate through all valid potion effects and add any that are applicable.
		for (PotionEffectType effect : PotionEffectType.values()) {
			HungerUtils.addEffect(p, effect, p.getFoodLevel());
		}
	}
	
	@EventHandler
	public void scaleHunger(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		FileConfiguration c = plugin.getConfig();
		
		// Sanity-Checks
		if (!HungerUtils.isValidWorld(p) || !HungerUtils.isEnabled()) {
			e.setCancelled(false);
			return;
		}
		
		if (HungerUtils.isExempt(p)) {
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
		} else {
			e.setCancelled(false);
		}
		
		
	}
	
	public Hunger getPlugin() {
		return plugin;
	}
}
