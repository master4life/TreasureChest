package de.kiyan.TreasureChest.Listener;

import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.handle.ChestSelection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventBlockInteract implements Listener
{
    @EventHandler
    public void BlockInteract( PlayerInteractEvent event )
    {
        Player player = event.getPlayer();
        ChestSelection tchest = new ChestSelection( player );

        if( tchest.getEditModer( player )
                && player.getItemInHand().getType().equals( Material.WOODEN_AXE ) ) {
            Action action = event.getAction();

            Block block = event.getClickedBlock();
            if( block.getType() == Material.AIR )
                return;

            if( action == Action.LEFT_CLICK_BLOCK )
            {
                Location first = block.getLocation();
                player.sendMessage( Messages.PREFIX.getMessage() + " §cPos1 : " + first );
                tchest.setFirstLocation( player, first );
            }

            if( action == Action.RIGHT_CLICK_BLOCK )
            {
                Location second = block.getLocation();
                player.sendMessage( Messages.PREFIX.getMessage() + " §cPos2 : " + second );
                tchest.setSecondLocation( player, second );
            }

            tchest.endSelection( player );
        }
    }
}
