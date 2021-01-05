package de.kiyan.TreasureChest.Runnable;

import de.kiyan.TreasureChest.Utils.Effects;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FlyingItems extends BukkitRunnable
{
    private Location location;
    private Location frontLocation;
    private double highest;
    private String text;
    private ItemStack itemstack;
    private Item item;

    public FlyingItems( ItemStack is, Player player, double height, Plugin plugin )
    {
        this.location = player.getLocation();
        Vector direction = player.getLocation().getDirection();
        Location front = player.getLocation().add(direction);
        front.setY( front.getY() + 0.75 );

        this.frontLocation = front;
        this.itemstack = is;
        this.text = is.getItemMeta().getDisplayName();
        this.highest = height;
        this.item = spawn();
        runTaskTimer( plugin, 1L, 2L );
    }

    public void run()
    {
        if( item.getLocation().getY() >= getLocation().getY() + getHighest())
        {
            cancel();
            item.remove();
            new Effects().coneEffect( item.getLocation(), Particle.CLOUD );
        }
        item.setVelocity( new Vector( 0.0, 0.2, 0.0) );
    }

    public Item spawn() {
        Item i = getLocation().getWorld().dropItem( frontLocation, this.itemstack );
        i.setPickupDelay( 2147483647 );
        if( this.text != null ) {
            i.setCustomName( this.text.split( "§5§l")[0].replace( ": ", "" ) );
            i.setCustomNameVisible( true );
        }
        return i;
    }

    public Location getLocation() {
        return this.location;
    }

    public double getHighest() {
        return this.highest;
    }
}
