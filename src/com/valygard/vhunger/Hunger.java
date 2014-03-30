/**
 * Hunger.java is a part of the plugin vHunger.
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

import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.valygard.vhunger.listener.GlobalListener;
import com.valygard.vhunger.listener.WorldListener;
import com.valygard.vhunger.util.HungerUtils;
import com.valygard.vhunger.util.Updater;

/**
 * @author Anand
 * 
 */
public class Hunger extends JavaPlugin {
	// Classes
	private Commands commands;
	private HungerUtils utils;
	
	// Updater related
	public boolean update = false;
	public String name = "";
	
	public void onEnable() {
		// Instantiate our HungerUtils class.
		utils = new HungerUtils(this);
		
		// Register commands
		registerCommands();
		
		// Load the default config-file.
		loadConfig();

		// Register our events
		registerEvents();
		
		// Finally, check for updates.
		checkForUpdates();
	}
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new GlobalListener(this), this);
		getServer().getPluginManager().registerEvents(new WorldListener(this), this);
	}
	
	private void registerCommands() {
		commands = new Commands(this);
		getCommand("hunger").setExecutor(commands);
	}
	
	private void checkForUpdates() {
		if (!getConfig().getBoolean("global-settings.check-for-updates")) {
			getLogger().log(Level.WARNING, "Why you no like being informed about updates??");
			return;
		}
		
		if (getConfig().getBoolean("global-settings.auto-update")) {
			Updater updater = new Updater(this, 65327, this.getFile(), Updater.UpdateType.DEFAULT, true);
			update = updater.getResult() == Updater.UpdateResult.SUCCESS;
			name = updater.getLatestName(); 
			
			if (update)
				getLogger().log(Level.INFO, name + " has been installed. Restart your server for changes to take effect.");
			return;
		}
		Updater updater = new Updater(this, 65327, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, true);
		update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
		name = updater.getLatestName();
		
		if (update)
			getLogger().log(Level.INFO, name + " is available for download at: http://dev.bukkit.org/bukkit-plugins/vhunger/");
	}

	private void loadConfig() {
		saveDefaultConfig();

		FileConfiguration cfg = getConfig();

		// Add defaults
		if (cfg.getConfigurationSection("global-settings") == null) {

			cfg.createSection("global-settings");

			cfg.set("global-settings.enabled", true);
			cfg.set("global-settings.check-for-updates", true);
			cfg.set("global-settings.auto-update", false);
			cfg.set("global-settings.hunger", true);
		}

		List<String> worlds = cfg.getStringList("worlds");
		if (worlds.isEmpty() || worlds == null) {
			worlds.add("world");
			worlds.add("world_nether");
			
			cfg.set("worlds", worlds);
		}

		if (cfg.getConfigurationSection("effects") == null) {
			cfg.set("effects.poison.duration", 7);
			cfg.set("effects.poison.amplifier", 2);
			cfg.set("effects.poison.hunger-at-activation", 16);
			cfg.set("effects.poison.message",
					"&eYou are sick from your starvation... You should really consider eating.");
		}

		if (cfg.getConfigurationSection("depletion-rate") == null) {
			cfg.set("depletion-rate.sprinting", 1.7);
			cfg.set("depletion-rate.sneaking", 0.77);
			cfg.set("depletion-rate.swimming", 1.6);
			cfg.set("depletion-rate.flying", 1.0);
			cfg.set("depletion-rate.sleeping", 0.3);
		}
		
		cfg.options().header(getHeader());

		saveConfig();
	}

	// Get the config header.
	private String getHeader() {
		String s = System.getProperty("line.separator");
		return "vHunger v" + getDescription().getVersion() + " Config-file." + s + s +
				"Edit the following settings to your pleasure." + s +
				"You can view how to configure the config-file, ask questions or report issues at" + s +
				"http://dev.bukkit.org/bukkit-plugins/vhunger/" + s +
				"Happy configuring!";

	}
	
	// Send a message with prefix and allow for '&' hex-color codes.
	public void tell(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.GREEN + "[vHunger] " + ChatColor.RESET + msg));
	}
	
	public HungerUtils getUtils() {
		return utils;
	}
}
