package de.kiyan.TreasureChest.Listener;

import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.TChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class EventBlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak( BlockBreakEvent event ) {
        Player player = event.getPlayer();
        if( TChest.tchestList == null ) return;
        for( TChest tChest : TChest.tchestList ) {
            if( tChest.getBackup().containsKey( event.getBlock() ) ) {
                event.setCancelled( true );
                player.sendMessage( Messages.BREAK_BLOCK.getMessage( true ) );
            }
        }
    }
}
