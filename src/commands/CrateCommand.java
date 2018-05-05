package commands;

import Crates.Crate;
import Crates.CrateManager;
import main.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import util.Util;

public class CrateCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("crate")) {
                if(args.length == 0) {
                    for(String s : main.getInstance().getConfig().getStringList("Messages.Help")) {
                        player.sendMessage(Util.color(s));
                    }
                    return false;
                }
                if (args[0].equalsIgnoreCase("give")) {
                    if (args.length <= 1) {
                        player.sendMessage(Util.color(main.getInstance().getConfig().getString("Messages.CorrectUsage")));
                        return false;
                    }
                    if (player.hasPermission(main.getInstance().getConfig().getString("Settings.GivePermission"))) {
                        if (args.length >= 4) {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                player.sendMessage(Util.color("&cInvalid player!"));
                                return false;
                            }
                            if (CrateManager.getCrate(args[2]) == null) {
                                player.sendMessage(Util.color("&cThis crate does not exist!"));
                                return false;

                            }

                            if (Util.isInt(args[3])) {
                                Crate crate = CrateManager.getCrate(args[2]);
                                ItemStack item = crate.giveCrate(Integer.parseInt(args[3]));
                                target.getInventory().addItem(item);

                            } else {
                                player.sendMessage("Incorrect!");
                            }

                        } else {
                            player.sendMessage(Util.color(main.getInstance().getConfig().getString("Messages.CorrectUsage")));
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission(main.getInstance().getConfig().getString("Settings.ReloadPermission"))) {
                        player.sendMessage(Util.color(main.getInstance().getConfig().getString("Messages.ConfigReloaded")));
                        main.getInstance().saveDefaultConfig();
                        main.getInstance().reloadConfig();
                        return false;
                    }
                }
            }

        }
        return false;
    }
}
