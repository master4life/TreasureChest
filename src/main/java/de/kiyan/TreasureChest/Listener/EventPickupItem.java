package de.kiyan.TreasureChest.Listener;

import de.kiyan.TreasureChest.TChest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class EventPickupItem implements Listener {
    @EventHandler
    public void onPlayerPickUpItem( PlayerPickupItemEvent event ) {
        Player player = event.getPlayer();
        if( TChest.tchestList == null ) return;
        Item item = event.getItem();
        for( TChest tChest : TChest.tchestList ) {
            if( tChest.getDrops() != null && !tChest.getP().equals(player) && !tChest.getState().equals(TChest.TChestState.END) &&
                    tChest.getDrops().contains( item ) )
                event.setCancelled(true);
        }
    }
}
