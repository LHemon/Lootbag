package com.lemon.lootbag;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class MainCommand implements CommandExecutor {
	final String WarnPrefix = ChatColor.GOLD + "禮包系統 >> " + ChatColor.RED;
	final String MsgPrefix = ChatColor.GOLD + "禮包系統 >> " + ChatColor.YELLOW;
	Main plugin;

	public MainCommand(Main instance) {
		plugin = instance;
	}

	public boolean isNumeric(String s) {
		return s != null && s.matches("[-+]?\\d*\\.?\\d+");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("create")) {
					if (args.length == 2) {
						if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
							Inventory inv = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "創建禮包");
							player.openInventory(inv);
							Integer lootbagId = plugin.getConfig().getInt("lastCreatedId");
							System.out.println(lootbagId.toString());
							lootbagId++;
							plugin.getConfig().set("lootbags." + lootbagId + ".bagname", args[1].replace("&", "§"));
						} else {
							sender.sendMessage(WarnPrefix + "你不可以把空氣設定為禮包");
						}
					} else {
						sender.sendMessage(WarnPrefix + "請輸入禮包名稱");
					}
				} else if (args[0].equalsIgnoreCase("edit")) {
					if (args.length == 2) {
						if (isNumeric(args[1])) {
							if (plugin.ulb.IdList.contains(Integer.valueOf(args[1]))) {
								Inventory inv = Bukkit.createInventory(null, 27,
										ChatColor.YELLOW + "編輯內容 (" + args[1] + ")");
								FileConfiguration config = plugin.getConfig();
								Integer id = Integer.valueOf(args[1]);
								for (String slot : config.getConfigurationSection("lootbags." + id + ".contents")
										.getKeys(false)) {
									ItemStack item = new ItemStack(Material.AIR, 1);
									item.setType(Material.getMaterial(
											config.getString("lootbags." + id + ".contents." + slot + ".item")));
									ItemMeta itemMeta = item.getItemMeta();
									if (config.getString("lootbags." + id + ".contents." + slot + ".name") != null) {
										itemMeta.setDisplayName(
												config.getString("lootbags." + id + ".contents." + slot + ".name"));
									}
									if (config
											.getStringList("lootbags." + id + ".contents." + slot + ".lores") != null) {
										itemMeta.setLore(config
												.getStringList("lootbags." + id + ".contents." + slot + ".lores"));
									}
									if (config.getConfigurationSection(
											"lootbags." + id + ".contents." + slot + ".enchs") != null) {
										for (String eName : config
												.getConfigurationSection(
														"lootbags." + id + ".contents." + slot + ".enchs")
												.getKeys(false)) {
											Enchantment ench = Enchantment.getByName(eName);
											Integer level = config
													.getInt("lootbags." + id + ".contents." + slot + ".enchs." + eName);
											itemMeta.addEnchant(ench, level, true);
										}
									}
									item.setItemMeta(itemMeta);
									inv.setItem(Integer.valueOf(slot), item);
								}
								player.openInventory(inv);
							} else {
								sender.sendMessage(WarnPrefix + "禮包ID 不存在");
							}
						} else {
							sender.sendMessage(WarnPrefix + "請輸入數字 ID");
						}
					} else {
						sender.sendMessage(WarnPrefix + "請輸入禮包 ID");
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						if (isNumeric(args[1])) {
							if (plugin.ulb.IdList.contains(Integer.valueOf(args[1]))) {
								FileConfiguration config = plugin.getConfig();
								Integer lootbagId = Integer.valueOf(args[1]);
								config.set("lootbags." + lootbagId, null);
								sender.sendMessage(MsgPrefix + "已刪除禮包 (ID: " + lootbagId + ")");
								plugin.saveConfig();
								plugin.ulb.reloadConfig();
							} else {
								sender.sendMessage(WarnPrefix + "禮包不存在");
							}
						} else {
							sender.sendMessage(WarnPrefix + "請輸入數字 ID");
						}
					} else {
						sender.sendMessage(WarnPrefix + "請輸入禮包 ID");
					}
				} else if (args[0].equalsIgnoreCase("getid")) {
					if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
						ItemStack tool = new ItemStack(Material.AIR, 1);
						tool.setType(player.getInventory().getItemInMainHand().getType());
						tool.setItemMeta(player.getInventory().getItemInMainHand().getItemMeta());
						if (plugin.ulb.ItemList.contains(tool)) {
							Integer lootbagId = plugin.ulb.IdList.get(plugin.ulb.ItemList.indexOf(tool));
							sender.sendMessage(MsgPrefix + "手上禮包的 ID是: " + lootbagId);
						} else {
							sender.sendMessage(WarnPrefix + "你手上的物品不是禮包");
						}
					} else {
						sender.sendMessage(WarnPrefix + "空氣不可以是禮包");
					}
				} else if (args[0].equalsIgnoreCase("help") || args.length == 0) {
					sender.sendMessage(MsgPrefix + ChatColor.GOLD + "指令系統教學");
					sender.sendMessage(MsgPrefix + "/lootbag create <禮包名稱>  " + ChatColor.WHITE + "以手上的物品創建禮包");
					sender.sendMessage(MsgPrefix + "/lootbag edit <禮包ID>  " + ChatColor.WHITE + "編輯禮包內容");
					sender.sendMessage(MsgPrefix + "/lootbag remove <禮包ID>  " + ChatColor.WHITE + "移除禮包");
					sender.sendMessage(MsgPrefix + "/lootbag getid  " + ChatColor.WHITE + "取得手上禮包的 ID");
				} else {
					sender.sendMessage(WarnPrefix + "子指令不存在");
				}
			} else {
				sender.sendMessage(WarnPrefix + "請輸入 /lootbag help 查看指令");
			}
		} else {
			sender.sendMessage(WarnPrefix + "只有玩家可以使用本指令");
		}
		return true;
	}
}