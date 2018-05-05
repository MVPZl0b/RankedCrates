package Crates;

import main.main;
import org.bukkit.inventory.ItemStack;
import util.Util;

import java.util.ArrayList;
import java.util.List;

public class CrateManager {

    public static List<Crate> crates = new ArrayList<Crate>();

    public static Crate getCrate(String name) {

        for (Crate c : crates) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public static void loadCrates() {
        for (String crate : main.getInstance().getConfig().getConfigurationSection("Crates").getKeys(false)) {
            Crate ezCrate = new Crate(crate, getCrateDisplay(crate), getCrateMaterial(crate), getLore(crate));
            for(String reward : main.getInstance().getConfig().getStringList("Crates." + crate + ".Rewards")) {
                int chance = main.getInstance().getConfig().getInt("Rewards." +reward + ".Chance");
                for(int i = 0; i < chance; i++) {
                    ezCrate.getRewards().add(reward);
                }
            }
            crates.add(ezCrate);
        }

    }

    public static String getCrateDisplay(String name) {
        return main.getInstance().getConfig().getString("Crates." + name + ".Name");
    }

    public static String getCrateMaterial(String name) {
        return main.getInstance().getConfig().getString("Crates." + name + ".Material");
    }

    public static List<String> getLore(String name) {
        return main.getInstance().getConfig().getStringList("Crates." + name + ".Lore");
    }

    public static Boolean isCrate(ItemStack item) {
            return getCrate(item) != null;
    }
    public static Crate getCrate(ItemStack item) {
        for(Crate c :crates) {
            if(Util.color(c.getDisplayName()).equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                return c;
            }
        }
        return null;
    }
}
