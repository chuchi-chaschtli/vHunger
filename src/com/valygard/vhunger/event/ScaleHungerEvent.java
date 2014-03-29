/**
 * ScaleHungerEvent.java is a part of the plugin vHunger.
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
package com.valygard.vhunger.event;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.valygard.vhunger.Hunger;
import com.valygard.vhunger.util.HungerUtils;

/**
 * @author Anand
 * 
 */
public class ScaleHungerEvent extends Event implements Cancellable 
{
	private static final HandlerList handlers = new HandlerList();

	private Hunger plugin;
	private HungerUtils utils;

	private boolean cancelled;

	private Player player;
	private int hunger;

	public ScaleHungerEvent(Hunger plugin, Player player) {
		this.plugin = plugin;
		this.utils = plugin.getUtils();

		this.player = player;
		this.hunger = player.getFoodLevel();
		this.cancelled = false;

		scaleHunger();
	}

	public void scaleHunger() {
		// Cancel if the player is not applicable
		if (!utils.isValidWorld(player) || !utils.isEnabled()) {
			setCancelled(true);
			return;
		}
		
		if (utils.isExempt(player)) {
			setCancelled(true);
			return;
		}
		
		FileConfiguration c = plugin.getConfig();
		// Scale the hunger
		if(player.isSprinting()) {
			player.setFoodLevel((int) Math.round(hunger - c.getDouble("depletion-rate.sprinting")));
		} else if(player.isSneaking()) {
			player.setFoodLevel((int) Math.round(hunger - c.getDouble("depletion-rate.sneaking")));
		} else if(player.isSleeping()) {
			player.setFoodLevel((int) Math.round(hunger - c.getDouble("depletion-rate.sleeping")));
		} else if(player.isFlying()) {
			player.setFoodLevel((int) Math.round(hunger - c.getDouble("depletion-rate.flying")));
		} else if(player.getLocation().getBlock().getType().equals(Material.WATER)) {
			player.setFoodLevel((int) Math.round(hunger - c.getDouble("depletion-rate.swimming")));
		}
	}

	// GETTERS

	public Player getPlayer() {
		return player;
	}

	public String getPlayerByName() {
		return player.getName();
	}

	public int getHunger() {
		return hunger;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Hunger getPlugin() {
		return plugin;
	}

}
