package util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static String color(final String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> colorList(List<String> list) {
        List<String> lore = new ArrayList<String>();
        for(String s : list) {
            lore.add(color(s));
        }
        return lore;
    }

    public static boolean isInt(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static ItemStack createItem(String name, int data, Material material, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setDurability((short) data);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(String name, int data, Material material, List<String> lore, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setDurability((short) data);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(String name, int data, int amount, Material material, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setDurability((short) data);
        item.setItemMeta(meta);
        item.setAmount(amount);
        return item;
    }


}
