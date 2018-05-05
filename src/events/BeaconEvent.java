package events;

import Crates.Crate;
import Crates.CrateManager;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import main.main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BeaconEvent implements Listener {

    private static HashMap<Location, Crate> locations = new HashMap<>();
    private static HashMap<Location, Material> blocks = new HashMap<>();

    public boolean active;

    @EventHandler
    public void beaconPlace(BlockPlaceEvent event) {
        FLocation FLocation = new FLocation(event.getBlockPlaced().getLocation());

        Faction faction = Board.getInstance().getFactionAt(FLocation);

        if (event.getBlockPlaced().getType().equals(event.getItemInHand().getType()) && CrateManager.isCrate(event.getItemInHand())) {
            if (!faction.equals(Factions.getInstance().getWarZone())) {
                event.getPlayer().sendMessage(Util.color(main.getInstance().getConfig().getString("Messages.NotInWarzone")));
                event.setCancelled(true);
                return;
            }
            if (active) {
                event.getPlayer().sendMessage(Util.color(main.getInstance().getConfig().getString("Messages.AlreadyActiveCrate")));
                event.setCancelled(true);
                return;
            }
            Crate crate = CrateManager.getCrate(event.getItemInHand());
            Location placed = event.getBlockPlaced().getLocation();
            Location location = event.getBlockPlaced().getLocation();
            place(event, placed, crate);
            event.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Bukkit.getServer().getWorld(event.getBlockPlaced().getWorld().getName()).getBlockAt(placed).setType(Material.matchMaterial(crate.getMaterial()));
                }
            }, 1);
            if (event.getPlayer().getItemInHand().getAmount() == 1) {
                event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                return;
            }
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
        }
    }

    public void place(BlockPlaceEvent event, Location location, Crate crate) {
        active = true;
        List<String> worlds = main.getInstance().getConfig().getStringList("Settings.AllowedWorlds");

        if (!worlds.contains(event.getBlockPlaced().getWorld().getName())) return;

        Location placed = location;
        locations.put(event.getBlock().getLocation(), crate);
        Bukkit.getServer().broadcastMessage(Util.color(main.getInstance().getConfig().getString("Messages.CrateAnnouncement").replace("{x}", "" + placed.getX()
        ).replace("{y}", "" + placed.getY()).replace("{z}", "" + placed.getZ()).replace("{type}", crate.getName())));
        Location blockLocation = event.getBlock().getLocation();
        Location standLocation = new Location(event.getBlockPlaced().getWorld(), event.getBlock().getX() + 0.5, event.getBlockPlaced().getY() + 0.25, event.getBlockPlaced().getZ() + 0.5);
        ArmorStand stand = location.getWorld().spawn(standLocation, ArmorStand.class);
        stand.setVisible(false);
        stand.setSmall(true);
        stand.setCustomName(Util.color(crate.getDisplayName()));
        stand.setGravity(false);
        stand.setCustomNameVisible(true);
        if (main.getInstance().getConfig().getBoolean("Settings.BeaconBeam") && event.getBlockPlaced().getType().equals(Material.BEACON)) {

            Location corner1 = blockLocation.clone().add(-1, -1, -1);
            Location corner2 = blockLocation.clone().add(+1, -1, +1);

            int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
            int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
            int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

            int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
            int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = blockLocation.getWorld().getBlockAt(x, maxY, z);
                    blocks.put(block.getLocation(), block.getType());
                    block.setType(Material.IRON_BLOCK);
                }
            }
        }


        Bukkit.getScheduler().scheduleSyncDelayedTask(main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (main.getInstance().getConfig().getBoolean("Settings.BeaconBeam") && event.getBlockPlaced().getType().equals(Material.BEACON)) {
                    for (Location loc : blocks.keySet()) {
                        blockLocation.getWorld().getBlockAt(loc).setType(blocks.get(loc));
                    }
                }
                event.getBlockPlaced().setType(Material.CHEST);
                Bukkit.getServer().broadcastMessage(Util.color(main.getInstance().getConfig().getString("Messages.ChestSpawned").replace("{type}", crate.getName())));
                blocks.clear();
                active = false;
            }
        }, 20 * main.getInstance().getConfig().getInt("Settings.DefendTime"));


    }

    @EventHandler
    public void beaconBreak(PlayerInteractEvent event) {

        if (event.getClickedBlock() == null) return;
        boolean isCrate = locations.containsKey(event.getClickedBlock().getLocation());

        Crate crate = locations.get(event.getClickedBlock().getLocation());

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && isCrate && event.getClickedBlock().getType().equals(Material.matchMaterial(crate.getMaterial()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Util.color(main.getInstance().getConfig().getString("Messages.CannotBreakCrate")));
        }

        if (isCrate && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType().equals(Material.CHEST)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Util.color(main.getInstance().getConfig().getString("Messages.BreakTheChest")));
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && isCrate && event.getClickedBlock().getType().equals(Material.CHEST)) {
            FileConfiguration config = main.getInstance().getConfig();

            Location location = event.getClickedBlock().getLocation();

            event.getClickedBlock().getLocation().getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()).setType(Material.AIR);
            location.getWorld().playSound(location, Sound.NOTE_PLING, 0.5F, 0.5F);

            Bukkit.getServer().broadcastMessage(Util.color(main.getInstance().getConfig().getString("Messages.BrokenCrate").replace("{type}", crate.getName())));
            /*if (!main.getInstance().getConfig().getBoolean("Settings.RandomReward")) {
                for (String reward : crate.getRewards()) {
                    ItemStack item = Util.createItem(Util.color(config.getString("Rewards." + reward + ".Name")), 0, Material.matchMaterial(config.getString("Rewards." + reward + ".Material"))
                            , Util.colorList(config.getStringList("Rewards." + reward + ".Lore")), 1);
                    event.getClickedBlock().getLocation().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), item);
                }
            } else {*/
            int amount = main.getInstance().getConfig().getInt("Crates." + crate + ".AmountOfRewards");
            for (int i = 0; i < amount; i++) {
                String reward = crate.getRewards().get(new Random().nextInt(crate.getRewards().size()));
                ItemStack item = Util.createItem(Util.color(config.getString("Rewards." + reward + ".Name")), 0, Material.matchMaterial(config.getString("Rewards." + reward + ".Material"))
                        , Util.colorList(config.getStringList("Rewards." + reward + ".Lore")), 1);
                event.getClickedBlock().getLocation().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), item);
            }
            List<Entity> nearbyEntities = (List<Entity>) location.getWorld().getNearbyEntities(location, 1, 1, 1);

            for (Entity e : location.getWorld().getEntities()) {
                if (nearbyEntities.contains(e)) {
                    if (e.getType() == EntityType.ARMOR_STAND) {
                        e.remove();
                    }
                }
            }

            locations.remove(event.getClickedBlock().getLocation());
        }
    }
}
