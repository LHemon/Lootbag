package com.lemon.lootbag;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class InventoryClose implements Listener {
	Main plugin;
	final String WarnPrefix = ChatColor.GOLD + "禮包系統 >> " + ChatColor.RED;
	final String MsgPrefix = ChatColor.GOLD + "禮包系統 >> " + ChatColor.YELLOW;

	public InventoryClose(Main instance) {
		plugin = instance;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		FileConfiguration config = plugin.getConfig();
		if (ChatColor.stripColor(e.getInventory().getName()).equals("創建禮包")) {
			Player player = (Player) e.getPlayer();
			Integer lootbagId = config.getInt("lastCreatedId");
			lootbagId++;
			config.set("lastCreatedId", lootbagId);
			ItemStack tool = player.getInventory().getItemInMainHand();
			// config.set("lootbags." + lootbagId, null);
			config.set("lootbags." + lootbagId + ".item", tool.getType().toString());
			if (tool.getItemMeta().hasDisplayName()) {
				config.set("lootbags." + lootbagId + ".name", tool.getItemMeta().getDisplayName());
			}
			if (tool.getItemMeta().hasLore()) {
				config.set("lootbags." + lootbagId + ".lores", tool.getItemMeta().getLore());
			}
			if (tool.getItemMeta().hasEnchants()) {
				for (Enchantment ench : tool.getEnchantments().keySet()) {
					config.set("lootbags." + lootbagId + ".enchs." + ench.getName(), tool.getEnchantments().get(ench));
				}

			}
			Integer slot = 0;
			for (ItemStack i : e.getInventory().getContents()) {
				if (i != null) {
					config.set("lootbags." + lootbagId + ".contents." + slot + ".item", i.getType().toString());
					config.set("lootbags." + lootbagId + ".contents." + slot + ".amount", i.getAmount());
					if (i.getItemMeta().hasDisplayName()) {
						config.set("lootbags." + lootbagId + ".contents." + slot + ".name",
								i.getItemMeta().getDisplayName());
					}
					if (i.getItemMeta().hasLore()) {
						config.set("lootbags." + lootbagId + ".contents." + slot + ".lores", i.getItemMeta().getLore());
					}
					if (i.getItemMeta().hasEnchants()) {
						for (Enchantment ench : i.getEnchantments().keySet()) {
							config.set("lootbags." + lootbagId + ".contents." + slot + ".enchs." + ench.getName(),
									i.getEnchantments().get(ench));
						}
					}
				}
				slot++;
			}
			plugin.saveConfig();
			plugin.ulb.reloadConfig();
			player.sendMessage(MsgPrefix + "已創建禮包 (ID: " + lootbagId + ")");
		} else if (ChatColor.stripColor(e.getInventory().getName()).contains("編輯內容")) {
			Player player = (Player) e.getPlayer();
			Integer lootbagId = Integer
					.valueOf(ChatColor.stripColor(e.getInventory().getName()).replace("編輯內容 (", "").replace(")", ""));
			Integer slot = 0;
			config.set("lootbags." + lootbagId + ".contents", null);
			for (ItemStack i : e.getInventory().getContents()) {
				if (i != null) {
					config.set("lootbags." + lootbagId + ".contents." + slot + ".item", i.getType().toString());
					config.set("lootbags." + lootbagId + ".contents." + slot + ".amount", i.getAmount());
					if (i.getItemMeta().hasDisplayName()) {
						config.set("lootbags." + lootbagId + ".contents." + slot + ".name",
								i.getItemMeta().getDisplayName());
					}
					if (i.getItemMeta().hasLore()) {
						config.set("lootbags." + lootbagId + ".contents." + slot + ".lores", i.getItemMeta().getLore());
					}
					if (i.getItemMeta().hasEnchants()) {
						for (Enchantment ench : i.getEnchantments().keySet()) {
							config.set("lootbags." + lootbagId + ".contents." + slot + ".enchs." + ench.getName(),
									i.getEnchantments().get(ench));
						}
					}
				}
				slot++;
			}
			plugin.saveConfig();
			plugin.ulb.reloadConfig();
			player.sendMessage(MsgPrefix + "已編輯禮包 (ID: " + lootbagId + ")內容");
		}
	}
}
