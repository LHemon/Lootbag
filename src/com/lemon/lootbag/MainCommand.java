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
	final String WarnPrefix = ChatColor.GOLD + "§�]�t�� >> " + ChatColor.RED;
	final String MsgPrefix = ChatColor.GOLD + "§�]�t�� >> " + ChatColor.YELLOW;
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
							Inventory inv = Bukkit.createInventory(null, 27, ChatColor.YELLOW + "�Ы�§�]");
							player.openInventory(inv);
							Integer lootbagId = plugin.getConfig().getInt("lastCreatedId");
							System.out.println(lootbagId.toString());
							lootbagId++;
							plugin.getConfig().set("lootbags." + lootbagId + ".bagname", args[1].replace("&", "��"));
						} else {
							sender.sendMessage(WarnPrefix + "�A���i�H��Ů�]�w��§�]");
						}
					} else {
						sender.sendMessage(WarnPrefix + "�п�J§�]�W��");
					}
				} else if (args[0].equalsIgnoreCase("edit")) {
					if (args.length == 2) {
						if (isNumeric(args[1])) {
							if (plugin.ulb.IdList.contains(Integer.valueOf(args[1]))) {
								Inventory inv = Bukkit.createInventory(null, 27,
										ChatColor.YELLOW + "�s�褺�e (" + args[1] + ")");
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
								sender.sendMessage(WarnPrefix + "§�]ID ���s�b");
							}
						} else {
							sender.sendMessage(WarnPrefix + "�п�J�Ʀr ID");
						}
					} else {
						sender.sendMessage(WarnPrefix + "�п�J§�] ID");
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						if (isNumeric(args[1])) {
							if (plugin.ulb.IdList.contains(Integer.valueOf(args[1]))) {
								FileConfiguration config = plugin.getConfig();
								Integer lootbagId = Integer.valueOf(args[1]);
								config.set("lootbags." + lootbagId, null);
								sender.sendMessage(MsgPrefix + "�w�R��§�] (ID: " + lootbagId + ")");
								plugin.saveConfig();
								plugin.ulb.reloadConfig();
							} else {
								sender.sendMessage(WarnPrefix + "§�]���s�b");
							}
						} else {
							sender.sendMessage(WarnPrefix + "�п�J�Ʀr ID");
						}
					} else {
						sender.sendMessage(WarnPrefix + "�п�J§�] ID");
					}
				} else if (args[0].equalsIgnoreCase("getid")) {
					if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
						ItemStack tool = new ItemStack(Material.AIR, 1);
						tool.setType(player.getInventory().getItemInMainHand().getType());
						tool.setItemMeta(player.getInventory().getItemInMainHand().getItemMeta());
						if (plugin.ulb.ItemList.contains(tool)) {
							Integer lootbagId = plugin.ulb.IdList.get(plugin.ulb.ItemList.indexOf(tool));
							sender.sendMessage(MsgPrefix + "��W§�]�� ID�O: " + lootbagId);
						} else {
							sender.sendMessage(WarnPrefix + "�A��W�����~���O§�]");
						}
					} else {
						sender.sendMessage(WarnPrefix + "�Ů𤣥i�H�O§�]");
					}
				} else if (args[0].equalsIgnoreCase("help") || args.length == 0) {
					sender.sendMessage(MsgPrefix + ChatColor.GOLD + "���O�t�αо�");
					sender.sendMessage(MsgPrefix + "/lootbag create <§�]�W��>  " + ChatColor.WHITE + "�H��W�����~�Ы�§�]");
					sender.sendMessage(MsgPrefix + "/lootbag edit <§�]ID>  " + ChatColor.WHITE + "�s��§�]���e");
					sender.sendMessage(MsgPrefix + "/lootbag remove <§�]ID>  " + ChatColor.WHITE + "����§�]");
					sender.sendMessage(MsgPrefix + "/lootbag getid  " + ChatColor.WHITE + "���o��W§�]�� ID");
				} else {
					sender.sendMessage(WarnPrefix + "�l���O���s�b");
				}
			} else {
				sender.sendMessage(WarnPrefix + "�п�J /lootbag help �d�ݫ��O");
			}
		} else {
			sender.sendMessage(WarnPrefix + "�u�����a�i�H�ϥΥ����O");
		}
		return true;
	}
}