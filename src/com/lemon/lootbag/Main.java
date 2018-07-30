package com.lemon.lootbag;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	UseLootbag ulb = new UseLootbag(this);

	@Override
	public void onEnable() {
		// Initialize config
		final FileConfiguration config = this.getConfig();

		config.addDefault("lastCreatedId", 0);
		config.options().copyDefaults(true);
		saveConfig();

		// Register Events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new InventoryClose(this), this);
		pm.registerEvents(ulb, this);

		// Register Commands
		this.getCommand("lootbag").setExecutor(new MainCommand(this));
	}

}
