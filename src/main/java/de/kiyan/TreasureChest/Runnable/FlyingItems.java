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
    final private Location location;
    final private Location frontLocation;
    final private String text;
    final private ItemStack itemstack;
    final private Item item;
    private Integer height;

    public FlyingItems( ItemStack is, Player player, int height, Plugin plugin )
    {
        this.location = player.getLocation();
        Vector direction = player.getLocation().getDirection();
        Location front = player.getLocation().add(direction);
        front.setY( front.getY() + 0.75 );

        this.frontLocation = front;
        this.itemstack = is;
        this.text = is.getItemMeta().getDisplayName();
        this.item = spawn();
        this.height = ( height * 10 );
        runTaskTimer( plugin, 1L, 2L );
    }

    public void run()
    {
        if( getHighest() <= 0 )
        {
            cancel();
            item.remove();
            new Effects().coneEffect( item.getLocation(), Particle.CLOUD );
        }
        item.setVelocity( new Vector( 0.0, 0.2, 0.0) );
        this.height--;
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
        return this.height;
    }
}
