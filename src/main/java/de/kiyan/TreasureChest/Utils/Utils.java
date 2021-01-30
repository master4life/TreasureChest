package de.kiyan.TreasureChest.Utils;

import de.kiyan.TreasureChest.Main;
import de.kiyan.TreasureChest.TChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Utils {
    public static int RandInt(int from, int to) {
        return from + (int) (Math.random() * (to - from + 1));
    }

    public static Location getBlockCenterUP(Location location) {
        if (location != null)
            return new Location(location.getWorld(), location.getX() + 0.5D, location.getY() + 1.0D, location.getZ() + 0.5D);
        return location;
    }

    public static Location getBlockCenter(Location location) {
        if (location != null)
            return new Location(location.getWorld(), location.getX() + 0.5D, location.getY(), location.getZ() + 0.5D);
        return location;
    }

    public static FallingBlock spawnFallingBlock(TChest tChest, Location location, BlockData data) {
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, data);
        fallingBlock.setDropItem(false);
        fallingBlock.setGravity(false);
        fallingBlock.setVelocity(new Vector(0, 0, 0));
        Shulker box = (Shulker) location.getWorld().spawnEntity(location.add(0, 0, 0), EntityType.SHULKER);
        box.setAI(false);
        box.setGravity(false);
        box.setInvisible(true);
        box.setSilent(true);
        box.setCollidable(false);
        box.setInvulnerable(true);
        box.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999, 255));
        box.setMetadata("TChest", new FixedMetadataValue(Main.getInstance(), tChest.getP().getName()));
        return fallingBlock;
    }

    public static void spawnChest(TChest tChest, Location location, BlockData data) {
        MagmaCube box = (MagmaCube) location.getWorld().spawnEntity(location.add( 0.5, 0, 0.5), EntityType.MAGMA_CUBE);
        box.setSize(3);
        box.setAI(false);
        box.setInvisible(true);
        box.setSilent(true);
        box.setCollidable(false);
        box.setInvulnerable(true);
        box.setGravity(false);
        box.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999, 255));
        box.setMetadata("TChest", new FixedMetadataValue(Main.getInstance(), tChest.getP().getName()));
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> Bukkit.getOnlinePlayers().forEach( (all) -> all.sendBlockChange(location, data) ), 2L);
    }
}
