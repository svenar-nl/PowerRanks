package nl.svenar.PowerRanks.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.svenar.PowerRanks.PowerRanks;
import nl.svenar.PowerRanks.Data.Messages;
import nl.svenar.PowerRanks.Data.Users;

public class cmd_enablesubranksuffix extends PowerCommand {

	private Users users;

	public cmd_enablesubranksuffix(PowerRanks plugin, String command_name, COMMAND_EXECUTOR ce) {
		super(plugin, command_name, ce);
		this.users = new Users(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender.hasPermission("powerranks.cmd.enablesubranksuffix")) {
			if (args.length == 2) {
				final String playername = args[0];
				final String subrank = this.users.getRankIgnoreCase(args[1]);
				final boolean result = this.users.changeSubrankField(playername, subrank, "use_suffix", true);
				if (result) {
					Messages.messageSuccessChangesubrank(sender, subrank, playername);
				} else {
					Messages.messageErrorChangesubrank(sender, subrank, playername);
				}
			} else {
				Messages.messageCommandUsageEnablesubranksuffix(sender);
			}
		} else {
			Messages.noPermission(sender);
		}

		return false;
	}

	public ArrayList<String> tabCompleteEvent(CommandSender sender, String[] args) {
		ArrayList<String> tabcomplete = new ArrayList<String>();

		if (args.length == 1) {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				tabcomplete.add(player.getName());
			}
		}

		if (args.length == 2) {
			for (String rank : this.users.getSubranks(args[0])) {
				if (rank.toLowerCase().contains(args[1].toLowerCase())) {
					tabcomplete.add(rank);
				}
			}
		}

		return tabcomplete;
	}
}
