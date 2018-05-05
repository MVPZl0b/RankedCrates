package events;

import main.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import util.Util;

public class RewardEvent implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isReward(event.getItem())) {
                event.setCancelled(true);
                executeReward(event.getPlayer(), getReward(event.getItem()));
                if (event.getPlayer().getItemInHand().getAmount() == 1) {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));

                } else {
                    event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                }
            }
        }
    }


    public String getReward(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return null;
        ConfigurationSection rewards = main.getInstance().getConfig().getConfigurationSection("Rewards");

        for (String string : rewards.getKeys(false)) {
            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(Util.color(rewards.getString(string + ".Name"))))
                return string;
        }

        return null;
    }

    public boolean isReward(ItemStack item) {
        return getReward(item) != null;
    }

    public void executeReward(Player player, String reward) {
        for (String s : main.getInstance().getConfig().getStringList("Rewards." + reward + ".Commands")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("{name}", player.getName()));
        }
    }

}
