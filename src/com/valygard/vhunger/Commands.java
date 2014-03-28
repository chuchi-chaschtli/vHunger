/**
 * Commands.java is a part of the plugin vHunger.
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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Anand
 *
 */
public class Commands implements CommandExecutor {

	private Hunger plugin;
	
	public Commands(Hunger plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player p = (Player) sender;
		int hunger = p.getFoodLevel();
		
		if (!cmd.getName().equalsIgnoreCase("hunger"))
			return false;
		
		if (args.length == 0) {
			if (!p.hasPermission("hunger.check")) {
				sendIncorrectMessage(p, false, true);
				return false;
			}
			plugin.tell(p, "Your hunger is &e" + hunger + ".");
		}
		
		else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("help")) {
				plugin.tell(p, "&2------ [&avHunger (Version " + plugin.getDescription().getVersion() + ")&2] ------");
				plugin.tell(p, "&a/hunger [player] &r-&e Check a player's hunger.");
				if(p.hasPermission("hunger.admin")) {
					plugin.tell(p, "&a/hunger nourish [player] &r-&e Feed a player.");
					plugin.tell(p, "&a/hunger set [player] <integer> &r-&e Set your own hunger or another player's.");
					plugin.tell(p, "&a/hunger config <save|reload> &r-&e Save or reload the config file.");
				}	
			}
			
			else if (args[0].equalsIgnoreCase("nourish")) {
				if (!p.hasPermission("hunger.nourish")) {
					sendIncorrectMessage(p, false, true);
					return false;
				}
				
				// Set their hunger and saturation to the max.
				p.setFoodLevel(20);
				p.setSaturation(20);
				
				plugin.tell(p, "You have been satiated.");
			} else {
				// If the args[0] is anything else, it should check if its a player.
				if (!p.hasPermission("hunger.check.others")) {
					sendIncorrectMessage(p, false, true);
					return false;
				}
				
				// Check is player is null.
				Player args1 = Bukkit.getPlayer(args[0]);
				if (args1 == null) {
					plugin.tell(p, "The player &e " + args[0] + "&r is offline or does not exist.");
					return false;
				}
				
				plugin.tell(p, args1 + "'s hunger level is &e" + args1.getFoodLevel() + ".");
				
			}
		}
		
		else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("config")) {
				if (args[1].equalsIgnoreCase("reload")) {
					if (!p.hasPermission("hunger.config.reload")) {
						sendIncorrectMessage(p, false, true);
						return false;
					}
					
					// Reload config
					plugin.reloadConfig();
					plugin.tell(p, "Config successfully reloaded.");
				}
				
				else if (args[1].equalsIgnoreCase("save")) {
					if (!p.hasPermission("hunger.config.save")) {
						sendIncorrectMessage(p, false, true);
						return false;
					}
					
					// Save Config
					plugin.saveConfig();
					plugin.tell(p, "Config successfully saved.");
				}
				
				else {
					sendIncorrectMessage(p, false, true);
					return false;
				}
			} 
			
			else if (args[0].equalsIgnoreCase("set")) {
				if (!p.hasPermission("hunger.set")) {
					sendIncorrectMessage(p, false, true);
					return false;
				}
				
				try {
					int newHunger = Integer.parseInt(args[1]);
					p.setFoodLevel(newHunger);
					p.setSaturation(newHunger);
					plugin.tell(p, "You have set your hunger to &e" + newHunger + ".");
				} catch (NumberFormatException e) {
					throw new NumberFormatException("Expected integer between 0-20 for hunger!");
				}
			}
			
			else if (args[0].equalsIgnoreCase("nourish")) {
				if (!p.hasPermission("hunger.nourish.others")) {
					sendIncorrectMessage(p, false, true);
					return false;
				}
				
				Player args2 = Bukkit.getPlayer(args[1]);
				
				if (args2 == null) {
					plugin.tell(p, "The player &e " + args[1] + "&r is offline or does not exist.");
					return false;
				}
				
				// Feed the player.
				args2.setFoodLevel(20);
				args2.setSaturation(20);
				
				plugin.tell(p, "You have satiated &e " + args2.getName() + ".");
				plugin.tell(args2, "You have been satiated.");
			}
			
			else {
				sendIncorrectMessage(p, true, false);
				return false;
			}
		}
		
		else if (args.length > 3) {
			// Our only command with 3 args is /hunger set...
			if (!args[0].equalsIgnoreCase("set")) {
				sendIncorrectMessage(p, true, false);
				return false;
			}
			
			if (!p.hasPermission("hunger.set.others")) {
				sendIncorrectMessage(p, false, true);
				return false;
			}
			
			Player args2 = Bukkit.getPlayer(args[1]);
			
			if (args2 == null) {
				plugin.tell(p, "The player &e " + args[1] + "&r is offline or does not exist.");
				return false;
			}
			
			try {
				int newHunger = Integer.parseInt(args[2]);
				
				args2.setFoodLevel(newHunger);
				args2.setSaturation(newHunger);
				
				plugin.tell(args2, "Your hunger is now &e " + newHunger + ".");
				plugin.tell(p, "You have set the hunger of &e " + args2.getName() + " &rto&e " + newHunger + ".");
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Expected integer between 0-20 for hunger!");
			}
		}
		return true;
	}
	
	// Our incorrect usage formulator.
	private void sendIncorrectMessage(Player p, boolean hasPermission, boolean invalidArgs) {
		if (!hasPermission)
			plugin.tell(p, "You do not have permission to use this command.");
		if (!invalidArgs)
			plugin.tell(p, "Invalid arguments. Use &e/hunger help&r for a list of commands.");
	}
	
	public Hunger getPlugin() {
		return plugin;
	}

}
