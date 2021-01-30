package de.kiyan.TreasureChest.handle;

import de.kiyan.TreasureChest.Config;
import de.kiyan.TreasureChest.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ChestSelection {

    public static HashMap<Player, Location> FIRST_SELECTION = new HashMap<>();
    public static HashMap<Player, Location> SECOND_SELECTION = new HashMap<>();
    public static HashMap<Player, String> EDIT_MODE = new HashMap<>();

    public ChestSelection(Player player) {
        if (!FIRST_SELECTION.containsKey(player))
            FIRST_SELECTION.put(player, null);

        if (!SECOND_SELECTION.containsKey(player))
            SECOND_SELECTION.put(player, null);
    }

    public HashMap<String, BlockData> getSelectedBlocks(Player player) {
        if (FIRST_SELECTION.get(player) == null
                && SECOND_SELECTION.get(player) == null) {
            player.sendMessage(Messages.TCHEST_SELECT_BEFORE.getMessage(true));
            return null;
        }

        World world = FIRST_SELECTION.get(player).getWorld();
        Location loc1 = FIRST_SELECTION.get(player);
        Location loc2 = SECOND_SELECTION.get(player);

        ArrayList<Block> blocks = new ArrayList<>();

        int topBlockX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int topBlockY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int topBlockZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int bottomBlockX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int bottomBlockY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int bottomBlockZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++)
            for (int z = bottomBlockZ; z <= topBlockZ; z++)
                for (int y = bottomBlockY; y <= topBlockY; y++)
                    blocks.add(world.getBlockAt(x, y, z));

        if (blocks.size() > 500) {
            removeEditMode(player);
            FIRST_SELECTION.remove(player);
            SECOND_SELECTION.remove(player);

            player.sendMessage(Messages.TOO_BIG.getMessage(true));

            return null;
        }

        HashMap<String, BlockData> blockString = new HashMap<>();

        Location location3 = getCenter(player, loc2, loc1);
        int j = location3.getBlockX();
        int k = location3.getBlockY();
        int m = location3.getBlockZ();
        for (Block bl : blocks) {
            if (!bl.getType().equals(Material.AIR)) {
                Location location = bl.getLocation();
                int n = location.getBlockX();
                int i1 = location.getBlockY();
                int i2 = location.getBlockZ();
                String stringX = " ";
                String stringY = " ";
                String stringZ = " ";
                if (n > j) {
                    stringX = "+" + (n - j);
                } else {
                    stringX = "-" + (j - n);
                }
                if (i1 > k) {
                    stringY = "+" + (i1 - k);
                } else {
                    stringY = "-" + (k - i1);
                }
                if (i2 > m) {
                    stringZ = "+" + (i2 - m);
                } else {
                    stringZ = "-" + (m - i2);
                }

                blockString.put(stringX + "_" + stringY + "_" + stringZ, bl.getBlockData());
            }
        }

        return blockString;
    }

    /*
    Getter and setter if the person is selecting or not.
     */

    public void removeEditMode(Player player ) {
        EDIT_MODE.remove(player);
    }

    public Boolean getEditModer(Player player) {
        return EDIT_MODE.containsKey(player);
    }

    /*
    Getter and setter of the players locations
    */
    public void setFirstLocation(Player player, Location loc) {
        FIRST_SELECTION.replace(player, loc);
    }

    public void setSecondLocation(Player player, Location loc) {
        SECOND_SELECTION.replace(player, loc);
    }

    public Location getFirstLocation(Player player) {
        return FIRST_SELECTION.get(player);
    }

    public Location getSecondLocation(Player player) {
        return SECOND_SELECTION.get(player);
    }

    public Location getCenter(Player player, Location loc1, Location loc2) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int x1 = Math.max(loc1.getBlockX(), loc2.getBlockX()) + 1;
        int z1 = Math.max(loc1.getBlockZ(), loc2.getBlockZ()) + 1;

        return new Location(player.getWorld(), minX + (x1 - minX) / 2.0D, minY + 1, minZ + (z1 - minZ) / 2.0D);
    }

    public void endSelection(Player player) {
        if (FIRST_SELECTION.get(player) != null && SECOND_SELECTION.get(player) != null) {
            ChestSelection chest = new ChestSelection(player);
            if (chest.getSelectedBlocks( player) != null) {
                Location loc1 = FIRST_SELECTION.get(player);
                Location loc2 = SECOND_SELECTION.get(player);

                ArrayList<Location> arrayLoc = new ArrayList<>();
                arrayLoc.add(loc1);
                arrayLoc.add(loc2);
                Config config = new Config();
                config.removeBlocks(EDIT_MODE.get(player));
                config.setBlocks(EDIT_MODE.get(player), chest.getSelectedBlocks(player));
                new ChestMenu().setupNewMenu(player, EDIT_MODE.get(player)).openMenu(player);

                EDIT_MODE.remove(player);
                FIRST_SELECTION.remove(player);
                SECOND_SELECTION.remove(player);
            }
        }
    }
}
