package nl.svenar.PowerRanks.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.svenar.PowerRanks.PowerRanks;
import nl.svenar.PowerRanks.Cache.CachedConfig;
import nl.svenar.PowerRanks.Cache.CachedPlayers;
import nl.svenar.PowerRanks.Cache.CachedRanks;
import nl.svenar.PowerRanks.addons.PowerRanksAddon;
import nl.svenar.PowerRanks.addons.PowerRanksAddon.RankChangeCause;
import nl.svenar.PowerRanks.addons.PowerRanksPlayer;

import org.bukkit.event.Listener;

public class Users implements Listener {
	PowerRanks m;

	public Users(PowerRanks m) {
		this.m = m;
	}

	public void setGroup(Player player, String t, String rank, boolean fireAddonEvent) {
		YamlConfiguration langYaml = PowerRanks.loadLangFile();
		if (player != null) {
			if (player.hasPermission("powerranks.cmd.set") || player.hasPermission("powerranks.cmd.set." + rank)) {
				Player target = Bukkit.getServer().getPlayer(t);

				if (target != null) {
					File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
					File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
					YamlConfiguration rankYaml = new YamlConfiguration();
					YamlConfiguration playerYaml = new YamlConfiguration();
					try {
						rankYaml.load(rankFile);
						if (rankYaml.get("Groups." + rank) != null) {
							this.m.removePermissions(player);
							playerYaml.load(playerFile);
							String oldRank = playerYaml.getString("players." + target.getUniqueId() + ".rank");
							playerYaml.set("players." + target.getUniqueId() + ".rank", (Object) rank);
							playerYaml.save(playerFile);
							CachedPlayers.update();
							if (fireAddonEvent && CachedConfig.getBoolean("announcements.rankup.enabled")) {
								Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.rankup.format").replace("[player]", t).replace("[rank]", rank).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
							}

							if (fireAddonEvent)
								for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
									PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, target);
									prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rank, RankChangeCause.SET, true);
								}

							Messages.messageSetRankSuccessSender(player, t, rank);
							Messages.messageSetRankSuccessTarget(target, player.getName(), rank);
							this.m.setupPermissions(target);
							this.m.updateTablistName(target);

						} else {
							Messages.messageGroupNotFound(player, rank);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else {
					File rankFile2 = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
					File playerFile2 = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
					YamlConfiguration rankYaml2 = new YamlConfiguration();
					YamlConfiguration playerYaml2 = new YamlConfiguration();
					try {
						rankYaml2.load(rankFile2);
						if (rankYaml2.get("Groups." + rank) != null) {
							playerYaml2.load(playerFile2);

							boolean offline_player_found = false;

							for (String key : playerYaml2.getConfigurationSection("players").getKeys(false)) {
								if (playerYaml2.getString("players." + key + ".name").equalsIgnoreCase(t)) {
									String oldRank = playerYaml2.getString("players." + key + ".rank");
									playerYaml2.set("players." + key + ".rank", (Object) rank);
									playerYaml2.save(playerFile2);
									CachedPlayers.update();
									if (fireAddonEvent && CachedConfig.getBoolean("announcements.rankup.enabled")) {
										Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.rankup.format").replace("[player]", t).replace("[rank]", rank).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
									}

									if (fireAddonEvent)
										for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
											PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, t);
											prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rank, RankChangeCause.SET, false);
										}

									Messages.messageSetRankSuccessSender(player, t, rank);

									offline_player_found = true;
								}
							}

							if (!offline_player_found) {
								Messages.messagePlayerNotFound(player, t);
							}
						} else {
							Messages.messageGroupNotFound(player, rank);
						}
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			ConsoleCommandSender console = Bukkit.getConsoleSender();
			Player target2 = Bukkit.getServer().getPlayer(t);

			if (target2 != null) {
				File rankFile2 = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
				File playerFile2 = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
				YamlConfiguration rankYaml2 = new YamlConfiguration();
				YamlConfiguration playerYaml2 = new YamlConfiguration();
				try {
					rankYaml2.load(rankFile2);
					if (rankYaml2.get("Groups." + rank) != null) {
						this.m.removePermissions(target2);
						playerYaml2.load(playerFile2);
						String oldRank = playerYaml2.getString("players." + target2.getUniqueId() + ".rank");
						playerYaml2.set("players." + target2.getUniqueId() + ".rank", (Object) rank);
						playerYaml2.save(playerFile2);
						CachedPlayers.update();
						if (fireAddonEvent && CachedConfig.getBoolean("announcements.rankup.enabled")) {
							Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.rankup.format").replace("[player]", t).replace("[rank]", rank).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
						}

						if (fireAddonEvent)
							for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
								PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, target2);
								prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rank, RankChangeCause.SET, true);
							}

						Messages.messageSetRankSuccessSender(console, t, rank);
						Messages.messageSetRankSuccessTarget(target2, console.getName(), rank);
						this.m.setupPermissions(target2);
						this.m.updateTablistName(target2);

					} else {
						Messages.messageGroupNotFound(console, rank);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else {
				File rankFile2 = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
				File playerFile2 = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
				YamlConfiguration rankYaml2 = new YamlConfiguration();
				YamlConfiguration playerYaml2 = new YamlConfiguration();
				try {
					rankYaml2.load(rankFile2);
					if (rankYaml2.get("Groups." + rank) != null) {
						playerYaml2.load(playerFile2);

						boolean offline_player_found = false;

						if (playerYaml2.isConfigurationSection("players")) {
							for (String key : playerYaml2.getConfigurationSection("players").getKeys(false)) {
								if (playerYaml2.getString("players." + key + ".name").equalsIgnoreCase(t)) {
									String oldRank = playerYaml2.getString("players." + key + ".rank");
									playerYaml2.set("players." + key + ".rank", (Object) rank);
									playerYaml2.save(playerFile2);
									CachedPlayers.update();
									if (fireAddonEvent && CachedConfig.getBoolean("announcements.rankup.enabled")) {
										Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.rankup.format").replace("[player]", t).replace("[rank]", rank).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
									}

									if (fireAddonEvent)
										for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
											PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, t);
											prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rank, RankChangeCause.SET, false);
										}

									Messages.messageSetRankSuccessSender(console, t, rank);

									offline_player_found = true;
								}
							}
						}
						if (!offline_player_found) {
							Messages.messagePlayerNotFound(console, t);
						}
					} else {
						Messages.messageGroupNotFound(console, rank);
					}
				} catch (IOException | InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean setGroup(Player player, String rank, boolean fireAddonEvent) {
		YamlConfiguration langYaml = PowerRanks.loadLangFile();
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		YamlConfiguration playerYaml = new YamlConfiguration();
		boolean success = false;
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				this.m.removePermissions(player);
				playerYaml.load(playerFile);
				String oldRank = playerYaml.getString("players." + player.getUniqueId() + ".rank");
				playerYaml.set("players." + player.getUniqueId() + ".rank", (Object) rank);
				playerYaml.save(playerFile);
				CachedPlayers.update();
				if (fireAddonEvent && CachedConfig.getBoolean("announcements.rankup.enabled")) {
					Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.rankup.format").replace("[player]", player.getDisplayName()).replace("[rank]", rank).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
				}

				if (fireAddonEvent)
					for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
						PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, player);
						prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rank, RankChangeCause.SET, true);
					}

				this.m.setupPermissions(player);
				this.m.updateTablistName(player);

				Messages.messageSetRankSuccessSender(player, player.getName(), rank);
				success = true;
			} else {
				success = false;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return success;
	}

	public String getRanksConfigFieldString(String rank, String field) {
		String value = "";
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			value = rankYaml.getString("Groups." + rank + "." + field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public boolean setRanksConfigFieldString(String rank, String field, String new_value) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			rankYaml.set("Groups." + rank + "." + field, new_value);
			rankYaml.save(rankFile);
			CachedRanks.update();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getRanksConfigFieldInt(String rank, String field) {
		int value = -1;
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			value = rankYaml.getInt("Groups." + rank + "." + field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public boolean setRanksConfigFieldInt(String rank, String field, int new_value) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			rankYaml.set("Groups." + rank + "." + field, new_value);
			rankYaml.save(rankFile);
			CachedRanks.update();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean getRanksConfigFieldBoolean(String rank, String field) {
		boolean value = false;
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			value = rankYaml.getBoolean("Groups." + rank + "." + field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public boolean setRanksConfigFieldBoolean(String rank, String field, boolean new_value) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			rankYaml.set("Groups." + rank + "." + field, new_value);
			rankYaml.save(rankFile);
			CachedRanks.update();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getGroup(String plr, String t) {
		Player sender = (plr == null || plr == "API") ? null : Bukkit.getServer().getPlayer(plr);
		Player target = Bukkit.getServer().getPlayer(t);
		String target_name = "";
		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playerYaml = new YamlConfiguration();
		String group = "";
		if (target != null) {
			try {
				playerYaml.load(playerFile);
				group = playerYaml.getString("players." + target.getUniqueId() + ".rank");
				target_name = target.getName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				playerYaml.load(playerFile);
				for (String key : playerYaml.getConfigurationSection("players").getKeys(false)) {
					if (playerYaml.getString("players." + key + ".name").equalsIgnoreCase(t)) {
						group = playerYaml.getString("players." + key + ".rank");
						target_name = playerYaml.getString("players." + key + ".name");
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (group.length() > 0) {
			if (sender != null) {
				Messages.messagePlayerCheckRank(sender, target_name, group);
			} else {
				Messages.messagePlayerCheckRank(Bukkit.getConsoleSender(), target_name, group);
			}
		} else {
			if (sender != null) {
				Messages.messagePlayerNotFound(sender, t);
			} else {
				Messages.messagePlayerNotFound(Bukkit.getConsoleSender(), t);
			}
		}
		return (group.length() == 0) ? "error" : group;
	}

	public String getGroup(Player player) {
//		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
//		YamlConfiguration playerYaml = new YamlConfiguration();
//		String group = null;
//		try {
//			playerYaml.load(playerFile);
//			group = playerYaml.getString("players." + player.getUniqueId() + ".rank");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return group;
		return CachedPlayers.getString("players." + player.getUniqueId() + ".rank");
	}

	public String getGroup(String playername) {
//		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
//		YamlConfiguration playerYaml = new YamlConfiguration();
//		String group = null;
		String uuid = "";
		String group = null;
		if (Bukkit.getServer().getPlayer(playername) != null)
			uuid = Bukkit.getServer().getPlayer(playername).getUniqueId().toString();

		if (uuid.length() == 0) {
			for (String key : CachedPlayers.getConfigurationSection("players").getKeys(false)) {
				if (CachedPlayers.getString("players." + key + ".name").equalsIgnoreCase(playername)) {
					uuid = key;
				}
			}
		} else if (uuid.length() != 0) {
			group = CachedPlayers.getString("players." + uuid + ".rank");
		}
		return group;

//		try {
//			playerYaml.load(playerFile);
//
//			if (uuid.length() == 0) {
//				for (String key : playerYaml.getConfigurationSection("players").getKeys(false)) {
//					if (playerYaml.getString("players." + key + ".name").equalsIgnoreCase(playername)) {
//						uuid = key;
//					}
//				}
//			}
//
//			if (uuid.length() != 0) {
//				group = playerYaml.getString("players." + uuid + ".rank");
//			} else {
//				return "";
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return group;
	}

	public Set<String> getGroups() {
		return CachedRanks.getConfigurationSection("Groups").getKeys(false);
//		ConfigurationSection ranks = null;
//		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
//		YamlConfiguration rankYaml = new YamlConfiguration();
//		try {
//			rankYaml.load(rankFile);
//			ranks = rankYaml.getConfigurationSection("Groups");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return ranks.getKeys(false);
	}

	public boolean addPermission(String rank, String permission) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		if (permission.contains("/") || permission.contains(":")) {
			return false;
		}

		try {
			rankYaml.load(rankFile);
			if (!rank.equals("*")) {
				if (rankYaml.get("Groups." + rank) != null) {
					List<String> list = (List<String>) rankYaml.getStringList("Groups." + rank + ".permissions");
					if (!list.contains(permission)) {
						list.add(permission);
						rankYaml.set("Groups." + rank + ".permissions", (Object) list);
						rankYaml.save(rankFile);
						CachedRanks.update();
					}
					this.m.updatePlayersWithRank(this, rank);
					return true;
				}
			} else {
				for (String r : getGroups()) {
					if (rankYaml.get("Groups." + r) != null) {
						List<String> list = (List<String>) rankYaml.getStringList("Groups." + r + ".permissions");
						if (!list.contains(permission)) {
							list.add(permission);
							rankYaml.set("Groups." + r + ".permissions", (Object) list);
							this.m.updatePlayersWithRank(this, r);
						}
					}
				}
				rankYaml.save(rankFile);
				CachedRanks.update();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removePermission(String rank, String permission) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (!rank.equals("*")) {
				if (rankYaml.get("Groups." + rank) != null) {
					List<String> list = (List<String>) rankYaml.getStringList("Groups." + rank + ".permissions");
					list.remove(permission);
					rankYaml.set("Groups." + rank + ".permissions", (Object) list);
					rankYaml.save(rankFile);
					CachedRanks.update();
					this.m.updatePlayersWithRank(this, rank);
					return true;
				}

			} else {
				for (String r : getGroups()) {
					if (rankYaml.get("Groups." + r) != null) {
						List<String> list = (List<String>) rankYaml.getStringList("Groups." + r + ".permissions");
						list.remove(permission);
						rankYaml.set("Groups." + r + ".permissions", (Object) list);
						this.m.updatePlayersWithRank(this, r);
					}
				}
				rankYaml.save(rankFile);
				CachedRanks.update();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addInheritance(String rank, String inheritance) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				List<String> list = (List<String>) rankYaml.getStringList("Groups." + rank + ".inheritance");
				if (!list.contains(inheritance)) {
					list.add(inheritance);
				}
				rankYaml.set("Groups." + rank + ".inheritance", (Object) list);
				rankYaml.save(rankFile);
				CachedRanks.update();
				this.m.updatePlayersWithRank(this, rank);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setPrefix(String rank, String prefix) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				rankYaml.set("Groups." + rank + ".chat.prefix", (Object) prefix);
				rankYaml.save(rankFile);
				CachedRanks.update();
				this.m.updatePlayersWithRank(this, rank);
				this.m.updatePlayersTABlistWithRank(this, rank);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setSuffix(String rank, String suffix) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				rankYaml.set("Groups." + rank + ".chat.suffix", (Object) suffix);
				rankYaml.save(rankFile);
				CachedRanks.update();
				this.m.updatePlayersWithRank(this, rank);
				this.m.updatePlayersTABlistWithRank(this, rank);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setChatColor(String rank, String color) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				rankYaml.set("Groups." + rank + ".chat.chatColor", (Object) color);
				rankYaml.save(rankFile);
				CachedRanks.update();
				this.m.updatePlayersWithRank(this, rank);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setNameColor(String rank, String color) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				rankYaml.set("Groups." + rank + ".chat.nameColor", (Object) color);
				rankYaml.save(rankFile);
				CachedRanks.update();
				this.m.updatePlayersWithRank(this, rank);
				this.m.updatePlayersTABlistWithRank(this, rank);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeInheritance(String rank, String inheritance) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				List<String> list = (List<String>) rankYaml.getStringList("Groups." + rank + ".inheritance");
				if (list.contains(inheritance)) {
					list.remove(inheritance);
				}
				rankYaml.set("Groups." + rank + ".inheritance", (Object) list);
				rankYaml.save(rankFile);
				CachedRanks.update();
				this.m.updatePlayersWithRank(this, rank);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createRank(String rank) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) == null) {
				rankYaml.set("Groups." + rank + ".permissions", new ArrayList<String>());
				rankYaml.set("Groups." + rank + ".inheritance", new ArrayList<String>());
				rankYaml.set("Groups." + rank + ".build", true);
				rankYaml.set("Groups." + rank + ".chat.prefix", "[&7" + rank + "&r]");
				rankYaml.set("Groups." + rank + ".chat.suffix", "");
				rankYaml.set("Groups." + rank + ".chat.chatColor", "&f");
				rankYaml.set("Groups." + rank + ".chat.nameColor", "&f");
				rankYaml.set("Groups." + rank + ".level.promote", "");
				rankYaml.set("Groups." + rank + ".level.demote", "");
				rankYaml.set("Groups." + rank + ".economy.buyable", new ArrayList<String>());
				rankYaml.set("Groups." + rank + ".economy.cost", 0);
				rankYaml.set("Groups." + rank + ".gui.icon", "stone");
				rankYaml.save(rankFile);
				CachedRanks.update();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteRank(String rank) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				rankYaml.set("Groups." + rank, (Object) null);
				rankYaml.save(rankFile);
				CachedRanks.update();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setBuild(String rank, boolean enabled) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rank) != null) {
				rankYaml.set("Groups." + rank + ".build", (Object) enabled);
				rankYaml.save(rankFile);
				CachedRanks.update();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean promote(String playername) {
		YamlConfiguration langYaml = PowerRanks.loadLangFile();
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		YamlConfiguration playerYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player != null) {
			try {
				rankYaml.load(rankFile);
				playerYaml.load(playerFile);
				String oldRank = playerYaml.getString("players." + player.getUniqueId() + ".rank");
				String rank = playerYaml.getString("players." + player.getUniqueId() + ".rank");
				if (rankYaml.get("Groups." + rank) != null) {
					String rankname = rankYaml.getString("Groups." + rank + ".level.promote");
					if (rankYaml.get("Groups." + rankname) != null && rankname.length() > 0) {
						this.setGroup(player, rankname, false);
						if (CachedConfig.getBoolean("announcements.promote.enabled")) {
							Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.promote.format").replace("[player]", playername).replace("[rank]", rankname).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
						}

						for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
							PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, player);
							prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rank, RankChangeCause.PROMOTE, true);
						}
//						playerYaml.set("players." + player.getUniqueId() + ".rank", (Object) rankname);
//						playerYaml.save(playerFile);
//						CachedPlayers.update();
//						this.m.setupPermissions(player);
//						this.m.updateTablistName(player);
						this.m.updatePlayersWithRank(this, rank);

						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				rankYaml.load(rankFile);
				playerYaml.load(playerFile);

				boolean offline_player_found = false;

				for (String key : playerYaml.getConfigurationSection("players").getKeys(false)) {
					if (playerYaml.getString("players." + key + ".name").equalsIgnoreCase(playername)) {
						String oldRank = playerYaml.getString("players." + key + ".rank");
						String rankname = rankYaml.getString("Groups." + playerYaml.getString("players." + key + ".rank") + ".level.promote");
						if (rankname.length() == 0)
							return false;

						this.setGroup(player, rankname, false);
						if (CachedConfig.getBoolean("announcements.promote.enabled")) {
							Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.promote.format").replace("[player]", playername).replace("[rank]", rankname).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
						}

						for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
							PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, player);
							prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rankname, RankChangeCause.PROMOTE, true);
						}
//						playerYaml.set("players." + key + ".rank", (Object) rankname);
//						playerYaml.save(playerFile);
//						CachedPlayers.update();
//						this.m.setupPermissions(player);
//						this.m.updateTablistName(player);

						offline_player_found = true;
						return true;
					}
				}

				if (!offline_player_found) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean demote(String playername) {
		YamlConfiguration langYaml = PowerRanks.loadLangFile();
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		YamlConfiguration playerYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player != null) {
			try {
				rankYaml.load(rankFile);
				playerYaml.load(playerFile);
				String oldRank = playerYaml.getString("players." + player.getUniqueId() + ".rank");
				String rank = playerYaml.getString("players." + player.getUniqueId() + ".rank");
				if (rankYaml.get("Groups." + rank) != null) {
					String rankname = rankYaml.getString("Groups." + rank + ".level.demote");
					if (rankYaml.get("Groups." + rankname) != null && rankname.length() > 0) {
						this.setGroup(player, rankname, false);
						if (CachedConfig.getBoolean("announcements.demote.enabled")) {
							Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.demote.format").replace("[player]", playername).replace("[rank]", rankname).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
						}

						for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
							PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, player);
							prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rankname, RankChangeCause.DEMOTE, true);
						}
//						playerYaml.set("players." + player.getUniqueId() + ".rank", (Object) rankname);
//						playerYaml.save(playerFile);
//						CachedPlayers.update();
						this.m.updatePlayersWithRank(this, rank);
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				rankYaml.load(rankFile);
				playerYaml.load(playerFile);

				boolean offline_player_found = false;

				for (String key : playerYaml.getConfigurationSection("players").getKeys(false)) {
					if (playerYaml.getString("players." + key + ".name").equalsIgnoreCase(playername)) {
						String oldRank = playerYaml.getString("players." + key + ".rank");
						String rankname = rankYaml.getString("Groups." + playerYaml.getString("players." + key + ".rank") + ".level.demote");
						if (rankname.length() == 0)
							return false;

						this.setGroup(player, rankname, false);
						if (CachedConfig.getBoolean("announcements.demote.enabled")) {
							Bukkit.broadcastMessage(PowerRanks.chatColor(CachedConfig.getString("announcements.demote.format").replace("[player]", playername).replace("[rank]", rankname).replace("[powerranks_prefix]", langYaml.getString("general.prefix").replace("%plugin_name%", PowerRanks.pdf.getName())), true));
						}

						for (Entry<File, PowerRanksAddon> prAddon : this.m.addonsManager.addonClasses.entrySet()) {
							PowerRanksPlayer prPlayer = new PowerRanksPlayer(this.m, player);
							prAddon.getValue().onPlayerRankChange(prPlayer, oldRank, rankname, RankChangeCause.PROMOTE, true);
						}
//						playerYaml.set("players." + key + ".rank", (Object) rankname);
//						playerYaml.save(playerFile);
//						CachedPlayers.update();
						offline_player_found = true;
						return true;
					}
				}

				if (!offline_player_found) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean renameRank(String rank, String to) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		YamlConfiguration playerYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
			playerYaml.load(playerFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		if (rankYaml.get("Groups." + rank) != null) {
			List<String> listPermissions = (List<String>) rankYaml.getStringList("Groups." + to + ".permissions");
			for (String line : rankYaml.getStringList("Groups." + rank + ".permissions")) {
				listPermissions.add(line);
			}
			rankYaml.set("Groups." + to + ".permissions", (Object) listPermissions);

			List<String> listInheritance = (List<String>) rankYaml.getStringList("Groups." + to + ".inheritance");
			for (String line : rankYaml.getStringList("Groups." + rank + ".inheritance")) {
				listInheritance.add(line);
			}
			rankYaml.set("Groups." + to + ".inheritance", (Object) listInheritance);

			rankYaml.set("Groups." + to + ".build", rankYaml.get("Groups." + rank + ".build"));
			rankYaml.set("Groups." + to + ".chat.prefix", rankYaml.get("Groups." + rank + ".chat.prefix"));
			rankYaml.set("Groups." + to + ".chat.suffix", rankYaml.get("Groups." + rank + ".chat.suffix"));
			rankYaml.set("Groups." + to + ".chat.chatColor", rankYaml.get("Groups." + rank + ".chat.chatColor"));
			rankYaml.set("Groups." + to + ".chat.nameColor", rankYaml.get("Groups." + rank + ".chat.nameColor"));
			rankYaml.set("Groups." + to + ".level.promote", rankYaml.get("Groups." + rank + ".level.promote"));
			rankYaml.set("Groups." + to + ".level.demote", rankYaml.get("Groups." + rank + ".level.demote"));

			List<String> listEconomyBuyable = (List<String>) rankYaml.getStringList("Groups." + to + ".economy.buyable");
			for (String line : rankYaml.getStringList("Groups." + rank + ".economy.buyable")) {
				listEconomyBuyable.add(line);
			}
			rankYaml.set("Groups." + to + ".economy.buyable", (Object) listEconomyBuyable);
			rankYaml.set("Groups." + to + ".economy.cost", rankYaml.get("Groups." + rank + ".economy.cost"));
			rankYaml.set("Groups." + to + ".gui.icon", rankYaml.get("Groups." + rank + ".gui.icon"));

			ConfigurationSection players = playerYaml.getConfigurationSection("players");
			for (String p : players.getKeys(false)) {
				if (playerYaml.getString("players." + p + ".rank") != null) {
//					PowerRanks.log.info(playerYaml.getString("players." + p + ".rank") + (playerYaml.getString("players." + p + ".rank").equalsIgnoreCase(rank) ? " Match" : " No Match"));
					if (playerYaml.getString("players." + p + ".rank").equalsIgnoreCase(rank)) {
						playerYaml.set("players." + p + ".rank", to);
					}
				}
			}
			try {
				rankYaml.save(rankFile);
				CachedRanks.update();
				playerYaml.save(playerFile);
				CachedPlayers.update();
			} catch (IOException e) {
				e.printStackTrace();
			}

			deleteRank(rank);
			return true;
		}
		return false;
	}

	public boolean setDefaultRank(String rankname) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		if (rankYaml.get("Groups." + rankname) != null) {
			rankYaml.set("Default", rankname);
			try {
				rankYaml.save(rankFile);
				CachedRanks.update();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return true;
		}

		return false;
	}

	public String getRankIgnoreCase(String rankname) {
		String rank = rankname;

		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			ConfigurationSection ranks = rankYaml.getConfigurationSection("Groups");
			for (String r : ranks.getKeys(false)) {
				if (r.equalsIgnoreCase(rankname)) {
					rank = r;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rank;
	}

	public List<String> getPermissions(String rank) {
		List<String> permissions = new ArrayList<String>();
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		permissions = rankYaml.getStringList("Groups." + rank + ".permissions");
		return permissions;
	}

	public List<String> getInheritances(String rank) {
		List<String> inheritances = new ArrayList<String>();
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		inheritances = rankYaml.getStringList("Groups." + rank + ".inheritance");
		return inheritances;
	}

	public Set<String> getCachedPlayers() {
		ConfigurationSection players = null;
		File playerFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playerYaml = new YamlConfiguration();
		try {
			playerYaml.load(playerFile);
			players = playerYaml.getConfigurationSection("players");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return players.getKeys(false);
	}

	public String getPrefix(Player player) {
		String prefix = "";
		String rank = getGroup(player);

		final File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		final YamlConfiguration rankYaml = new YamlConfiguration();

		try {
			rankYaml.load(rankFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		prefix = (rankYaml.getString("Groups." + rank + ".chat.prefix") != null) ? rankYaml.getString("Groups." + rank + ".chat.prefix") : "";

		return prefix;
	}

	public String getPrefix(String rank) {
		String prefix = "";
		rank = this.getRankIgnoreCase(rank);

		final File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		final YamlConfiguration rankYaml = new YamlConfiguration();

		try {
			rankYaml.load(rankFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		prefix = (rankYaml.getString("Groups." + rank + ".chat.prefix") != null) ? rankYaml.getString("Groups." + rank + ".chat.prefix") : "";

		return prefix;
	}

	public String getSuffix(Player player) {
		String suffix = "";
		String rank = getGroup(player);

		final File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		final YamlConfiguration rankYaml = new YamlConfiguration();

		try {
			rankYaml.load(rankFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		suffix = (rankYaml.getString("Groups." + rank + ".chat.suffix") != null) ? rankYaml.getString("Groups." + rank + ".chat.suffix") : "";

		return suffix;
	}
	
	public String getSuffix(String rank) {
		String suffix = "";
		rank = this.getRankIgnoreCase(rank);

		final File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		final YamlConfiguration rankYaml = new YamlConfiguration();

		try {
			rankYaml.load(rankFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		suffix = (rankYaml.getString("Groups." + rank + ".chat.suffix") != null) ? rankYaml.getString("Groups." + rank + ".chat.suffix") : "";

		return suffix;
	}

	public String getChatColor(Player player) {
		String color = "";
		String rank = getGroup(player);

		final File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		final YamlConfiguration rankYaml = new YamlConfiguration();

		try {
			rankYaml.load(rankFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		color = (rankYaml.getString("Groups." + rank + ".chat.chatColor") != null) ? rankYaml.getString("Groups." + rank + ".chat.chatColor") : "";

		return color;
	}

	public String getNameColor(Player player) {
		String color = "";
		String rank = getGroup(player);

		final File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		final YamlConfiguration rankYaml = new YamlConfiguration();

		try {
			rankYaml.load(rankFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		color = (rankYaml.getString("Groups." + rank + ".chat.nameColor") != null) ? rankYaml.getString("Groups." + rank + ".chat.nameColor") : "";

		return color;
	}

	public String getDefaultRanks() {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		return rankYaml.getString("Default");
	}

	public boolean addBuyableRank(String rankname, String rankname2) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		rankname = getRankIgnoreCase(rankname);
		rankname2 = getRankIgnoreCase(rankname2);
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rankname) != null) {
				List<String> list = (List<String>) rankYaml.getStringList("Groups." + rankname + ".economy.buyable");
				if (!list.contains(rankname2)) {
					list.add(rankname2);
				}
				rankYaml.set("Groups." + rankname + ".economy.buyable", (Object) list);
				rankYaml.save(rankFile);
				CachedRanks.update();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delBuyableRank(String rankname, String rankname2) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		rankname = getRankIgnoreCase(rankname);
		rankname2 = getRankIgnoreCase(rankname2);
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rankname) != null) {
				List<String> list = (List<String>) rankYaml.getStringList("Groups." + rankname + ".economy.buyable");
				if (list.contains(rankname2)) {
					list.remove(rankname2);
				}
				rankYaml.set("Groups." + rankname + ".economy.buyable", (Object) list);
				rankYaml.save(rankFile);
				CachedRanks.update();
				this.m.updatePlayersWithRank(this, rankname);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<String> getBuyableRanks(String rank) {
		List<String> ranks = new ArrayList<String>();
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		try {
			rankYaml.load(rankFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		ranks = rankYaml.getStringList("Groups." + rank + ".economy.buyable");
		return ranks;
	}

	public boolean setBuyCost(String rankname, String cost) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		rankname = getRankIgnoreCase(rankname);
		if (!cost.chars().anyMatch(Character::isLetter)) {
			try {
				rankYaml.load(rankFile);
				if (rankYaml.get("Groups." + rankname) != null) {
					rankYaml.set("Groups." + rankname + ".economy.cost", Integer.parseInt(cost));
					rankYaml.save(rankFile);
					CachedRanks.update();
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public int getRankCost(String rankname) {
		File rankFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration rankYaml = new YamlConfiguration();
		int cost = 0;
		try {
			rankYaml.load(rankFile);
			if (rankYaml.get("Groups." + rankname) != null) {
				cost = rankYaml.getInt("Groups." + rankname + ".economy.cost");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cost;
	}

	public boolean addPlayerPermission(String target_player_name, String permission) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		if (permission.contains("/") || permission.contains(":")) {
			return false;
		}

		Player target_player = Bukkit.getServer().getPlayer(target_player_name);

		if (target_player != null) {
			try {
				playersYaml.load(playersFile);
				if (playersYaml.get("players." + target_player.getUniqueId()) != null) {
					List<String> list = (List<String>) playersYaml.getStringList("players." + target_player.getUniqueId() + ".permissions");
					if (!list.contains(permission)) {
						list.add(permission);
						playersYaml.set("players." + target_player.getUniqueId() + ".permissions", (Object) list);
					}
					playersYaml.save(playersFile);
					CachedPlayers.update();
					this.m.setupPermissions(target_player);

					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String uuid = "";
			try {
				playersYaml.load(playersFile);
				for (String key : playersYaml.getConfigurationSection("players").getKeys(false)) {
					if (playersYaml.getString("players." + key + ".name").equalsIgnoreCase(target_player_name)) {
						uuid = key;
					}
				}

				if (uuid.length() > 0) {
					if (playersYaml.get("players." + uuid) != null) {
						List<String> list = (List<String>) playersYaml.getStringList("players." + uuid + ".permissions");
						if (!list.contains(permission)) {
							list.add(permission);
							playersYaml.set("players." + uuid + ".permissions", (Object) list);
						}
						playersYaml.save(playersFile);
						CachedPlayers.update();
						return true;
					} else {
						return false;
					}
				} else
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean delPlayerPermission(String target_player_name, String permission) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		if (permission.contains("/") || permission.contains(":")) {
			return false;
		}

		Player target_player = Bukkit.getServer().getPlayer(target_player_name);

		if (target_player != null) {
			try {
				playersYaml.load(playersFile);
				if (playersYaml.get("players." + target_player.getUniqueId()) != null) {
					List<String> list = (List<String>) playersYaml.getStringList("players." + target_player.getUniqueId() + ".permissions");
					if (list.contains(permission)) {
						list.remove(permission);
						playersYaml.set("players." + target_player.getUniqueId() + ".permissions", (Object) list);
					}
					playersYaml.save(playersFile);
					CachedPlayers.update();
					this.m.setupPermissions(target_player);

					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String uuid = "";
			try {
				playersYaml.load(playersFile);
				for (String key : playersYaml.getConfigurationSection("players").getKeys(false)) {
					if (playersYaml.getString("players." + key + ".name").equalsIgnoreCase(target_player_name)) {
						uuid = key;
					}
				}

				if (uuid.length() > 0) {
					if (playersYaml.get("players." + uuid) != null) {
						List<String> list = (List<String>) playersYaml.getStringList("players." + uuid + ".permissions");
						if (list.contains(permission)) {
							list.remove(permission);
							playersYaml.set("players." + uuid + ".permissions", (Object) list);
						}
						playersYaml.save(playersFile);
						CachedPlayers.update();
						return true;
					} else {
						return false;
					}
				} else
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean addSubrank(String playername, String subrank) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return false;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);
			ArrayList<String> default_worlds = new ArrayList<String>();
			default_worlds.add("All");

			if (playersYaml.get("players." + uuid + ".subranks." + getRankIgnoreCase(subrank)) == null) {
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".use_prefix", true);
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".use_suffix", true);
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".use_permissions", true);
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".worlds", default_worlds);
			} else {
				return false;
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
			this.m.setupPermissions(player);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean removeSubrank(String playername, String subrank) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return false;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);

			if (playersYaml.get("players." + uuid + ".subranks." + getRankIgnoreCase(subrank)) != null) {
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank), null);
			} else {
				return false;
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
			this.m.setupPermissions(player);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public List<String> getSubranks(String playername) {
		List<String> ranks = new ArrayList<String>();
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return ranks;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);

			try {
				if (playersYaml.getConfigurationSection("players." + uuid + ".subranks") != null) {
					ConfigurationSection subranks = playersYaml.getConfigurationSection("players." + uuid + ".subranks");
					for (String r : subranks.getKeys(false)) {
						ranks.add(getRankIgnoreCase(r));
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		return ranks;
	}

	public boolean changeSubrankField(String playername, String subrank, String field, boolean value) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return false;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);

			if (playersYaml.get("players." + uuid + ".subranks." + getRankIgnoreCase(subrank)) != null) {
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + "." + field, value);
			} else {
				return false;
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public List<String> getPlayerPermissions(String playername) {
		List<String> list = new ArrayList<String>();

		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return list;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);

			if (playersYaml.get("players." + uuid + ".permissions") != null) {
				list = (List<String>) playersYaml.getStringList("players." + uuid + ".permissions");
			} else {
				return list;
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public String getSubrankprefixes(Player player) {
		String values = "";
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		YamlConfiguration ranksYaml = new YamlConfiguration();
		String uuid = player.getUniqueId().toString();
		try {
			playersYaml.load(playersFile);
			ranksYaml.load(ranksFile);

			if (playersYaml.get("players." + uuid + ".subranks") != null) {
				ConfigurationSection subranks = playersYaml.getConfigurationSection("players." + uuid + ".subranks");

				for (String r : subranks.getKeys(false)) {
					if (playersYaml.getBoolean("players." + uuid + ".subranks." + r + ".use_prefix")) {
						values += ChatColor.RESET
								+ (ranksYaml.getString("Groups." + r + ".chat.prefix") != null && ranksYaml.getString("Groups." + r + ".chat.prefix").length() > 0 ? ranksYaml.getString("Groups." + r + ".chat.prefix") + " " : "");
					}
				}
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}

	public String getSubranksuffixes(Player player) {
		String values = "";
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		YamlConfiguration ranksYaml = new YamlConfiguration();
		String uuid = player.getUniqueId().toString();
		try {
			playersYaml.load(playersFile);
			ranksYaml.load(ranksFile);

			if (playersYaml.get("players." + uuid + ".subranks") != null) {
				ConfigurationSection subranks = playersYaml.getConfigurationSection("players." + uuid + ".subranks");

				for (String r : subranks.getKeys(false)) {
					if (playersYaml.getBoolean("players." + uuid + ".subranks." + r + ".use_suffix")) {
						values += ChatColor.RESET
								+ (ranksYaml.getString("Groups." + r + ".chat.suffix") != null && ranksYaml.getString("Groups." + r + ".chat.suffix").length() > 0 ? ranksYaml.getString("Groups." + r + ".chat.suffix") + " " : "");
					}
				}
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return values;
	}

	public boolean createUserTag(String tag, String format) {
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration ranksYaml = new YamlConfiguration();

		try {
			ranksYaml.load(ranksFile);

			if (ranksYaml.isSet("Usertags")) {
				boolean tagExists = false;
				if (!ranksYaml.isString("Usertags")) {
					try {
						ConfigurationSection tags = ranksYaml.getConfigurationSection("Usertags");
						for (String key : tags.getKeys(false)) {
							if (key.equalsIgnoreCase(tag)) {
								tagExists = true;
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					ranksYaml.set("Usertags", null);
					ranksYaml.set("Usertags." + tag, format);
					ranksYaml.save(ranksFile);
					CachedRanks.update();
				}

				PowerRanks.log.info("tagExists: " + tagExists);

				if (!tagExists) {
					ranksYaml.set("Usertags." + tag, format);
					ranksYaml.save(ranksFile);
					CachedRanks.update();
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean editUserTag(String tag, String format) {
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration ranksYaml = new YamlConfiguration();

		try {
			ranksYaml.load(ranksFile);

			if (ranksYaml.isSet("Usertags")) {
				try {
					ConfigurationSection tags = ranksYaml.getConfigurationSection("Usertags");
					boolean tagExists = false;
					for (String key : tags.getKeys(false)) {
						if (key.equalsIgnoreCase(tag)) {
							tagExists = true;
							break;
						}
					}

					if (tagExists) {
						ranksYaml.set("Usertags." + tag, format);
						ranksYaml.save(ranksFile);
						CachedRanks.update();
						return true;
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeUserTag(String tag) {
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration ranksYaml = new YamlConfiguration();

		try {
			ranksYaml.load(ranksFile);

			if (ranksYaml.isSet("Usertags")) {
				try {
					ConfigurationSection tags = ranksYaml.getConfigurationSection("Usertags");
					boolean tagExists = false;
					for (String key : tags.getKeys(false)) {
						if (key.equalsIgnoreCase(tag)) {
							tagExists = true;
							break;
						}
					}

					if (tagExists) {
						ranksYaml.set("Usertags." + tag, null);
						ranksYaml.save(ranksFile);
						CachedRanks.update();
						return true;
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setUserTag(String playername, String tag) {
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration ranksYaml = new YamlConfiguration();
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return false;

		String uuid = player.getUniqueId().toString();

		try {
			ranksYaml.load(ranksFile);
			playersYaml.load(playersFile);

			if (ranksYaml.isSet("Usertags")) {
				try {
					ConfigurationSection tags = ranksYaml.getConfigurationSection("Usertags");
					boolean tagExists = false;
					for (String key : tags.getKeys(false)) {
						if (key.equalsIgnoreCase(tag)) {
							tagExists = true;
							break;
						}
					}

					if (tagExists) {
						playersYaml.set("players." + uuid + ".usertag", tag);
						playersYaml.save(playersFile);
						CachedPlayers.update();
						return true;
					}
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Set<String> getUserTags() {
		Set<String> tags = new HashSet<String>();
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration ranksYaml = new YamlConfiguration();

		try {
			ranksYaml.load(ranksFile);

			if (ranksYaml.getConfigurationSection("Usertags") != null) {
				try {
					ConfigurationSection tmp_tags = ranksYaml.getConfigurationSection("Usertags");
					tags = tmp_tags.getKeys(false);
				} catch (Exception e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tags;
	}

	public String getUserTagValue(String usertag) {
		String value = "";
		File ranksFile = new File(String.valueOf(PowerRanks.fileLoc) + "Ranks" + ".yml");
		YamlConfiguration ranksYaml = new YamlConfiguration();

		try {
			ranksYaml.load(ranksFile);

			if (ranksYaml.getConfigurationSection("Usertags") != null) {
				if (ranksYaml.isSet("Usertags." + usertag)) {
					value = ranksYaml.getString("Usertags." + usertag);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}
	
	public String getUserTagValue(Player player) {
		String usertag = CachedPlayers.getString("players." + player.getUniqueId() + ".usertag");
		if (usertag.length() > 0) {
			return getUserTagValue(usertag);
		} else {
			return "";
		}
	}

	public boolean clearUserTag(String playername) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return false;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);

			playersYaml.set("players." + uuid + ".usertag", "");
			playersYaml.save(playersFile);
			CachedPlayers.update();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setPromoteRank(String rank, String promote_rank) {
		return setRanksConfigFieldString(getRankIgnoreCase(rank), "level.promote", promote_rank);
	}

	public boolean setDemoteRank(String rank, String promote_rank) {
		return setRanksConfigFieldString(getRankIgnoreCase(rank), "level.demote", promote_rank);
	}

	public boolean clearPromoteRank(String rank) {
		return setRanksConfigFieldString(getRankIgnoreCase(rank), "level.promote", "");
	}

	public boolean clearDemoteRank(String rank) {
		return setRanksConfigFieldString(getRankIgnoreCase(rank), "level.demote", "");
	}

	public ArrayList<String> getPlayerNames() {
		ArrayList<String> player_names = new ArrayList<String>();

//		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
//		YamlConfiguration playersYaml = new YamlConfiguration();
//		try {
//			playersYaml.load(playersFile);
//			ConfigurationSection players_section = playersYaml.getConfigurationSection("players");
		ConfigurationSection players_section = CachedPlayers.getConfigurationSection("players");
		for (String key : players_section.getKeys(false)) {
//				player_names.add(playersYaml.getString("players." + key + ".name"));
			player_names.add(CachedPlayers.getString("players." + key + ".name"));
		}

//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return player_names;
	}

	public boolean addToSubrankList(String playername, String subrank, String string, String worldname) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return false;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);

			List<String> list = (List<String>) playersYaml.getStringList("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".worlds");
			if (!list.contains(worldname)) {
				list.add(worldname);
				if (list.contains("All")) {
					list.remove("All");
				}
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".worlds", list);
			} else {
				return false;
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean removeFromSubrankList(String playername, String subrank, String string, String worldname) {
		File playersFile = new File(String.valueOf(PowerRanks.fileLoc) + "Players" + ".yml");
		YamlConfiguration playersYaml = new YamlConfiguration();
		Player player = Bukkit.getServer().getPlayer(playername);
		if (player == null)
			return false;

		String uuid = player.getUniqueId().toString();

		try {
			playersYaml.load(playersFile);

			List<String> list = (List<String>) playersYaml.getStringList("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".worlds");
			if (list.contains(worldname)) {
				list.remove(worldname);
				if (list.size() == 0) {
					list.add("All");
				}
				playersYaml.set("players." + uuid + ".subranks." + getRankIgnoreCase(subrank) + ".worlds", list);
			} else {
				return false;
			}

			playersYaml.save(playersFile);
			CachedPlayers.update();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}