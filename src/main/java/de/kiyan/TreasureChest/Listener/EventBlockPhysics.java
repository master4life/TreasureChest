package de.kiyan.TreasureChest.Listener;

import de.kiyan.TreasureChest.TChest;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class EventBlockPhysics implements Listener {
    @EventHandler
    public void onBlockChange( BlockPhysicsEvent event ) {
        if( TChest.tchestList == null )
            return;

        Block source = event.getSourceBlock();
        for( TChest tChest : TChest.tchestList ) {
            if( tChest.getBackup().containsKey( source ) ) {
                event.setCancelled( true );
            }
        }
    }
}
