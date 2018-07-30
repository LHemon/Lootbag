package com.lemon.lootbag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UseLootbag implements Listener {
	Main plugin;
	List<ItemStack> ItemList = new ArrayList<ItemStack>();
	List<Integer> IdList = new ArrayList<Integer>();

	public UseLootbag(Main instance) {
		plugin = instance;
		reloadConfig();
	}

	public void reloadConfig() {
		FileConfiguration config = plugin.getConfig();
		try {
			if (config.getConfigurationSection("lootbags") != null) {
				ItemList.clear();
				IdList.clear();
				for (String lb : config.getConfigurationSection("lootbags").getKeys(false)) {
					if (config.getString("lootbags." + lb + ".bagname") != null) {
						// NameList.add(config.getString("lootbags." + lb +
						// ".bagname"));
					} else {
						throw new NullPointerException();
					}
					IdList.add(Integer.valueOf(lb));
					ItemStack item = new ItemStack(Material.AIR, 1);
					item.setType(Material.getMaterial(config.getString("lootbags." + lb + ".item")));
					ItemMeta itemMeta = item.getItemMeta();
					if (config.getString("lootbags." + lb + ".name") != null) {
						itemMeta.setDisplayName(config.getString("lootbags." + lb + ".name"));
					}
					if (config.getStringList("lootbags." + lb + ".lores") != null) {
						itemMeta.setLore(config.getStringList("lootbags." + lb + ".lores"));
					}
					if (config.getConfigurationSection("lootbags." + lb + ".enchs") != null) {
						for (String eName : config.getConfigurationSection("lootbags." + lb + ".enchs")
								.getKeys(false)) {
							Enchantment ench = Enchantment.getByName(eName);
							Integer level = config.getInt("lootbags." + lb + ".enchs." + eName);
							itemMeta.addEnchant(ench, level, true);
						}
					}
					item.setItemMeta(itemMeta);
					ItemList.add(item);
				}
				System.out.println("[Lootbag] 已載入 " + ItemList.size() + " 個禮包");
			} else {
				System.out.println("[Lootbag] 已載入 0 個禮包");
			}
		} catch (Exception e) {
			System.out.println("[Lootbag] 插件載入錯誤");
		}
	}

	@EventHandler
	public void onRightclick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack tool = new ItemStack(Material.AIR, 1);
		tool.setType(player.getInventory().getItemInMainHand().getType());
		tool.setItemMeta(player.getInventory().getItemInMainHand().getItemMeta());
		if (ItemList.contains(tool)) {
			e.setCancelled(true);
			Integer id = IdList.get(ItemList.indexOf(tool));
			player.getInventory().getItemInMainHand()
					.setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

			FileConfiguration config = plugin.getConfig();
			Inventory inv = Bukkit.createInventory(null, 27, config.getString("lootbags." + id + ".bagname"));
			if (config.getConfigurationSection("lootbags." + id + ".contents") != null) {
				for (String slot : config.getConfigurationSection("lootbags." + id + ".contents").getKeys(false)) {
					ItemStack item = new ItemStack(Material.AIR, 1);
					item.setType(
							Material.getMaterial(config.getString("lootbags." + id + ".contents." + slot + ".item")));
					item.setAmount(config.getInt("lootbags." + id + ".contents." + slot + ".amount"));
					ItemMeta itemMeta = item.getItemMeta();
					if (config.getString("lootbags." + id + ".contents." + slot + ".name") != null) {
						itemMeta.setDisplayName(config.getString("lootbags." + id + ".contents." + slot + ".name"));
					}
					if (config.getStringList("lootbags." + id + ".contents." + slot + ".lores") != null) {
						itemMeta.setLore(config.getStringList("lootbags." + id + ".contents." + slot + ".lores"));
					}
					if (config.getConfigurationSection("lootbags." + id + ".contents." + slot + ".enchs") != null) {
						for (String eName : config
								.getConfigurationSection("lootbags." + id + ".contents." + slot + ".enchs")
								.getKeys(false)) {
							Enchantment ench = Enchantment.getByName(eName);
							Integer level = config.getInt("lootbags." + id + ".contents." + slot + ".enchs." + eName);
							itemMeta.addEnchant(ench, level, true);
						}
					}
					item.setItemMeta(itemMeta);
					inv.setItem(Integer.valueOf(slot), item);
				}
			}
			player.openInventory(inv);
		}
	}
}
